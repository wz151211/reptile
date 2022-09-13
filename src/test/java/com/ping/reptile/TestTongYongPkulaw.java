package com.ping.reptile;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.common.collect.Lists;
import com.ping.reptile.model.vo.Item;
import com.ping.reptile.model.vo.Node;
import com.ping.reptile.model.vo.Pkulaw;
import com.ping.reptile.model.vo.Theme;
import com.ping.reptile.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpCookie;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TestTongYongPkulaw {
    @Test
    public void testList() {
        String url = "https://sy.tongyongbei.com/https/77726476706e69737468656265737421e7e056d2373b7d5c7f1fc7af9758/searchingapi/list/advanced/apy?vpn-12-o2-www.pkulaw.com";
        LocalDate date = LocalDate.now();
        AtomicInteger days = new AtomicInteger(0);

        LocalDate start = date.minusDays(days.get() + 5);
        LocalDate end = date.minusDays(days.get());
        LocalDate endMinus = start.minusDays(6);
        LocalDate startMinus = endMinus.minusDays(6);
        String startDate = start.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", ".");
        String endDate = end.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", ".");
        log.info("pageNum = {},day={}", 0, startDate);
        Node punishmentDate = Node.builder().type("daterange").order(5).showText("处罚日期").fieldName("PunishmentDate").combineAs(2).fieldItems(Lists.newArrayList(Item.builder()
                .order(0)
                .combineAs(1)
                .start(startDate)
                .end(endDate)
                .values(Lists.newArrayList(startMinus.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", "."),
                        endMinus.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", ".")))
                .build())).build();

        Node category = Node.builder().type("select").order(4).showText("主题分类").fieldName("Category").combineAs(2).fieldItems(Lists.newArrayList(Item.builder()
                .value(Lists.newArrayList("003"))
                .keywordTagData(Lists.newArrayList("003"))
                .order(0)
                .combineAs(2)
                .filterNodes(Lists.newArrayList())
                .items(Lists.newArrayList(Theme.builder().name("环保").value("003").text("环保").path("003").build()))
                .build())).build();

        Pkulaw pkulaw = Pkulaw.builder()
                .orderbyExpression("PunishmentDate Desc")
                .pageIndex(0)
                .pageSize(10)
                .fieldNodes(Lists.newArrayList(punishmentDate, category))
                .clusterFilters(new HashMap<>())
                .groupBy(new HashMap<>())
                .build();

        HttpResponse response = null;
        JSONConfig conf = new JSONConfig();
        conf.setOrder(true);
        String jsonStr = JSONUtil.toJsonStr(pkulaw, conf);
        log.info("列表请求参数-{}", jsonStr);
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
                .header("Host", "sy.tongyongbei.com")
                .header("Origin", "https://sy.tongyongbei.com")
                .header("Referer", "https://sy.tongyongbei.com/https/77726476706e69737468656265737421e7e056d2373b7d5c7f1fc7af9758/advanced/penalty/")
                .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFXzZpaDM1eVRMSk1pZUkwdnFnOU1tVFFySjZSY1VTeGlYZU5kY01hb1lrIn0.eyJleHAiOjE2NjU2NjcyMjcsImlhdCI6MTY2MzA3NTMwMSwiYXV0aF90aW1lIjoxNjYzMDc1MjI3LCJqdGkiOiJmNDBhMTY3Yy03ZTQ4LTQ4ZTAtYWZjOC03YTZhNTkxYTY4MDMiLCJpc3MiOiJodHRwczovL2Nhcy5wa3VsYXcuY29tL2F1dGgvcmVhbG1zL2ZhYmFvIiwic3ViIjoiYmVhYmRkMTAtZGNiMi00N2ZmLWEyYzktOWUxYWY0NWIxYTVjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGt1bGF3Iiwic2Vzc2lvbl9zdGF0ZSI6IjRiN2JhODJiLTY2MTYtNDVlNy04MTRjLTllMTkzNzBhNjc2MiIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZXhhbSIsInJlZmVyZW5jZSIsIue7hOeuoeeQhuWRmCIsInByb2N1cmF0b3JhdGUiLCJsYXciLCJqb3VybmFsIiwibGF3ZmlybSIsInBlbmFsdHkiLCJlbmdsaXNoIiwidmlkZW8iLCJjYXNlIl19LCJzY29wZSI6Im9wZW5pZCBwa3VsYXctZXh0ZW5zaW9ucyB2NiByb2xlcyBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwaG9uZU51bWJlciI6IjgyNjY4MjY2IiwibG9naW5UeXBlIjoiaXAiLCJuaWNrbmFtZSI6IuS4reWbveefs-ayueWkp-Wtpu-8iOWNjuS4nO-8iSIsImV4dGVuc2lvbl9yb2xlcyI6e30sInByZWZlcnJlZF91c2VybmFtZSI6IuS4reWbveefs-ayueWkp-Wtpu-8iOWNjuS4nO-8iSIsImVtYWlsIjoiMjU4MzgwNjEzM0BxcS5jb20ifQ.KCiULJRPsdyReGVJa0OinFk2Hl8_rzcVklgZB6T9M676-gLQ9RgySIu-aBvH4bBi3o7chj7U2vZ4IbgaCL7etMwCpTiIOyzqZjyvy-O91t_bsrejb681a6Ob-g3ax4L7UUdYKjhK3f_MsR-P704OdyR_P4fQA4FW8qjiy0EsDsqymbSytNJHgGgLLNweTSw1VowaO88w_DghqIFVVj1-jDvXaBbBrE-BFn1dCHq5T8qNZVhTqgJZWenGlhMTpYo1T5VVEd2iFGDPJFqOBICwdAaNX05Ii8KvaGpyqrj1X5yj5145kiDTldsoLzshvgjYYqGCcdOrQ2YxsycSuPS9qw")
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

        System.out.println(response.body());
        JSONObject object = JSON.parseObject(response.body());
        if (object == null) {

        }
        JSONArray data = object.getJSONArray("data");
        if (data == null || data.size() == 0) {

        }
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String gid = jsonObject.getString("gid");
        }
    }


    @Test
    public void testDetail() throws IOException {
        String url = "https://sy.tongyongbei.com/https/77726476706e69737468656265737421e7e056d2373b7d5c7f1fc7af9758/apy/9445d6e6deb7c9d2ae9735f5515a12b1b299925f0f1381efbdfb.html";
        List<HttpCookie> cookies = new ArrayList<>();

        HttpCookie cookie2 = new HttpCookie("refresh", "0");
        cookie2.setDomain("sy.tongyongbei.com");
        cookie2.setPath("/");
        cookie2.setHttpOnly(false);
        cookie2.setSecure(false);

        HttpCookie cookie3 = new HttpCookie("user32131", "eqweqwe");
        cookie3.setDomain(".tongyongbei.com");
        cookie3.setPath("/");
        cookie3.setHttpOnly(false);
        cookie3.setSecure(false);

        HttpCookie cookie4 = new HttpCookie("wengine_vpn_ticketwvpn_upc_edu_cn", "77a24934ed5d38be");
        cookie4.setDomain(".sy.tongyongbei.com");
        cookie4.setPath("/");
        cookie4.setHttpOnly(true);
        cookie4.setSecure(true);


        //   cookies.add(cookie2);
        //   cookies.add(cookie3);
        cookies.add(cookie4);


        try (final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED)) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.waitForBackgroundJavaScript(600 * 1000);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());

            webClient.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            webClient.addRequestHeader("Accept-Encoding", "gzip, deflate, br");
            webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.9");
            webClient.addRequestHeader("Cache-Control", "max-age=0");
            webClient.addRequestHeader("Connection", "keep-alive");
            webClient.addRequestHeader("Host", "sy.tongyongbei.com");
            webClient.addRequestHeader("Referer", "https://sy.tongyongbei.com/https/77726476706e69737468656265737421e7e056d2373b7d5c7f1fc7af9758/apy/9445d6e6deb7c9d2ae9735f5515a12b1b299925f0f1381efbdfb.html");
            webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
            webClient.addRequestHeader("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
            webClient.addRequestHeader("sec-ch-ua-mobile", "?0");
            webClient.addRequestHeader("sec-ch-ua-platform", "macOS");
            webClient.addRequestHeader("Sec-Fetch-Dest", "document");
            webClient.addRequestHeader("Sec-Fetch-Mode", "navigate");
            webClient.addRequestHeader("Sec-Fetch-Site", "same-origin");
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
            DomElement element = page.getElementById("gridleft");
            System.out.println(element.asNormalizedText());

        } catch (IOException e) {
            log.info("HtmlUnit获取页面出错", e);
        }
        //  webClient.addCookie();
    }
}
