package com.ping.reptile.pkulaw.law;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.mapper.PkuConfigMapper;
import com.ping.reptile.model.entity.PkuConfigEntity;
import com.ping.reptile.model.vo.Item;
import com.ping.reptile.model.vo.Node;
import com.ping.reptile.model.vo.Pkulaw;
import com.ping.reptile.pkulaw.law.vo.PkulawEntity;
import com.ping.reptile.pkulaw.law.vo.PkulawVo;
import com.ping.reptile.pkulaw.law.vo.Result;
import com.ping.reptile.pkulaw.law.vo.SummaryVo;
import com.ping.reptile.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.htmlunit.BrowserVersion;
import org.htmlunit.CookieManager;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.util.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class PkulawService {
    @Autowired
    private CustomProperties properties;
    @Autowired
    private PkuConfigMapper pkuConfigMapper;
    @Autowired
    private PkulawMapper pkulawMapper;

    private Pkulaw pkulaw;

    private List<HttpCookie> cookieList;

    private PkuConfigEntity config = null;
    private LocalDate date = null;
    private int min = 5;
    private int max = 10;
    private DateTime start = DateUtil.date();
    private DateTime time = DateUtil.date();
    private AtomicInteger count = new AtomicInteger(0);
    private AtomicInteger days = new AtomicInteger(0);
    private AtomicBoolean stop = new AtomicBoolean(false);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,
            2,
            30,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy());


    public void page(Integer pageNum, Integer pageSize) {
        if (config == null) {
            config = pkuConfigMapper.selectById(properties.getId());
        }
        if (pageNum == null) {
            pageNum = config.getPageNum();
        }
        if (pageSize == null) {
            pageSize = config.getPageSize();
        }
        if (date == null) {
            date = LocalDate.parse(config.getDocDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (pkulaw == null) {
            pkulaw = JSON.parseObject(config.getParams(), Pkulaw.class);
        }
        if (cookieList == null) {
            cookieList = new ArrayList<>();
            String cookie1 = config.getCookie();
            String[] cookies = cookie1.split(";");
            for (String s : cookies) {
                String[] s1 = s.split("=");
                HttpCookie cookie = new HttpCookie(s1[0], s1[1]);
                if (s1[0].contains("pkulaw_v6_sessionid")) {
                    cookie.setDomain("www.pkulaw.com");
                } else {
                    cookie.setDomain(".pkulaw.com");
                }
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookieList.add(cookie);
            }
        }
        LocalDate start = date.minusDays(days.get() + properties.getIntervalDays());
        LocalDate end = date.minusDays(days.get());
        String startDate = start.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", ".");
        String endDate = end.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", ".");
        days.getAndIncrement();
        List<Node> fieldNodes = pkulaw.getFieldNodes();
        for (Node node : fieldNodes) {
            for (Item item : node.getFieldItems()) {
                item.setStart(startDate);
                item.setEnd(endDate);
            }
        }
        pkuConfigMapper.updateDocDateById(end, config.getId());
        log.info("startDay={}, endDay={}", startDate, endDate);
        list(pageNum, pageSize);
        days.getAndAdd(properties.getIntervalDays());
        page(pageNum, pageSize);
    }

    public void list(Integer pageNum, Integer pageSize) {

        pkulaw.setPageIndex(pageNum);
        pkulaw.setPageSize(pageSize);
        log.info("pageNum = {}", pageNum);
        HttpResponse response = null;
        log.info("列表请求参数-{}", pkulaw);
        try {
            if (stop.get()) {
                TimeUnit.HOURS.sleep(6);
            }
            DateTime now = DateUtil.date();
            long minutes = DateUtil.between(start, now, DateUnit.MINUTE);
            if (minutes >= 240 && minutes <= 260) {
                log.info("休息30分钟");
                TimeUnit.MINUTES.sleep(30);
                start = DateUtil.offsetMinute(start, 270);
            }
            if (count.get() >= 10000) {
                log.info("休息了，数量已到限制");
                TimeUnit.HOURS.sleep(2);
                count.set(0);

            }
            TimeUnit.SECONDS.sleep(5);
            response = request(config.getUrl(), JSON.toJSONString(pkulaw));
        } catch (Exception e) {
            log.error("发送列表请求出错", e);
            try {
                if (response != null) {
                    log.error("body={}", response.body());
                }
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        int tatal = 0;
        try {
            Result result = null;
            try {
                result = JSON.parseObject(response.body(), Result.class);
            } catch (Exception e) {
                log.info("解析返回结果出错={}", response.body());
                if (response.body().contains("wyeCN")) {
                    TimeUnit.MINUTES.sleep(5);
               /*     boolean token = refreshToken();
                    if (!token) {
                        TimeUnit.HOURS.sleep(2);
                    }
                */
                    list(pageNum, pageSize);
                }
                if (response.body().contains("您的请求已被该站点的安全策略拦截")) {
                    log.info("您的请求已被该站点的安全策略拦截");
                    stop.set(true);
                    int hour = DateUtil.date().hour(true);
                    TimeUnit.HOURS.sleep(24 - hour);
                }
            }
            if (result == null || result.getData() == null) {
                log.info("返回值={}", response.body());
                if (response.body().contains("token expired") || response.body().contains("token offline")) {
                    log.info("token已过期");
                    TimeUnit.MINUTES.sleep(5);
                    boolean token = refreshToken();
                    if (!token) {
                        TimeUnit.HOURS.sleep(2);
                    }
                    list(pageNum, pageSize);
                }
                return;
            }

            if (result.getData() == null || result.getData().size() == 0) {
                return;
            }
            log.info("列表数量={}", result.getData().size());
            tatal = result.getData().size();
            for (PkulawVo vo : result.getData()) {
                PkulawEntity pkulaw = pkulawMapper.selectOne(Wrappers.<PkulawEntity>lambdaQuery().select(PkulawEntity::getGid, PkulawEntity::getId, PkulawEntity::getTimelinessDic).eq(PkulawEntity::getGid, vo.getGid()));
                if (pkulaw == null) {
                    PkulawEntity entity = new PkulawEntity();
                    entity.setTitle(vo.getTitle());
                    entity.setGid(vo.getGid());
                    entity.setLibrary(config.getUrl().substring(config.getUrl().lastIndexOf("/") + 1));
                    entity.setKeywords(vo.getKeywords());
                    for (SummaryVo summary : vo.getSummaries()) {
                        if (summary.getText().contains("现行有效")
                                || summary.getText().contains("失效")
                                || summary.getText().contains("已被修改")
                                || summary.getText().contains("尚未生效")
                                || summary.getText().contains("尚未施行")
                                || summary.getText().contains("部分失效")) {
                            entity.setTimelinessDic(summary.getText());
                        }
                        if (summary.getText().contains("号")) {
                            entity.setDocumentNo(summary.getText());
                        }
                        if (summary.getText().contains("公布")) {
                            entity.setIssueDate(summary.getText().replace("公布", ""));
                        }
                        if (summary.getText().contains("颁布")) {
                            entity.setIssueDate(summary.getText().replace("颁布", ""));
                        }
                        if (summary.getText().contains("刊登")) {
                            entity.setIssueDate(summary.getText().replace("刊登", ""));
                        }
                        if (summary.getText().contains("施行")) {
                            entity.setImplementDate(summary.getText().replace("施行", ""));
                        }
                    }
                    if (executor.getQueue().size() > 50) {
                        try {
                            TimeUnit.MINUTES.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    executor.execute(() -> details(entity));
                    //  details(entity);
                } else {
                    String timelinessDic = null;
                    for (SummaryVo summary : vo.getSummaries()) {
                        if (summary.getText().contains("现行有效")
                                || summary.getText().contains("失效")
                                || summary.getText().contains("现行")
                                || summary.getText().contains("废止")
                                || summary.getText().contains("已被修改")
                                || summary.getText().contains("尚未生效")
                                || summary.getText().contains("尚未施行")
                                || summary.getText().contains("部分失效")) {
                            timelinessDic = summary.getText();
                        }
                        if (StringUtils.isNotEmpty(timelinessDic) && !timelinessDic.equals(pkulaw.getTimelinessDic())) {
                            PkulawEntity entity = new PkulawEntity();
                            entity.setId(pkulaw.getId());
                            entity.setTimelinessDic(timelinessDic);
                            entity.setUpdateTime(new Date());
                            try {
                                pkulawMapper.updateById(entity);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            if (tatal >= pageSize) {
                list(pageNum + 1, pageSize);
            }

        } catch (Exception e) {
            if (response != null) {
                log.error("body={}", response.body());
            }
            log.error("列表获取出错", e);
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    public void details(PkulawEntity entity) {
        log.info("线程池中任务数量={}", executor.getQueue().size());

        try (final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED)) {
            TimeUnit.SECONDS.sleep(RandomUtil.randomInt(1, 10));
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.waitForBackgroundJavaScript(600 * 1000);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.addRequestHeader("X-Real-IP", IpUtils.getIp());
            webClient.addRequestHeader("X-Forwarded-For", IpUtils.getIp());
            webClient.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            webClient.addRequestHeader("Accept-Encoding", "gzip, deflate, br");
            webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.9");
            webClient.addRequestHeader("Connection", "keep-alive");
            webClient.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            webClient.addRequestHeader("Host", " www.pkulaw.com");
            webClient.addRequestHeader("User-Agent", config.getAgent());
            webClient.addRequestHeader("Sec-Ch-Ua", config.getChUa());
            webClient.addRequestHeader("Sec-Ch-Ua-Platform", config.getPlatform());
            webClient.addRequestHeader("Sec-Ch-Ua-Mobile", "?0");
            webClient.addRequestHeader("Sec-Fetch-Dest", "document");
            webClient.addRequestHeader("Sec-Fetch-Mode", "navigate");
            webClient.addRequestHeader("Sec-Fetch-Site", "none");
            webClient.addRequestHeader("Sec-Fetch-User", "?1");
            webClient.addRequestHeader("Upgrade-Insecure-Requests", "1");
            CookieManager cookieManager = new CookieManager();
            for (HttpCookie cookie : cookieList) {
                Cookie cookie11 = new Cookie(cookie.getDomain(),
                        cookie.getName(),
                        cookie.getValue(),
                        cookie.getPath(),
                        null,
                        cookie.getSecure(),
                        cookie.isHttpOnly());
                cookieManager.addCookie(cookie11);
            }
            webClient.setCookieManager(cookieManager);
            String url = config.getUrl().replace("searchingapi/adv/list/", "") + "/" + entity.getGid() + ".html";
            HtmlPage page = webClient.getPage(url);
            Document parse = Jsoup.parse(page.asXml());
            Element element = parse.getElementById("gridleft");
            try {
                if (element == null) {
                    TimeUnit.SECONDS.sleep(3);
                    element = parse.getElementById("gridleft");
                    if (element == null) {
                        if (parse.text().contains("检测到您所使用的IP存在风险")) {
                            stop.set(true);
                            log.info("IP被封");
                            TimeUnit.HOURS.sleep(6);
                        }
                        if (parse.text().contains("您的账号存在安全风险已被限制访问")) {
                            stop.set(true);
                            log.info("账号被限制访问");
                            TimeUnit.HOURS.sleep(6);
                        }
                        return;
                    }
                }
                if (element.text().contains("剩余50%未阅读")) {
                    log.info("访问数量已经到达上限");
                    stop.set(true);
                    int hour = DateUtil.date().hour(true);
                    TimeUnit.HOURS.sleep(24 - hour);
                }
                if (element.text().contains("今日正文查看数已满")) {
                    log.info("访问数量已经到达上限");
                    stop.set(true);
                    int hour = DateUtil.date().hour(true);
                    TimeUnit.HOURS.sleep(24 - hour);
                }
                Element bgImg = parse.getElementById("bgImg");
                if (bgImg != null) {
                    log.info("访问频率过高");
                    TimeUnit.MINUTES.sleep(20);
                }
                if (parse.text().contains("您的请求已被该站点的安全策略拦截")) {
                    log.info("您的请求已被该站点的安全策略拦截");
                    stop.set(true);
                    int hour = DateUtil.date().hour(true);
                    TimeUnit.HOURS.sleep(24 - hour);
                }
            } catch (Exception ex) {
                //  ex.printStackTrace();
            }
            Element fields = element.getElementsByClass("fields").get(0);
            Elements spans = fields.getElementsByTag("li");

            for (int i = 0; i < spans.size(); i++) {
                String value = "";
                Element span = spans.get(i);
                Elements strong = span.getElementsByTag("strong");
                String name = strong.text().trim();
                for (Element text : span.getElementsByTag("a")) {
                    if (StringUtils.isEmpty(text.ownText())) {
                        continue;
                    }
                    value += text.ownText() + ",";
                }
                if (StringUtils.isEmpty(value)) {
                    for (Element box : span.getElementsByClass("box")) {
                        if (StringUtils.isEmpty(box.ownText())) {
                            continue;
                        }
                        value += box.ownText();
                    }
                }
                if (StringUtils.isEmpty(value)) {
                    value = span.firstElementChild().ownText();
                }
                if (StringUtils.isNotEmpty(value)) {
                    if (StringUtils.isNotEmpty(value) && value.contains(",")) {
                        value = value.substring(0, value.length() - 1);
                    }
                }
                if (name.contains("制定机关") || name.contains("相关组织") || name.contains("新闻来源") || name.contains("官方合同") || name.contains("发布部门")) {
                    entity.setIssueDepartment(value);
                }
                if (name.contains("效力位阶") || name.contains("法规位阶")) {
                    entity.setEffectivenessDic(value);
                }
                if (name.contains("法规类别") || name.contains("条约种类") || name.contains("法规分类") || name.contains("法規分類") || name.contains("新闻分类") || name.contains("合同分类") || name.contains("行业类别") || name.contains("文书分类")) {
                    entity.setCategory(value);
                }
                if (name.contains("类别") && StringUtils.isEmpty(entity.getCategory())) {
                    entity.setCategory(value);
                }
                if (name.contains("专题分类") || name.contains("条约领域") || name.contains("類別")) {
                    entity.setTopicType(value);
                }
                if (name.contains("批准机关")) {
                    entity.setRatifyDepartment(value);
                }
                if (name.contains("批准日期")) {
                    entity.setRatifDate(value);
                }
                if (name.contains("失效依据")) {
                    entity.setInvalidBasis(value);
                }
                if (name.contains("颁布日期") || name.contains("刊登日期")) {
                    entity.setIssueDate(value);
                }
            }
            Element fullText = element.getElementById("divFullText");
            if (fullText != null) {
                entity.setAllText(fullText.html());
            }
            log.info("案件名称={}", entity.getTitle());
            entity.setCreateTime(new Date());
            try {
                pkulawMapper.insert(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.error("gid={}", entity.getGid());
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }


    public boolean refreshToken() {
        String url = "https://www.pkulaw.com/gateway/account/auth/refreshtoken";
        Map<String, String> params = new HashMap<>();
        params.put("access_token", config.getAuthorization().replace("Bearer ", "").trim());
        HttpResponse request = request(url, JSON.toJSONString(params));
        if (StringUtils.isNotEmpty(request.body())) {
            JSONObject object = JSON.parseObject(request.body());
            String accessToken = object.getString("access_token");
            log.info("刷新token={}", accessToken);
            if (StringUtils.isNotEmpty(accessToken)) {
                config.setAuthorization("Bearer " + accessToken);
                PkuConfigEntity entity = new PkuConfigEntity();
                entity.setId(config.getId());
                entity.setAuthorization("Bearer " + accessToken);
                entity.setAuthorization(entity.getAuthorization().trim());
                pkuConfigMapper.updateById(entity);
                return true;
            } else {
                log.info("获取刷新token失败={}", request.body());
            }
        }
        return false;
    }

    public HttpResponse request(String url, String params) {
        return HttpRequest.post(url)
                .timeout(-1)
                .cookie(cookieList)
                .body(params)
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/json")
                .header("Host", "www.pkulaw.com")
                .header("Origin", "https://www.pkulaw.com")
                .header("Referer", "https://www.pkulaw.com/advanced/law/lar")
                .header("Authorization", config.getAuthorization().trim())
                .header("User-Agent", config.getAgent())
                .header("Sec-Ch-Ua", config.getChUa())
                .header("Sec-Ch-Ua-Platform", config.getPlatform())
                .header("Sec-Ch-Ua-Mobile", "?0")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Customrequest", "CustomRequest/list/advanced")
                .execute();
    }
}
