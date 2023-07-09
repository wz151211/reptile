package com.ping.reptile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.mapper.ConfigMapper;
import com.ping.reptile.mapper.PkulawPunishMapper;
import com.ping.reptile.model.entity.ConfigEntity;
import com.ping.reptile.model.entity.PkulawPunishEntity;
import com.ping.reptile.model.vo.Pkulaw;
import com.ping.reptile.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class PkulawService_bak {
    @Autowired
    private CustomProperties properties;
    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private PkulawPunishMapper punishMapper;

    private Pkulaw pkulaw;
    private ConfigEntity config = null;
    private LocalDate date = null;
    private AtomicInteger days = new AtomicInteger(0);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            30,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy());

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
        if (pkulaw == null) {
            pkulaw = JSON.parseObject(config.getParams(), Pkulaw.class);
        }
        list(pageNum, pageSize);
        days.getAndIncrement();
        page(pageNum, pageSize);
    }

    public void list(Integer pageNum, Integer pageSize) {
        String url = "https://www.pkulaw.com/searchingapi/list/advanced/apy";
        if (date == null) {
            date = LocalDate.parse(config.getDocDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
        LocalDate start = date.minusDays(days.get());
        LocalDate end = date.minusDays(days.get());
        LocalDate endMinus = start.minusDays(6);
        LocalDate startMinus = endMinus.minusDays(6);
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

        HttpResponse response = null;
        log.info("列表请求参数-{}", pkulaw);
        String cookies = "gr_user_id=4d3d27b0-eb97-469d-8cba-d9fa6bc63bac; Hm_lvt_8266968662c086f34b2a3e2ae9014bf8=1682319538,1682754556,1683429513; pkulaw_v6_sessionid=jsdogntuqmir2t3n0w3pdzd0; a2e71717-86ec-ed11-b393-00155d3c0709_law=false; xCloseNew=8; CookieId=691505240b7fdf1f6301760eb79e1dd7; CookieId_LEGACY=691505240b7fdf1f6301760eb79e1dd7; KC_ROOT_LOGIN=1; KC_ROOT_LOGIN_LEGACY=1; SUB=e2234215-2a5d-43db-bef8-ea2a153c338b; SUB_LEGACY=e2234215-2a5d-43db-bef8-ea2a153c338b; f0871a58-97d1-ec11-b392-00155d3c0709_law=false; div_display=none; __RequestVerificationToken=e3Hu4_8sQZ9pTUEoCeYAC_RHOwoV1sw0Vo9LxMZlYm8sKHHJw1pBHbQa57We6pmqoMZEPIZTkVeWm2NXeeJS7ITAh9o1; userislogincookie=true; LoginAccount=wx2022051210004826671; authormes=bf20bed2d11ba001ebcb5ea4f0f65183b299c26aa1900a84b7822b69c68f18c1a5404d51976aefdabdfb; referer=https://www.pkulaw.com/penalty?way=topGuid; Hm_up_8266968662c086f34b2a3e2ae9014bf8=%7B%22ysx_yhqx_20220602%22%3A%7B%22value%22%3A%220%22%2C%22scope%22%3A1%7D%2C%22ysx_hy_20220527%22%3A%7B%22value%22%3A%2203%22%2C%22scope%22%3A1%7D%2C%22uid_%22%3A%7B%22value%22%3A%22f0871a58-97d1-ec11-b392-00155d3c0709%22%2C%22scope%22%3A1%7D%2C%22ysx_yhjs_20220602%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%7D; Hm_lpvt_8266968662c086f34b2a3e2ae9014bf8=1683463767";
        List<HttpCookie> list = new ArrayList<>();
        for (String s : cookies.split(";")) {
            String[] split = s.split("=");
            HttpCookie cookie = new HttpCookie(split[0], split[1]);
            cookie.setPath("/");
            cookie.setDomain(".pkulaw.com");
        }

        try {
            TimeUnit.SECONDS.sleep(5);
            response = HttpRequest.post(url)
                    .timeout(1000 * 30)
                    .cookie(list)
                    .body(JSON.toJSONString(pkulaw))
                    // .header("X-Real-IP", IpUtils.getIp())
                    // .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/json")
                    .header("Host", "www.pkulaw.com")
                    .header("Origin", "https://www.pkulaw.com")
                    .header("Referer", "https://www.pkulaw.com/advanced/penalty/")
                    .header("Authorization", config.getToken())
                    .header("User-Agent", config.getAgent())
                    .header("sec-ch-ua", config.getChua())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
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

        try {
            log.info("body={}", response.body());
            JSONObject object = JSON.parseObject(response.body());
            if (object == null) {
                return;
            }
            JSONArray data = object.getJSONArray("data");
            log.info("列表数量={}", data.size());
            if (data == null || data.size() == 0) {
                return;
            }
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                String gid = jsonObject.getString("gid");
                Long count = punishMapper.selectCount(Wrappers.<PkulawPunishEntity>lambdaQuery().eq(PkulawPunishEntity::getId, gid));
                if (count > 0) {
                    continue;
                }
                executor.execute(() -> {
                    details(gid);
                });
            }
            list(pageNum + 1, pageSize);
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
            PkulawPunishEntity entity = new PkulawPunishEntity();
            entity.setId(gid);
            entity.setTitle(title);
            if (spans.size() <= 6) {
                for (int i = 0; i < spans.size(); i++) {
                    String value = "";
                    for (Element text : spans.get(i).getElementsByAttribute("title")) {
                        value += text.attr("title");
                    }
                    if (i == 0) {
                        entity.setTheme(value);
                    }
                    if (i == 1) {
                        entity.setCategory(value);
                    }
                    if (i == 2) {
                        entity.setName(value);
                    }
                    if (i == 3) {
                        entity.setPunishUnit(value);
                    }
                    if (i == 4) {
                        try {
                            entity.setPunishDate(DateUtil.parse(value, "yyyy.MM.dd").toJdkDate());
                        } catch (Exception e) {
                            log.info("发布日期格式化出错，date={},id={}", value, gid);
                            e.printStackTrace();
                        }
                    }
                    if (i == 5) {
                        entity.setCaseNo(value);
                    }
                }
            } else {
                for (int i = 0; i < spans.size(); i++) {
                    String value = "";
                    for (Element text : spans.get(i).getElementsByAttribute("title")) {
                        value += text.attr("title");
                    }
                    if (i == 0) {
                        entity.setTheme(value);
                    }
                    if (i == 1) {
                        entity.setCategory(value);
                    }
                    if (i == 2) {
                        entity.setName(value);
                    }
                    if (i == 3) {
                        entity.setLevel(value);
                    }
                    if (i == 4) {
                        entity.setPunishUnit(value);
                    }
                    if (i == 5) {
                        entity.setArea(value);
                    }
                    if (i == 6) {
                        try {
                            entity.setPunishDate(DateUtil.parse(value, "yyyy.MM.dd").toJdkDate());
                        } catch (Exception e) {
                            log.info("发布日期格式化出错，date={},id={}", value, gid);
                            e.printStackTrace();
                        }
                    }
                    if (i == 7) {
                        entity.setCaseNo(value);
                    }
                    if (i == 8) {
                        for (Element sibling : spans.get(i - 1).nextElementSiblings()) {
                            value += (sibling.getElementsByTag("a").text() + "|");
                        }
                        entity.setBasis(value);

                    }
                }
            }

            Element contentElement = element.getElementById("divFullText");
            entity.setHtml(element.html());
            entity.setContent(contentElement.text());
            log.info("当事人名称={}", entity.getName());
            punishMapper.insert(entity);
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
