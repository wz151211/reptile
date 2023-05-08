package com.ping.reptile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.mapper.ConfigMapper;
import com.ping.reptile.mapper.DocumentMapper;
import com.ping.reptile.model.entity.ConfigEntity;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.model.vo.Dict;
import com.ping.reptile.model.vo.Pair;
import com.ping.reptile.model.vo.Result;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    private Integer min;
    private Integer max;
    private String requestVerificationToken = ParamsUtils.random(24);
    private AtomicInteger intervalDays = new AtomicInteger(0);

    private List<ConfigEntity> configList = null;

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
        if (min == null) {
            min = properties.getMin();
        }
        if (max == null) {
            max = properties.getMax();
        }
        configList = configMapper.selectList(Wrappers.<ConfigEntity>lambdaQuery().eq(ConfigEntity::getCategory, properties.getCategory()));
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
        intervalDays.set(RandomUtil.randomInt(properties.getIntervalDays() / 2, properties.getIntervalDays()));
        list(pageNum, pageSize);
        days.getAndAdd(intervalDays.get());
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
        String pageId = ParamsUtils.getPageId();
        Map<String, Object> params = new HashMap<>();
        String start = date.minusDays(days.get() + intervalDays.get()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String end = date.minusDays(days.get()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        params.put("pageId", pageId);
        if (StringUtils.isEmpty(config.getCaseType())) {
            params.put("s8", "02");
        } else {
            params.put("s8", config.getCaseType());
        }
        params.put("sortFields", "s51:desc");
        params.put("ciphertext", ParamsUtils.cipher());
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        Pair datePair = new Pair();
        datePair.setKey("cprq");
        datePair.setValue(start + " TO " + end);
        List<Pair> array = JSON.parseArray(config.getParams(), Pair.class);
        array.add(datePair);
        String pairs = JSON.toJSONString(array);
        log.info("参数={}", pairs);
        params.put("queryCondition", pairs);
        params.put("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@queryDoc");
        params.put("__RequestVerificationToken", requestVerificationToken);
        params.put("wh", 699);
        params.put("ww", 1280);
        params.put("cs", 0);

        ConfigEntity entity = configList.get(RandomUtil.randomInt(0, configList.size()));
        log.info("config:{}", entity);
        String session = null;
        String[] split = session.split(";");
        List<HttpCookie> cookieList = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            int index = s.indexOf("=");
            String key = s.substring(0, index).trim();
            String value = s.substring(index + 1).trim();
            HttpCookie httpCookie = new HttpCookie(key, value);
            httpCookie.setDomain("wenshu.court.gov.cn");
            httpCookie.setPath("/");
            httpCookie.setHttpOnly(true);
            httpCookie.setSecure(false);
            cookieList.add(httpCookie);
        }
        HttpResponse response = null;
        try {
            response = HttpRequest.post(url)
                    .form(params)
                    .timeout(-1)
                    .cookie(cookieList)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    //  .header("X-Real-IP", IpUtils.getIp())
                    //   .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Length", (params.toString().length() - params.size() - 1) + "")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Host", "wenshu.court.gov.cn")
                    .header("Origin", "https://wenshu.court.gov.cn")
                    .header("Pragma", "no-cache")
                    .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?pageId=" + pageId + "&s8=" + params.get("s8"))
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", entity.getAgent())
                    .header("sec-ch-ua", config.getChua())
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
            list(pageNum, pageSize);
        }
        try {
            //     log.info("列表数据={}", response.body());
            Result result = JSON.parseObject(response.body(), Result.class);
            log.info("code={},desc={}", result.getCode(), result.getDescription());
            if (result.getCode() == 9) {
                config = configMapper.selectById(RandomUtil.randomInt(0, configList.size()));
                log.info("Session已过期");
                return;
            }
            if (result.getCode() == -12) {
                log.info("账号已冻结");
                TimeUnit.HOURS.sleep(6);
                return;
            }
            int count = 0;
            if (result.getSuccess()) {
                String iv = DateUtil.format(new Date(), "yyyyMMdd");
                String decrypt = TripleDES.decrypt(result.getSecretKey(), result.getResult(), iv);
                JSONObject object = JSON.parseObject(decrypt);
                JSONArray jsonArray = object.getJSONObject("queryResult").getJSONArray("resultList");
                count = jsonArray.size();
                log.info("列表数量={}", jsonArray.size());
                if (jsonArray.size() == 0) {
                    TimeUnit.SECONDS.sleep(RandomUtil.randomInt(min, max));
                    return;
                }
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String caseNo = obj.getString("7");
                    log.info("案号={}", caseNo);
                    Long existence = documentMapper.selectCount(Wrappers.<DocumentEntity>lambdaQuery().eq(DocumentEntity::getCaseNo, caseNo));
                    if (existence > 0) {
                        continue;
                    }
                    String docId = obj.getString("rowkey");
                    String token = ParamsUtils.random(24);
                    detail(docId, token);
                }
            }
            TimeUnit.SECONDS.sleep(RandomUtil.randomInt(min, max));
            if (count >= pageSize) {
                list(pageNum + 1, pageSize);
            }
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
            list(pageNum, pageSize);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void detail(String docId, String token) {
        HttpResponse response = null;
        try {
            TimeUnit.SECONDS.sleep(RandomUtil.randomInt(min, max));
            try {
            //    user(docId, token);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
            Map<String, Object> params = new HashMap<>();
            params.put("docId", docId);
            params.put("ciphertext", ParamsUtils.cipher());
            params.put("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@docInfoSearch");
            params.put("__RequestVerificationToken", token);
            params.put("wh", 241);
            params.put("ww", 1275);
            params.put("cs", 0);
            ConfigEntity configEntity = configList.get(RandomUtil.randomInt(0, configList.size()));
            log.info("config:{}", configEntity);
            String session = null;
            String[] split = session.split(";");
            List<HttpCookie> cookieList = new ArrayList<>();
            for (String s : split) {
                int index = s.indexOf("=");
                String key = s.substring(0, index).trim();
                String value = s.substring(index + 1).trim();
                HttpCookie httpCookie = new HttpCookie(key, value);
                httpCookie.setDomain("wenshu.court.gov.cn");
                httpCookie.setPath("/");
                httpCookie.setHttpOnly(true);
                httpCookie.setSecure(false);
                cookieList.add(httpCookie);
            }
            response = HttpRequest.post(url)
                    .form(params)
                    .timeout(-1)
                    .cookie(cookieList)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    //   .header("X-Real-IP", IpUtils.getIp())
                    //   .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Content-Length", (params.toString().length() - params.size() - 1) + "")
                    .header("Host", "wenshu.court.gov.cn")
                    .header("Origin", "https://wenshu.court.gov.cn")
                    .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181107ANFZ0BXSK4/index.html?docId=" + docId)
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", configEntity.getAgent())
                    .header("sec-ch-ua", config.getChua())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();
            //      log.info("详情数据={}", response.body());
            Result result = JSON.parseObject(response.body(), Result.class);
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
                if (StringUtils.isEmpty(id)) {
                    log.info("案件详情:{}", jsonObject);
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
                String cause = null;
                if (causes != null) {
                    cause = causes.stream().map(Object::toString).collect(joining(","));
                }
                JSONArray partys = jsonObject.getJSONArray("s17");
                String party = null;
                if (partys != null) {
                    party = partys.stream().map(Object::toString).collect(joining(","));
                }
                JSONArray keywords = jsonObject.getJSONArray("s45");
                String keyword = null;
                if (keywords != null) {
                    keyword = keywords.stream().map(Object::toString).collect(joining(","));
                }
                String courtConsidered = jsonObject.getString("s26");
                String judgmentResult = jsonObject.getString("s27");
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
                entity.setParty(party);
                entity.setCause(cause);
                entity.setJudgmentResult(judgmentResult);
                entity.setKeyword(keyword);
                entity.setCourtConsidered(courtConsidered);
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
            detail(docId, token);
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }


    public void user(String docId, String token) {
        HttpResponse response = null;
        try {
            String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
            Map<String, Object> params = new HashMap<>();
            params.put("docId", docId);
            params.put("cfg", "com.lawyee.wbsttools.web.parse.dto.AppUserDTO@currentUser");
            params.put("__RequestVerificationToken", token);
            params.put("wh", 241);
            params.put("ww", 1275);
            params.put("cs", 0);
            ConfigEntity configEntity = configList.get(RandomUtil.randomInt(0, configList.size()));
            log.info("config:{}", configEntity);
            String session = null;
            String[] split = session.split(";");
            List<HttpCookie> cookieList = new ArrayList<>();
            for (String s : split) {
                String[] cookie = s.split("=");
                HttpCookie httpCookie = new HttpCookie(cookie[0], cookie[1]);
                httpCookie.setDomain("wenshu.court.gov.cn");
                httpCookie.setPath("/");
                httpCookie.setHttpOnly(true);
                httpCookie.setSecure(false);
                cookieList.add(httpCookie);
            }
            response = HttpRequest.post(url)
                    .form(params)
                    .timeout(-1)
                    .cookie(cookieList)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    //   .header("X-Real-IP", IpUtils.getIp())
                    //   .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Content-Length", (params.toString().length() - params.size() - 1) + "")
                    .header("Host", "wenshu.court.gov.cn")
                    .header("Origin", "https://wenshu.court.gov.cn")
                    .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181107ANFZ0BXSK4/index.html?docId=" + docId)
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", configEntity.getAgent())
                    .header("sec-ch-ua", config.getChua())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();
            //      log.info("详情数据={}", response.body());
            Result result = JSON.parseObject(response.body(), Result.class);
        } catch (Exception e) {
            try {
                if (response != null) {
                    log.error("body={}", response.body());
                    if (response.body().contains("307 Temporary Redirect")) {
                        TimeUnit.MINUTES.sleep(RandomUtil.randomInt(min, max));
                    }
                }
                log.error("用户获取出错", e);
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
}
