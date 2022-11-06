package com.ping.reptile;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.common.collect.Lists;
import com.ping.reptile.model.vo.Item;
import com.ping.reptile.model.vo.Node;
import com.ping.reptile.model.vo.Pkulaw;
import com.ping.reptile.model.vo.Theme;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class TestPkulaw {
    @Test
    public void testList() {
        // String url = " https://www.pkulaw.com/searchingapi/list/advanced/apy";
        String url = "https://sy.tongyongbei.com/https/77726476706e69737468656265737421e7e056d2373b7d5c7f1fc7af9758/searchingapi/list/advanced/apy?vpn-12-o2-www.pkulaw.com";
        Node punishmentDate = Node.builder().type("daterange").order(5).showText("处罚日期").fieldName("PunishmentDate").combineAs(2).fieldItems(Lists.newArrayList(Item.builder()
                .order(0)
                .combineAs(1)
                .start("2018.01.04")
                .end("2018.01.07")
                .values(Lists.newArrayList("2018.01.01", "2018.01.03"))
                .build())).build();

        Node category = Node.builder().type("select").order(4).showText("主题分类").fieldName("Category").combineAs(2).fieldItems(Lists.newArrayList(Item.builder()
                .value(Lists.newArrayList("003"))
                .keywordTagData(Lists.newArrayList("003"))
                .order(0)
                .combineAs(2)
                .filterNodes(Lists.newArrayList())
                .items(Lists.newArrayList(Theme.builder().name("环保").value("003").text("环保").path("003").build()))
                .build())).build();

        Node.builder().type("select").order(6).showText("处罚种类").fieldName("PunishmentTypeNew").combineAs(2).fieldItems(Lists.newArrayList(Item.builder()
                .value(Lists.newArrayList("001", "002"))
                .keywordTagData(Lists.newArrayList("001,002"))
                .order(0)
                .combineAs(2)
                .items(Lists.newArrayList(Theme.builder().name("警告、通报批评").value("001").text("警告、通报批评").path("001").build(),
                        Theme.builder().name("罚款、没收违法所得、没收非法财物").value("002").text("罚款、没收违法所得、没收非法财物").path("002").build()
                ))
                .build()
        )).build();

        Pkulaw pkulaw = Pkulaw.builder()
                .orderbyExpression("PunishmentDate Desc")
                .pageIndex(0)
                .pageSize(10)
                .fieldNodes(Lists.newArrayList(category, punishmentDate))
                .clusterFilters(new HashMap<>())
                .groupBy(new HashMap<>())
                .build();
        JSONConfig config = new JSONConfig();
        config.setOrder(true);
        String jsonStr = JSONUtil.toJsonStr(pkulaw, config);
        System.out.println(jsonStr);
        HttpResponse response = HttpRequest.post(url)
                .timeout(-1)
                //   .body("{\"orderbyExpression\":\"PunishmentDate Desc\",\"pageIndex\":0,\"pageSize\":10,\"fieldNodes\":[{\"type\":\"daterange\",\"order\":5,\"showText\":\"处罚日期\",\"fieldName\":\"PunishmentDate\",\"combineAs\":2,\"fieldItems\":[{\"order\":0,\"combineAs\":1,\"start\":\"2018.12.21\",\"end\":\"2018.12.26\",\"values\":[\"2018.12.09\",\"2018.12.15\"]}]},{\"type\":\"select\",\"order\":4,\"showText\":\"主题分类\",\"fieldName\":\"Category\",\"combineAs\":2,\"fieldItems\":[{\"order\":0,\"combineAs\":2,\"items\":[{\"name\":\"环保\",\"value\":\"003\",\"text\":\"环保\",\"path\":\"003\"}],\"value\":[\"003\"],\"keywordTagData\":[\"003\"],\"filterNodes\":[]}]}],\"clusterFilters\":{},\"groupBy\":{}}\n")
                //     .body("{\"orderbyExpression\":\"PunishmentDate Desc\",\"pageIndex\":0,\"pageSize\":10,\"fieldNodes\":[{\"type\":\"select\",\"order\":4,\"showText\":\"主题分类\",\"fieldName\":\"Category\",\"combineAs\":2,\"fieldItems\":[{\"value\":[\"003\"],\"keywordTagData\":[\"003\"],\"order\":0,\"combineAs\":2,\"items\":[{\"value\":\"003\",\"name\":\"环保\",\"text\":\"环保\",\"path\":\"003\"}],\"filterNodes\":[]}]},{\"type\":\"daterange\",\"order\":5,\"showText\":\"处罚日期\",\"fieldName\":\"PunishmentDate\",\"combineAs\":2,\"range\":\"\",\"fieldItems\":[{\"order\":0,\"combineAs\":1,\"start\":\"2018.09.08\",\"end\":\"2018.09.10\",\"values\":[\"2018.09.07\",\"2018.09.07\"]}]}],\"clusterFilters\":{},\"groupBy\":{}}")
                // .cookie(cookies)
                .body("{\"orderbyExpression\":\"PunishmentDate Desc\",\"pageIndex\":0,\"pageSize\":10,\"fieldNodes\":[{\"type\":\"select\",\"order\":4,\"showText\":\"主题分类\",\"fieldName\":\"Category\",\"combineAs\":2,\"fieldItems\":[{\"value\":[\"003\"],\"keywordTagData\":[\"003\"],\"order\":0,\"combineAs\":2,\"items\":[{\"value\":\"003\",\"name\":\"环保\",\"text\":\"环保\",\"path\":\"003\"}],\"filterNodes\":[]}]},{\"type\":\"daterange\",\"order\":5,\"showText\":\"处罚日期\",\"fieldName\":\"PunishmentDate\",\"combineAs\":2,\"range\":\"\",\"fieldItems\":[{\"order\":0,\"combineAs\":1,\"start\":\"2018.12.21\",\"end\":\"2018.12.26\",\"values\":[\"2018.09.20\",\"2018.09.28\"]}]}],\"clusterFilters\":{},\"groupBy\":{}}")
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/json")
                .header("Host", "sy.tongyongbei.com")
                .header("Origin", " https://sy.tongyongbei.com")
                .header("Referer", "https://sy.tongyongbei.com/https/77726476706e69737468656265737421e7e056d2373b7d5c7f1fc7af9758/advanced/penalty/")
                .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFXzZpaDM1eVRMSk1pZUkwdnFnOU1tVFFySjZSY1VTeGlYZU5kY01hb1lrIn0.eyJleHAiOjE2NjU2NTE5NzMsImlhdCI6MTY2MzA2Mzk4NSwiYXV0aF90aW1lIjoxNjYzMDU5OTczLCJqdGkiOiIzYTIzZGI3NS05ZmEwLTRmY2MtOTA4ZS0wYTk5NzUyYTg2N2EiLCJpc3MiOiJodHRwczovL2Nhcy5wa3VsYXcuY29tL2F1dGgvcmVhbG1zL2ZhYmFvIiwic3ViIjoiYmVhYmRkMTAtZGNiMi00N2ZmLWEyYzktOWUxYWY0NWIxYTVjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGt1bGF3Iiwic2Vzc2lvbl9zdGF0ZSI6IjQzMGEyZGI3LTJlZDQtNGE2NC05OTQzLTFkZmU1ZGYyYjZjYSIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZXhhbSIsInJlZmVyZW5jZSIsIue7hOeuoeeQhuWRmCIsInByb2N1cmF0b3JhdGUiLCJsYXciLCJqb3VybmFsIiwibGF3ZmlybSIsInBlbmFsdHkiLCJlbmdsaXNoIiwidmlkZW8iLCJjYXNlIl19LCJzY29wZSI6Im9wZW5pZCBwa3VsYXctZXh0ZW5zaW9ucyB2NiByb2xlcyBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwaG9uZU51bWJlciI6IjgyNjY4MjY2IiwibG9naW5UeXBlIjoiaXAiLCJuaWNrbmFtZSI6IuS4reWbveefs-ayueWkp-Wtpu-8iOWNjuS4nO-8iSIsImV4dGVuc2lvbl9yb2xlcyI6e30sInByZWZlcnJlZF91c2VybmFtZSI6IuS4reWbveefs-ayueWkp-Wtpu-8iOWNjuS4nO-8iSIsImVtYWlsIjoiMjU4MzgwNjEzM0BxcS5jb20ifQ.UbzefMaF_f6gCizEnAFvEix-i9ddnH0ZEJWNiQaRa7TbcVyLTR9-y6RJ6wxpIJW4IKDMjkC6ZCN5bnNEQUA9RWMaJ68X6Ta6kb8RqenjbnSiIIClUqxAJX0IgaLVuDpZ0MXbZHl9PX0NP7Eza2EvyXb68emlo7iVM06MeN0r4FJ05wfdzlkYyl2jix8dgASftZFbFaOPptMnlZFwyO5ZsZTiUw0u3HifgVipD55nGK2uy4W44FjdVv7QRZohV60rIYXPKB9fXXg9n4rAwoCE7hjfIVZBvatg6y4VJ5Cm6zdO2PhZIBoA8mUNqARGW4Yn3dtkojSGVwjV8tvaVikoPA")
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
        //   String url = "https://www.pkulaw.com/apy/9445d6e6deb7c9d28642a8afbee2f1a7c8b6b41f8d538dc5bdfb.html";
        String url = "https://sy.tongyongbei.com/https/77726476706e69737468656265737421e7e056d2373b7d5c7f1fc7af9758/apy/9445d6e6deb7c9d2cb4fbc39c8658efb1c7c311266691b08bdfb.html";
        List<HttpCookie> cookies = new ArrayList<>();
        HttpCookie cookie1 = new HttpCookie("Hm_lvt_8266968662c086f34b2a3e2ae9014bf8", "662819029,1663034065");
        cookie1.setDomain(".pkulaw.com");
        cookie1.setPath("/");
        cookie1.setHttpOnly(false);
        cookie1.setSecure(false);

        HttpCookie cookie2 = new HttpCookie("xCloseNew", "13");
        cookie2.setDomain("www.pkulaw.com");
        cookie2.setPath("/");
        cookie2.setHttpOnly(false);
        cookie2.setSecure(false);

        HttpCookie cookie3 = new HttpCookie("isClickDownLayer", "true");
        cookie3.setDomain("www.pkulaw.com");
        cookie3.setPath("/");
        cookie3.setHttpOnly(false);
        cookie3.setSecure(false);

        HttpCookie cookie4 = new HttpCookie("pkulaw_v6_sessionid", "iobfq2gn35rligaqy3djans3");
        cookie4.setDomain("www.pkulaw.com");
        cookie4.setPath("/");
        cookie4.setHttpOnly(true);
        cookie4.setSecure(false);


        HttpCookie cookie5 = new HttpCookie("KC_ROOT_LOGIN", "1");
        cookie5.setDomain(".pkulaw.com");
        cookie5.setPath("/");
        cookie5.setHttpOnly(false);
        cookie5.setSecure(false);

        HttpCookie cookie6 = new HttpCookie("KC_ROOT_LOGIN_LEGACY", "1");
        cookie6.setDomain(".pkulaw.com");
        cookie6.setPath("/");
        cookie6.setHttpOnly(false);
        cookie6.setSecure(false);

        HttpCookie cookie7 = new HttpCookie("userislogincookie", "true");
        cookie7.setDomain("www.pkulaw.com");
        cookie7.setPath("/");
        cookie7.setHttpOnly(false);
        cookie7.setSecure(false);

        HttpCookie cookie8 = new HttpCookie("authormes", "28d8de58ebc08f10635544ccfa4db1545dfd5836e823ac726b9ef843994c77300dbbf4b4de013855bdfb");
        cookie8.setDomain("www.pkulaw.com");
        cookie8.setPath("/");
        cookie8.setHttpOnly(false);
        cookie8.setSecure(false);

        HttpCookie cookie9 = new HttpCookie("Hm_up_8266968662c086f34b2a3e2ae9014bf8", "%7B%22ysx_yhqx_20220602%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22ysx_hy_20220527%22%3A%7B%22value%22%3A%2201%22%2C%22scope%22%3A1%7D%2C%22uid_%22%3A%7B%22value%22%3A%22b43a8dea-44d0-e811-9e87-5254ec69a56d%22%2C%22scope%22%3A1%7D%2C%22ysx_yhjs_20220602%22%3A%7B%22value%22%3A%222%22%2C%22scope%22%3A1%7D%7D");
        cookie9.setDomain("www.pkulaw.com");
        cookie9.setPath("/");
        cookie9.setHttpOnly(false);
        cookie9.setSecure(false);

        HttpCookie cookie10 = new HttpCookie("Hm_lpvt_8266968662c086f34b2a3e2ae9014bf8", "1663057853");
        cookie10.setDomain(".pkulaw.com");
        cookie10.setPath("/");
        cookie10.setHttpOnly(false);
        cookie10.setSecure(false);


        //   cookies.add(cookie1);
        //    cookies.add(cookie2);
        //   cookies.add(cookie3);
        cookies.add(cookie4);
        //    cookies.add(cookie5);
        //   cookies.add(cookie6);
        //   cookies.add(cookie7);
        //   cookies.add(cookie8);
        //   cookies.add(cookie9);
        //   cookies.add(cookie10);

        HttpCookie cookie12 = new HttpCookie("user32131", "eqweqwe");
        cookie12.setDomain(".tongyongbei.com");
        cookie12.setPath("/");
        cookie12.setHttpOnly(false);
        cookie12.setSecure(false);
        HttpResponse response = HttpRequest.post(url)
                .timeout(-1)
                .cookie(cookie12)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Host", "www.pkulaw.com")
                .header("Origin", "https://www.pkulaw.com")
                .header("Referer", "https://www.pkulaw.com/advanced/penalty/")
                .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFXzZpaDM1eVRMSk1pZUkwdnFnOU1tVFFySjZSY1VTeGlYZU5kY01hb1lrIn0.eyJleHAiOjE2NjU0NTE5MjMsImlhdCI6MTY2Mjg2NTU1OCwiYXV0aF90aW1lIjoxNjYyODU5OTIzLCJqdGkiOiI4MmJjOWNjNi03ZWM4LTRlZGQtODU1MS1hOWE0YTRhM2Y5MDEiLCJpc3MiOiJodHRwczovL2Nhcy5wa3VsYXcuY29tL2F1dGgvcmVhbG1zL2ZhYmFvIiwic3ViIjoiMDRmNjg2N2EtOGJhMy00YTFiLTgzNzAtMGIwZjU1MWY5YjFhIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGt1bGF3Iiwic2Vzc2lvbl9zdGF0ZSI6IjYyNDI1YWE5LTMzZTEtNGU2OC05ZjdjLTI3MGE3NDU5YjA5YiIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZXhhbSIsInJlZmVyZW5jZSIsIue7hOeuoeeQhuWRmCIsInByb2N1cmF0b3JhdGUiLCJsYXciLCJqb3VybmFsIiwibGF3ZmlybSIsInBlbmFsdHkiLCJlbmdsaXNoIiwidmlkZW8iLCJjYXNlIl19LCJzY29wZSI6Im9wZW5pZCBwa3VsYXctZXh0ZW5zaW9ucyB2NiByb2xlcyBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwaG9uZU51bWJlciI6IjgyNjY4MjY2IiwibG9naW5UeXBlIjoicGFzc3dvcmQiLCJuaWNrbmFtZSI6Iuilv-WMl-WGnOael-enkeaKgOWkp-WtpiIsImV4dGVuc2lvbl9yb2xlcyI6eyJ3ZWl4aW4iOlsiZXhhbSIsInJlZmVyZW5jZSIsInByb2N1cmF0b3JhdGUiLCJqb3VybmFsIiwibGF3IiwibGF3ZmlybSIsInBlbmFsdHkiLCJlbmdsaXNoIiwidmlkZW8iLCJjYXNlIl19LCJwcmVmZXJyZWRfdXNlcm5hbWUiOiLopb_ljJflhpzmnpfnp5HmioDlpKflraYiLCJlbWFpbCI6ImwxODY2OTMxNDA5OUAxNjMuY29tIn0.x-noYrAlbvn-LayJmhaA_u8yZem627SQqX3cEPJLhmOBCmnFLWRXoMWPvz14MW9mCQgsTeMDntN3dktY13ibMSO2S4KFEjK56Ya92DPEc-Ye9uaAvMaLKOAoPOAtKvEgh_yqFdkg_pZ6rwezHEWxPa3meJpn_GFwGV0yD0VynGJdFl1KqKXHDEtkxtNtuP2PaH43dSMinay3LtaBNiqwOPMP4fnv1dHpdm4sVfe-FWZs2jjPx4mtX15G-FHZaseK1Pbn7dV3Eu0WGE31DLUfFAzV7OXlALZsfx87souYqRdtAgpNR_AtHh1ahDSQUToMf2QQl1Ohgplid404-u00Jw")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "Windows")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-User", "?1")
                .header("Upgrade-Insecure-Requests", "1")
                .execute();

        //  System.out.println(response.body());
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
            HtmlPage page = webClient.getPage("https://www.pkulaw.com/apy/9445d6e6deb7c9d25ca0e12a0c87b929284e62cac681b2ecbdfb.html?way=listView");
            DomElement element = page.getElementById("gridleft");
            System.out.println(element.asNormalizedText());
        } catch (IOException e) {
            log.info("HtmlUnit获取页面出错", e);
        }
        //  webClient.addCookie();
    }
}
