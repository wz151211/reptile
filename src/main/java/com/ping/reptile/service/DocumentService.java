package com.ping.reptile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.mapper.ConfigMapper;
import com.ping.reptile.mapper.DocumentMapper;
import com.ping.reptile.model.entity.ConfigEntity;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.model.vo.Dict;
import com.ping.reptile.model.vo.Pair;
import com.ping.reptile.model.vo.Result;
import com.ping.reptile.utils.IpUtils;
import com.ping.reptile.utils.ParamsUtils;
import com.ping.reptile.utils.TripleDES;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 10:39
 */
@Slf4j
@Service
public class DocumentService {
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private CustomProperties properties;
    @Autowired
    private ConfigMapper configMapper;

    private ConfigEntity config = null;

    private AtomicInteger days = new AtomicInteger(0);
    private List<Dict> areas = new ArrayList<>();
    private List<Dict> docTypes = new ArrayList<>();
    private Map<String, String> docTypeMap = new HashMap<>();
    private LocalDate date = null;
    private int min = 3;
    private int max = 10;

    private List<ConfigEntity> configList = null;
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
            Resource[] resources = resolver.getResources("classpath:doc/*.txt");
            for (Resource resource : resources) {
                if ("area.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    areas.addAll(JSON.parseArray(text, Dict.class));
                }
                if ("docType.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    docTypes.addAll(JSON.parseArray(text, Dict.class));
                    for (Dict docType : docTypes) {
                        docTypeMap.put(docType.getCode(), docType.getName());
                    }
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
        configList = configMapper.selectList(Wrappers.<ConfigEntity>lambdaQuery().eq(ConfigEntity::getEnable, properties.getEnable()));
        if (pageNum == null) {
            pageNum = config.getPageNum();
        }
        if (pageSize == null) {
            pageSize = config.getPageSize();
        }
        if (date == null) {
            date = LocalDate.parse(config.getDocDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (date.minusDays(days.get()).getYear() < 1990) {
            return;
        }
        log.info("开始查询日期为[{}]下的数据", date.minusDays(days.get()).format(DateTimeFormatter.ISO_LOCAL_DATE));
        list(pageNum, pageSize);
        days.getAndAdd(properties.getIntervalDays());
        try {
            TimeUnit.SECONDS.sleep(RandomUtil.randomInt(min, max));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        page(pageNum, pageSize);
    }

    public void list(Integer pageNum, Integer pageSize) {
        log.info("pageNum = {}", pageNum);
        String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
        String pageId = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> params = new HashMap<>();
        String start = date.minusDays(days.get() + properties.getIntervalDays()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String end = date.minusDays(days.get()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        params.put("pageId", pageId);
        // params.put("s8", "02");
        params.put("s21", "家庭暴力");
        params.put("sortFields", "s51:desc");
        params.put("ciphertext", ParamsUtils.cipher());
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        Pair datePair = new Pair();
        datePair.setKey("cprq");
        datePair.setValue(start + " TO " + end);

        Pair name = new Pair();
        name.setKey("s1");
        name.setValue("盗窃");

        Pair caseType = new Pair();
        caseType.setKey("s8");
        caseType.setValue("02");

        Pair docType = new Pair();
        docType.setKey("s6");
        docType.setValue("01");

        Pair trial = new Pair();
        trial.setKey("s9");
        trial.setValue("0201");


        String pairs = JSON.toJSONString(Lists.newArrayList(datePair, name, caseType, docType, trial));

/*        Pair name = new Pair();
        name.setKey("s45");
        name.setValue("家庭暴力");

*//*        Pair caseType = new Pair();
        caseType.setKey("s8");
        caseType.setValue("02");*//*

        Pair docType = new Pair();
        docType.setKey("s6");
        docType.setValue("01");
        String pairs = JSON.toJSONString(Lists.newArrayList(datePair, name, docType));*/

        log.info("参数={}", pairs);
        params.put("queryCondition", pairs);
        params.put("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@queryDoc");
        params.put("__RequestVerificationToken", ParamsUtils.random(24));
        params.put("wh", 699);
        params.put("ww", 1280);

        ConfigEntity entity = configList.get(RandomUtil.randomInt(0, configList.size()));
        log.info("config:{}", entity);

        HttpCookie cookie = new HttpCookie("SESSION", entity.getToken());
        cookie.setDomain("wenshu.court.gov.cn");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        HttpResponse response = null;
        try {
            response = HttpRequest.post(url)
                    .form(params)
                    .timeout(-1)
                    .cookie(cookie)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("X-Real-IP", IpUtils.getIp())
                    .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Cache-Control", "no-cache")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Host", "wenshu.court.gov.cn")
                    .header("Origin", "https://wenshu.court.gov.cn")
                    .header("Pragma", "no-cache")
                    .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?pageId=" + pageId + "&s8=02")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", entity.getAgent())
                    //    .header("sec-ch-ua", entity.getChua())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();

        } catch (Exception e) {
            log.error("发送列表请求出错", e);
            try {
                TimeUnit.MINUTES.sleep(RandomUtil.randomInt(min, max));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            Result result = JSON.parseObject(response.body(), Result.class);
            if (result.getCode() == 9) {
                config = configMapper.selectById(RandomUtil.randomInt(0, configList.size()));
                log.info("Session已过期");
                return;
            }
            log.info("code={},desc={}", result.getCode(), result.getDescription());
            log.info(result.getSecretKey());
            if (result.getSuccess()) {
                String iv = DateUtil.format(new Date(), "yyyyMMdd");
                String decrypt = TripleDES.decrypt(result.getSecretKey(), result.getResult(), iv);
                JSONObject object = JSON.parseObject(decrypt);
                JSONArray jsonArray = object.getJSONObject("queryResult").getJSONArray("resultList");
                log.info("列表数量={}", jsonArray.size());
                if (jsonArray.size() == 0) {
                    TimeUnit.SECONDS.sleep(RandomUtil.randomInt(min, max));
                    return;
                }
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String docId = obj.getString("rowkey");
                    detail(docId);
                }
            }
            list(pageNum + 1, pageSize);
        } catch (Exception e) {
            try {
                if (response != null) {
                    log.error("body={}", response.body());
                    if (response.body().contains("307 Temporary Redirec")) {
                        TimeUnit.MINUTES.sleep(RandomUtil.randomInt(min, max));
                    }
                }
                log.error("列表获取出错", e);

                TimeUnit.SECONDS.sleep(RandomUtil.randomInt(min, max));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void detail(String docId) {
        HttpResponse response = null;
        try {
            TimeUnit.SECONDS.sleep(RandomUtil.randomInt(min, max));
            String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
            Map<String, Object> params = new HashMap<>();
            params.put("docId", docId);
            params.put("ciphertext", ParamsUtils.cipher());
            params.put("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@docInfoSearch");
            params.put("__RequestVerificationToken", ParamsUtils.random(24));
            params.put("wh", 425);
            params.put("ww", 1680);
            params.put("cs", 0);
            ConfigEntity configEntity = configList.get(RandomUtil.randomInt(0, configList.size()));
            log.info("config:{}", configEntity);
            HttpCookie cookie = new HttpCookie("SESSION", configEntity.getToken());
            cookie.setDomain("wenshu.court.gov.cn");
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            response = HttpRequest.post(url)
                    .form(params)
                    .timeout(-1)
                    .cookie(cookie)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("X-Real-IP", IpUtils.getIp())
                    .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Host", "wenshu.court.gov.cn")
                    .header("Origin", "https://wenshu.court.gov.cn")
                    .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181107ANFZ0BXSK4/index.html?docId=" + docId)
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", configEntity.getAgent())
                    //      .header("sec-ch-ua", configEntity.getChua())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();
            Result result = JSON.parseObject(response.body(), Result.class);
            log.info("detail--", result.getSecretKey());
            log.info("detail--code={},desc={}", result.getCode(), result.getDescription());
            if (result.getSuccess()) {
                String iv = DateUtil.format(new Date(), "yyyyMMdd");
                String decrypt = TripleDES.decrypt(result.getSecretKey(), result.getResult(), iv);
                JSONObject jsonObject = JSON.parseObject(decrypt);
                String id = jsonObject.getString("s5");
                Long count = documentMapper.selectCount(Wrappers.<DocumentEntity>lambdaQuery().eq(DocumentEntity::getId, id));
                if (count > 0) {
                    return;
                }
                String name = jsonObject.getString("s1");
                String caseNo = jsonObject.getString("s7");
                String courtName = jsonObject.getString("s2");
                String refereeDate = jsonObject.getString("s31");
                String caseType = jsonObject.getString("s8");
                String trialProceedings = jsonObject.getString("s9");
                String docType = jsonObject.getString("s6");
                JSONArray causes = jsonObject.getJSONArray("s11");
                String cause = causes.stream().map(Object::toString).collect(joining(","));
                String htmlContent = jsonObject.getString("qwContent");
                jsonObject.remove("qwContent");
                String jsonContent = jsonObject.toJSONString();
                log.info("案件名称={}", name);
                DocumentEntity entity = new DocumentEntity();
                entity.setId(id);
                entity.setName(name);
                entity.setCaseNo(caseNo);
                entity.setCourtName(courtName);
                entity.setRefereeDate(refereeDate);
                entity.setCaseType(caseType);
                entity.setCause(cause);
                entity.setTrialProceedings(trialProceedings);
                entity.setDocType(docTypeMap.get(docType));
                entity.setJsonContent(jsonContent);
                entity.setHtmlContent(htmlContent);
                entity.setCreateTime(new Date());
                documentMapper.insert(entity);
            }
        } catch (Exception e) {
            try {
                if (response != null) {
                    log.error("body={}", response.body());
                    if (response.body().contains("307 Temporary Redirect")) {
                        TimeUnit.MINUTES.sleep(RandomUtil.randomInt(min, max));
                    }
                }
                log.error("详情获取出错", e);
                TimeUnit.SECONDS.sleep(RandomUtil.randomInt(min, max));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }


    public List<Dict> getCourt(String code) {
        String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
        String pageId = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> params = new HashMap<>();
        params.put("pageId", pageId);
        params.put("s8", "02");
        params.put("parentCode", code);
        params.put("cfg", "com.lawyee.judge.dc.parse.dto.LoadDicDsoDTO@loadFyByCode");
        params.put("__RequestVerificationToken", ParamsUtils.random(24));
        params.put("wh", 470);
        params.put("ww", 1680);
        HttpResponse response = null;
        try {
            TimeUnit.SECONDS.sleep(RandomUtil.randomInt(min, max));
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
                    .header("Host", "wenshu.court.gov.cn")
                    .header("Origin", "https://wenshu.court.gov.cn")
                    .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?pageId=" + pageId + "&s8=02")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    //  .header("User-Agent", configList.get(index).getAgent())
                    //  .header("sec-ch-ua", configList.get(index).getChua())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();

            Result result = JSON.parseObject(response.body(), Result.class);
            JSONObject object = JSON.parseObject(result.getResult());
            List<Dict> courts = JSON.parseArray(object.getJSONArray("fy").toJSONString(), Dict.class);
            List<Dict> countList = new CopyOnWriteArrayList<>();
            if (courts.size() == 0) {
                return countList;
            }
            countList.addAll(courts);
            for (Dict court : courts) {
                countList.addAll(getCourt(court.getCode()));
            }
            return countList;
        } catch (Exception e) {
            if (response != null) {
                log.error("body={}", response.body());
            }
            log.error("发送列表请求出错", e);
            try {
                TimeUnit.SECONDS.sleep(RandomUtil.randomInt(min, max));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return new ArrayList<Dict>();
    }

}
