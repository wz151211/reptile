package com.ping.reptile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.common.collect.Lists;
import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.mapper.ConfigMapper;
import com.ping.reptile.mapper.JudicialCasesMapper;
import com.ping.reptile.mapper.PkuConfigMapper;
import com.ping.reptile.mapper.PkulawPunishMapper;
import com.ping.reptile.model.entity.ConfigEntity;
import com.ping.reptile.model.entity.JudicialCasesEntity;
import com.ping.reptile.model.entity.PkuConfigEntity;
import com.ping.reptile.model.entity.PkulawPunishEntity;
import com.ping.reptile.model.vo.Item;
import com.ping.reptile.model.vo.Node;
import com.ping.reptile.model.vo.Pkulaw;
import com.ping.reptile.model.vo.Theme;
import com.ping.reptile.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class JudicialCasesService {
    @Autowired
    private CustomProperties properties;
    @Autowired
    private PkuConfigMapper pkuConfigMapper;
    @Autowired
    private JudicialCasesMapper judicialCasesMapper;

    private PkuConfigEntity config = null;
    private LocalDate date = null;
    private int min = 5;
    private int max = 10;
    private AtomicInteger days = new AtomicInteger(0);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            30,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public void page(Integer pageNum, Integer pageSize, String code) {
        if (config == null) {
            config = pkuConfigMapper.selectById(properties.getId());
        }
        if (pageNum == null) {
            pageNum = config.getPageNum();
        }
        if (pageSize == null) {
            pageSize = config.getPageSize();
        }
        list(pageNum, pageSize, code);
        days.getAndAdd(10);
        page(pageNum, pageSize, code);
    }

    public void list(Integer pageNum, Integer pageSize, String code) {
        String url = "https://www.pkulaw.com/searchingapi/list/advanced/apy";
        if (date == null) {
            date = LocalDate.parse(config.getDocDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
        LocalDate start = date.minusDays(days.get() + 10);
        LocalDate end = date.minusDays(days.get());
        String startDate = start.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", ".");
        String endDate = end.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", ".");
        log.info("pageNum = {},day={}", pageNum, startDate);
        if (start.isBefore(LocalDate.of(2015, 1, 01))) {
            return;
        }
        if (executor.getQueue().size() > 1000) {
            try {
                TimeUnit.MINUTES.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Node punishmentDate = Node.builder().type("daterange").order(5).showText("审结日期").fieldName("LastInstanceDate").combineAs(2).fieldItems(Lists.newArrayList(Item.builder()
                .order(0)
                .combineAs(1)
                .start(startDate)
                .end(endDate)
                //   .values(Lists.newArrayList(startMinus.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", "."),
                //           endMinus.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", ".")))
                .build())).build();
        JSONObject object1 = JSON.parseObject("{\n" +
                "            \"type\":\"text\",\n" +
                "            \"order\":1,\n" +
                "            \"showText\":\"全文\",\n" +
                "            \"fieldName\":\"FullText\",\n" +
                "            \"matchTypeEnabled\":false,\n" +
                "            \"matchSpanEnabled\":true,\n" +
                "            \"combineAs\":2,\n" +
                "            \"subCombineAs\":2,\n" +
                "            \"fieldItems\":[\n" +
                "                {\n" +
                "                    \"order\":0,\n" +
                "                    \"values\":\"一并审查\",\n" +
                "                    \"valuesCombineAs\":2,\n" +
                "                    \"extra\":{\n" +
                "                        \"combineAs\":2,\n" +
                "                        \"values\":\"规范性文件\"\n" +
                "                    },\n" +
                "                    \"matchType\":1,\n" +
                "                    \"matchSpan\":1,\n" +
                "                    \"matchSpanGap\":0,\n" +
                "                    \"fieldScope\":{\n" +
                "                        \"fieldName\":\"\",\n" +
                "                        \"showText\":\"\"\n" +
                "                    },\n" +
                "                    \"filterNodes\":[\n" +
                "\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }");

        Map<String, String> group = new HashMap<>();
        group.put("CaseGrade", code);
        Pkulaw pkulaw = Pkulaw.builder()
                .orderbyExpression("SortNum Desc,LastInstanceDate Des")
                .pageIndex(pageNum)
                .pageSize(pageSize)
                .fieldNodes(Lists.newArrayList(object1, JSON.parseObject(JSON.toJSONString(punishmentDate))))
                .clusterFilters(group)
                .groupBy(group)
                .build();

        HttpResponse response = null;
        JSONConfig conf = new JSONConfig();
        conf.setOrder(true);
        String jsonStr = JSONUtil.toJsonStr(pkulaw, conf);
        log.info("列表请求参数-{}", jsonStr);
        try {
            TimeUnit.SECONDS.sleep(5);
            response = HttpRequest.post(url)
                    .timeout(1000 * 30)
                    .body(jsonStr)
                    .header("X-Real-IP", IpUtils.getIp())
                    .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/json")
                    .header("Host", "www.pkulaw.com")
                    .header("Origin", "https://www.pkulaw.com")
                    .header("Referer", "https://www.pkulaw.com/advanced/penalty/")
                    .header("Authorization", "")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36")
                    .header("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"")
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("Sec-Fetch-Dest", "document")
                    .header("Sec-Fetch-Mode", "navigate")
                    .header("Sec-Fetch-Site", "none")
                    .header("Sec-Fetch-User", "?1")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("CustomRequest", "CustomRequest/list/advanced")
                    .execute();
        } catch (Exception e) {
            log.error("发送列表请求出错", e);
            if (response != null) {
                log.error("body={}", response.body());
            }
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        int tatal = 0;
        try {
            JSONObject object = JSON.parseObject(response.body());
            if (object == null) {
                return;
            }
            JSONArray data = object.getJSONArray("data");
            log.info("列表数量={}", data.size());
            if (data == null || data.size() == 0) {
                return;
            }
            tatal = data.size();
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                String gid = jsonObject.getString("gid");
                Long count = judicialCasesMapper.selectCount(Wrappers.<JudicialCasesEntity>lambdaQuery().eq(JudicialCasesEntity::getId, gid));
                if (count > 0) {
                    continue;
                }
                executor.execute(() -> {
                    details(gid);
                });
            }
            if (tatal >= pageSize) {
                list(pageNum + 1, pageSize, code);
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

    public void details(String gid) {
        log.info("线程池中任务数量={}", executor.getQueue().size());
        String url = "https://www.pkulaw.com/apy/" + gid + ".html";
        List<HttpCookie> cookies = new ArrayList<>();
        HttpCookie cookie4 = new HttpCookie("pkulaw_v6_sessionid", "");
        cookie4.setDomain("www.pkulaw.com");
        cookie4.setPath("/");
        cookie4.setHttpOnly(true);
        cookie4.setSecure(false);
        cookies.add(cookie4);
        try (final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED)) {
            TimeUnit.SECONDS.sleep(3);
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
            webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
            webClient.addRequestHeader("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
            webClient.addRequestHeader("sec-ch-ua-mobile", "?0");
            webClient.addRequestHeader("sec-ch-ua-platform", "Windows");
            webClient.addRequestHeader("Sec-Fetch-Dest", "document");
            webClient.addRequestHeader("Sec-Fetch-Mode", "navigate");
            webClient.addRequestHeader("Sec-Fetch-Site", "none");
            webClient.addRequestHeader("Sec-Fetch-User", "?1");
            webClient.addRequestHeader("Upgrade-Insecure-Requests", "1");
            CookieManager cookieManager = new CookieManager();
            for (HttpCookie cookie : cookies) {
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
            HtmlPage page = webClient.getPage(url);
            Document parse = Jsoup.parse(page.asXml());
            Element element = parse.getElementById("gridleft");
            String title = element.getElementsByClass("title").get(0).text();
            Element fields = element.getElementsByClass("fields").get(0);
            Elements spans = fields.getElementsByTag("li");
            JudicialCasesEntity entity = new JudicialCasesEntity();
            entity.setId(gid);
            entity.setTitle(title);

            for (int i = 0; i < spans.size(); i++) {
                String value = "";
                Element span = spans.get(i);
                Elements strong = span.getElementsByTag("strong");
                String name = strong.text().trim();
                for (Element text : span.getElementsByAttribute("title")) {
                    value += text.attr("title");
                }
                if (StringUtils.isEmpty(value)) {
                    value = span.firstElementChild().ownText();
                }
                if (name.contains("案由")) {
                    entity.setCause(value);
                }
                if (name.contains("案号")) {
                    entity.setCaseNo(value);
                }
                if (name.contains("审理法官")) {
                    entity.setJudge(value);
                }
                if (name.contains("文书类型")) {
                    entity.setDocType(value);
                }
                if (name.contains("公开类型")) {
                    entity.setExposedType(value);
                }
                if (name.contains("审理法院")) {
                    entity.setCourt(value);
                }
                if (name.contains("审结日期")) {
                    try {
                        entity.setConclusionDate(DateUtil.parse(value, "yyyy.MM.dd").toJdkDate());
                    } catch (Exception e) {
                        log.info("发布日期格式化出错，date={},id={}", value, gid);
                        e.printStackTrace();
                    }
                }
                if (name.contains("案件类型")) {
                    entity.setCaseType(value);
                }
                if (name.contains("审理程序")) {
                    entity.setTrialProceedings(value);
                }
                if (name.contains("代理律师/律所")) {
                    entity.setLawFirm(value);
                }
                if (name.contains("权责关键词")) {
                    entity.setKeyword(value);
                }

            }

            Element contentElement = element.getElementById("divFullText");
            entity.setHtml(element.html());
            entity.setContent(contentElement.text());
            log.info("案件名称={}", entity.getTitle());
            judicialCasesMapper.insert(entity);
        } catch (Exception e) {
            log.error("gid={}", gid);
            log.info("HtmlUnit获取页面出错", e);
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
