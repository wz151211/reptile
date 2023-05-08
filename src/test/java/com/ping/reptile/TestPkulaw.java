package com.ping.reptile;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.common.collect.Lists;
import com.ping.reptile.model.vo.Item;
import com.ping.reptile.model.vo.Node;
import com.ping.reptile.model.vo.Pkulaw;
import com.ping.reptile.model.vo.Theme;
import com.ping.reptile.service.PkulawService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.HashMap;

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
                //    .fieldNodes(Lists.newArrayList(category, punishmentDate))
                .clusterFilters(new HashMap<>())
                .groupBy(new HashMap<>())
                .build();
        JSONConfig config = new JSONConfig();
        config.setOrder(true);
        String jsonStr = JSONUtil.toJsonStr(pkulaw, config);
        System.out.println(jsonStr);
        HttpResponse response = HttpRequest.post(url)
                .timeout(-1)
                .body("{\"orderbyExpression\":\"PunishmentDate Desc\",\"pageIndex\":0,\"pageSize\":10,\"fieldNodes\":[{\"type\":\"select\",\"order\":4,\"showText\":\"主题分类\",\"fieldName\":\"Category\",\"combineAs\":2,\"fieldItems\":[{\"value\":[\"003\"],\"keywordTagData\":[\"003\"],\"order\":0,\"combineAs\":2,\"items\":[{\"value\":\"003\",\"name\":\"环保\",\"text\":\"环保\",\"path\":\"003\"}],\"filterNodes\":[]}]},{\"type\":\"daterange\",\"order\":5,\"showText\":\"处罚日期\",\"fieldName\":\"PunishmentDate\",\"combineAs\":2,\"range\":\"\",\"fieldItems\":[{\"order\":0,\"combineAs\":1,\"start\":\"2018.12.21\",\"end\":\"2018.12.26\",\"values\":[\"2018.09.20\",\"2018.09.28\"]}]}],\"clusterFilters\":{},\"groupBy\":{}}")
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/json")
                .header("Host", "sy.tongyongbei.com")
                .header("Origin", " https://sy.tongyongbei.com")
                .header("Referer", "https://sy.tongyongbei.com/https/77726476706e69737468656265737421e7e056d2373b7d5c7f1fc7af9758/advanced/penalty/")
                .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFXzZpaDM1eVRMSk1pZUkwdnFnOU1tVFFySjZSY1VTeGlYZU5kY01hb1lrIn0.eyJleHAiOjE2ODI3NTQ3MDAsImlhdCI6MTY4Mjc1MjkwMCwiYXV0aF90aW1lIjoxNjgyNzMwOTE4LCJqdGkiOiIzNzE0ZTk5MC05N2JjLTQ5NTMtOWJmNy0zNzZiYzhiZmQ2MzIiLCJpc3MiOiJodHRwczovL2Nhcy5wa3VsYXcuY29tL2F1dGgvcmVhbG1zL2ZhYmFvIiwic3ViIjoiYmVhYmRkMTAtZGNiMi00N2ZmLWEyYzktOWUxYWY0NWIxYTVjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGt1bGF3Iiwic2Vzc2lvbl9zdGF0ZSI6IjkwNDZkNGEwLWY3NTktNDllYy1iM2E2LWRhMWM5M2IyNDk5YyIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZXhhbSIsInJlZmVyZW5jZSIsIue7hOeuoeeQhuWRmCIsInByb2N1cmF0b3JhdGUiLCJqb3VybmFsIiwibGF3IiwiY2FzZXMiLCJsYXdmaXJtIiwicGVuYWx0eSIsImVuZ2xpc2giLCJ2aWRlbyIsImNhc2UiXX0sInNjb3BlIjoib3BlbmlkIHBrdWxhdy1leHRlbnNpb25zIHY2IHJvbGVzIGVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInBob25lTnVtYmVyIjoiODI2NjgyNjYiLCJsb2dpblR5cGUiOiJpcCIsIm5pY2tuYW1lIjoi5Lit5Zu955-z5rK55aSn5a2m77yI5Y2O5Lic77yJIiwiZXh0ZW5zaW9uX3JvbGVzIjp7IndlaXhpbiI6WyJleGFtIiwicmVmZXJlbmNlIiwicHJvY3VyYXRvcmF0ZSIsImpvdXJuYWwiLCJsYXciLCJjYXNlcyIsImxhd2Zpcm0iLCJwZW5hbHR5IiwiZW5nbGlzaCIsInZpZGVvIiwiY2FzZSJdfSwicHJlZmVycmVkX3VzZXJuYW1lIjoi5Lit5Zu955-z5rK55aSn5a2m77yI5Y2O5Lic77yJIiwiZW1haWwiOiIyNTgzODA2MTMzQHFxLmNvbSJ9.W-xgNdwFYEkL5lta9VQ9bVItmzmI-2Ffr9liMC6cV9dagIzYu0gvC0LyozTsU5kneCDoqjR32WU-fl6MgSPAwSmetn9AmFLvpjdy6TVnAWT3OXUBEKCJLFL6ZHaiAxAcC2vuCvXuZKv84pzOANiMWNYvBGCrHR1AWzfe1q9CawSZpIVrbel9nq9P-j1bRzZy3GAgBhZdbokIfDGRZktHFZbgABWbQL0IMC0cInKdW1td_JsYZNdjIpKZni2PXkR8TAfhu5De2isPYBFr0T4nCxx1QGWqMJPxnPWZkT8hAzie630RCuUvMYruoEF1UQv9vMJeI427o7YVIWrl1X4WPQ")
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
            System.out.println(jsonObject);
        }
    }


    @Test
    public void testDetail() throws IOException {
        //   String url = "https://www.pkulaw.com/apy/9445d6e6deb7c9d28642a8afbee2f1a7c8b6b41f8d538dc5bdfb.html";
        String url = "https://sy.tongyongbei.com/https/77726476706e69737468656265737421e7e056d2373b7d5c7f1fc7af9758/apy/9445d6e6deb7c9d20bdbbeec3676eb13ba37d3e0c8bc7259bdfb.html";
        //  System.out.println(response.body());
        try (final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED)) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.waitForBackgroundJavaScript(600 * 1000);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());

//           webClient.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
//            webClient.addRequestHeader("Accept-Encoding", "gzip, deflate, br");
//            webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.9");
//            webClient.addRequestHeader("Connection", "keep-alive");
//            webClient.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//            webClient.addRequestHeader("Host", " www.pkulaw.com");
//            webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
//            webClient.addRequestHeader("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
//            webClient.addRequestHeader("sec-ch-ua-mobile", "?0");
//            webClient.addRequestHeader("sec-ch-ua-platform", "Windows");
//            webClient.addRequestHeader("Sec-Fetch-Dest", "document");
//            webClient.addRequestHeader("Sec-Fetch-Mode", "navigate");
//            webClient.addRequestHeader("Sec-Fetch-Site", "none");
//            webClient.addRequestHeader("Sec-Fetch-User", "?1");
//            webClient.addRequestHeader("Upgrade-Insecure-Requests", "1");


            CookieManager cookieManager = new CookieManager();

         //   webClient.setCookieManager(cookieManager);
            HtmlPage page = webClient.getPage(url);
            String text = page.getBody().asNormalizedText();
            String s = page.asXml();
            page.getElementById("gridleft");
            System.out.println(text);
        } catch (IOException e) {
            log.info("HtmlUnit获取页面出错", e);
        }
        //  webClient.addCookie();
    }

    @Test
    public void testPku(){
        PkulawService pkulawService = new PkulawService();
        pkulawService.list();
    }
}
