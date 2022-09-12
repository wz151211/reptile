package com.ping.reptile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.XMLTokener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.mapper.ConfigMapper;
import com.ping.reptile.mapper.PunishMapper;
import com.ping.reptile.model.entity.ConfigEntity;
import com.ping.reptile.model.entity.PunishEntity;
import com.ping.reptile.model.vo.Dict;
import com.ping.reptile.model.vo.Pair;
import com.ping.reptile.model.vo.Param;
import com.ping.reptile.model.vo.Result;
import com.ping.reptile.utils.IpUtils;
import com.ping.reptile.utils.ParamsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.hutool.json.XMLTokener.entity;

/**
 * @author: W.Z
 * @date: 2022/8/26 17:20
 * @desc:
 */
@Service
@Slf4j
public class CasePunishService {
    @Autowired
    private PunishMapper punishMapper;
    @Autowired
    private CustomProperties properties;
    @Autowired
    private ConfigMapper configMapper;

    private ConfigEntity config = null;

    private List<Dict> areas = new ArrayList<>();
    private List<Dict> types = new ArrayList<>();
    private List<Dict> themes = new ArrayList<>();
    private AtomicInteger days = new AtomicInteger(0);

    private LocalDate date = null;
    private String[] years = {"2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014"};

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            30,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:dict/*.txt");
            for (Resource resource : resources) {
                if ("area.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    areas.addAll(JSON.parseArray(text, Dict.class));
                }

                if ("type.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    types.addAll(JSON.parseArray(text, Dict.class));
                }

                if ("theme.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    themes.addAll(JSON.parseArray(text, Dict.class));
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public void page(Integer pageNum, Integer pageSize) {
        if (config == null) {
            config = configMapper.selectById(properties.getId());
        }
        if (pageNum == null) {
            pageNum = config.getPageNum();
        }
        if (pageSize == null) {
            pageSize = config.getPageSize();
        }
        if (date == null) {
            date = LocalDate.parse(config.getPunishDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
        List<PunishEntity> caseNoList = punishMapper.getCaseNoList();
        for (PunishEntity entity : caseNoList) {
            Map<String, Object> params = new HashMap<>();
            params.put("pageSize", pageSize);
            params.put("pageNum", pageNum);
            params.put("sortFields", "23_s:desc,16_s:asc");
            params.put("ciphertext", ParamsUtils.cipher());
            Param punishDate = new Param();
            String start = date.plusDays(days.get()).format(DateTimeFormatter.ISO_LOCAL_DATE);
            String end = date.plusDays(days.get()).format(DateTimeFormatter.ISO_LOCAL_DATE);

            punishDate.setId(start + "," + end);
            punishDate.setKey("23_s");

//            Param full = new Param();
//            full.setId(ParamsUtils.getCaseNo(entity.getCaseNo()));
//            full.setKey("51_s");
//            full.setName("全文检索：" + full.getId());

            Param unit = new Param();
            unit.setId(entity.getPunishUnit().trim());
            unit.setKey("14_s,52_s");

      /*      Param y = new Param();
            y.setId("2022");
            y.setKey("24_i");*/
            String param = JSON.toJSONString(Lists.newArrayList(unit, punishDate));
            log.info("列表请求参数 {}", param);
            params.put("queryCondition", param);
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    list(params);
                }
            });
        }
        //  list(params, t.getName(), th.getName(), a.getCode(), a.getName());
        days.getAndIncrement();
        page(pageNum, pageSize);
    }


    public void list(Map<String, Object> params) {
        String url = "https://cfws.samr.gov.cn/queryDoc";
        HttpResponse response = null;
        try {

            response = HttpRequest.post(url)
                    .form(params)
                    .timeout(-1)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("X-Real-IP", IpUtils.getIp())
                    .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Host", "cfws.samr.gov.cn")
                    .header("Origin", "https://cfws.samr.gov.cn")
                    .header("Referer", "https://cfws.samr.gov.cn/list.html?49_ss=01")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                    .header("sec-ch-ua", "\"Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"104\"")
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "\"Windows\"")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();
        } catch (Exception e) {
            log.info("查询列表出错", e);
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            Result result = JSON.parseObject(response.body(), Result.class);
            if (result.getCode() == -12) {
                log.info("已经被限制访问");
                return;
            }
            log.info("code={},desc={}", result.getCode(), result.getDescription());
            log.info(result.getSecretKey());
            if (result.getSuccess()) {
                JSONObject object = JSON.parseObject(result.getResult());
                JSONArray jsonArray = object.getJSONObject("queryResult").getJSONArray("resultList");
                log.info("列表数量={}", jsonArray.size());

                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String docId = obj.getString("rowkey");
                    Long count = punishMapper.selectCount(Wrappers.<PunishEntity>lambdaQuery().eq(PunishEntity::getId, docId));
                    if (count > 0) {
                        continue;
                    }

                    PunishEntity entity = new PunishEntity();
                    entity.setId(docId);
                    entity.setCaseNo(obj.getString("2"));
                    entity.setName(obj.getString("30"));
                    entity.setPunishUnit(obj.getString("14"));
                    entity.setPunishDate(DateUtil.parse(obj.getString("23"), "yyyyMMdd").toJdkDate());
                    entity.setResult(obj.getString("7"));
                    detail(docId, entity);
                }
                if (jsonArray.size() > 0) {
                    Integer pageNum = (Integer) params.get("pageNum");
                    params.put("pageNum", pageNum + 1);
                    list(params);
                    TimeUnit.SECONDS.sleep(2);
                }
            }

        } catch (Exception e) {
            if (response != null) {
                log.error("body={}", response.body());
            }
            log.error("列表获取出错", e);
            try {
                TimeUnit.MINUTES.sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


    public void detail(String docId, PunishEntity entity) {
        String url = "https://cfws.samr.gov.cn/getDoc";
        HttpResponse response = null;
        PDDocument document = null;
        try {
            TimeUnit.SECONDS.sleep(2);

            Map<String, Object> params = new HashMap<>();
            params.put("ciphertext", ParamsUtils.cipher());
            params.put("docid", docId);
            response = HttpRequest.post(url)
                    .form(params)
                    .timeout(-1)
                    .header("Accept", "*/*")
                    .header("X-Real-IP", IpUtils.getIp())
                    .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Host", "cfws.samr.gov.cn")
                    .header("Origin", "https://cfws.samr.gov.cn")
                    .header("Referer", "https://cfws.samr.gov.cn/detail.html?docid=" + docId)
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                    .header("sec-ch-ua", "\"Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"104\"")
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "\"Windows\"")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();
            Result result = JSON.parseObject(response.body(), Result.class);
            JSONObject object = JSON.parseObject(result.getResult());
            document = PDDocument.load(Base64.getDecoder().decode(object.getString("i7").getBytes(StandardCharsets.UTF_8)));
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(document);
            entity.setContent(text);
            entity.setBasis(object.getString("i5"));
            entity.setType(object.getString("i4"));
            log.info("当事人名称={}", entity.getName());
            punishMapper.insert(entity);
        } catch (Exception e) {
            log.error("获取详情出错", e);
            try {
                TimeUnit.MINUTES.sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (document != null) {
                    document.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
