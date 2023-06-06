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
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.ping.reptile.model.entity.PkuConfigEntity;
import com.ping.reptile.pkulaw.PkulawServiceBak;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TestPkulaw {
    @Test
    public void testList() {
        String url = "https://www.pkulaw.com/searchingapi/list/advanced/chl";
        JSONConfig config = new JSONConfig();
        config.setOrder(true);
        String jsonStr = JSONUtil.toJsonStr("", config);
        System.out.println(jsonStr);
        HttpCookie cookie = new HttpCookie("pkulaw_v6_sessionid", "m4lzcyoa4su5x1eewfxdikh5");
        cookie.setDomain("www.pkulaw.com");
        cookie.setPath("/");
        HttpResponse response = HttpRequest.post(url)
                .timeout(-1)
                .cookie(cookie)
                .body(JSON.toJSONString(JSON.parseObject("{\"orderbyExpression\":\"IkBoost Desc,IssueDate Desc,EffectivenessSort Asc,TitleBoost Desc,IsOriginal Desc,DocumentNOSort Desc\",\"pageIndex\":0,\"pageSize\":10,\"fieldNodes\":[],\"clusterFilters\":{},\"groupBy\":{}}")))
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/json")
                .header("Host", "www.pkulaw.com")
                .header("Origin", "https://www.pkulaw.com")
                .header("Referer", "https://www.pkulaw.com/advanced/law/chl")
                .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFXzZpaDM1eVRMSk1pZUkwdnFnOU1tVFFySjZSY1VTeGlYZU5kY01hb1lrIn0.eyJleHAiOjE2ODQ2NjA5OTksImlhdCI6MTY4NDY1OTE5OSwiYXV0aF90aW1lIjoxNjgzNjIxNTk2LCJqdGkiOiI0YjI0ZGMzZS0yMjI3LTRiZTktYjE5ZS0xMWI3ODQ5ZDc3OGEiLCJpc3MiOiJodHRwczovL2Nhcy5wa3VsYXcuY29tL2F1dGgvcmVhbG1zL2ZhYmFvIiwic3ViIjoiZTIyMzQyMTUtMmE1ZC00M2RiLWJlZjgtZWEyYTE1M2MzMzhiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGt1bGF3Iiwic2Vzc2lvbl9zdGF0ZSI6ImNiMzJjMWRjLWJhNjMtNDlhYy1iZWJmLThjMjIyYThiNzMyZiIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZXhhbSIsInJlZmVyZW5jZSIsImpvdXJuYWwiLCJsYXciLCJjYXNlcyIsImxhd2Zpcm0iLCJwZW5hbHR5IiwiZW5nbGlzaCIsInZpZGVvIiwiY3JpbWluYWwiLCJjYXNlIl19LCJzY29wZSI6Im9wZW5pZCBwa3VsYXctZXh0ZW5zaW9ucyB2NiByb2xlcyBlbWFpbCBwcm9maWxlIiwiZ3JvdXBVc2VySWQiOiIzNGMyZTcxYy04NWQ0LTQzNTgtOWJkNC0yODQ4NjcxOTgzNjgiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInBob25lTnVtYmVyIjoiMTU4MTEzMjY3MDUiLCJsb2dpblR5cGUiOiJwYXNzd29yZCIsImdyb3VwVXNlck5hbWUiOiLljJfkuqzlpKflraYiLCJuYW1lIjoi6JGj5YWI55SfIiwibmlja25hbWUiOiLokaPlhYjnlJ8iLCJpc0dyb3VwVXNlciI6IjAiLCJleHRlbnNpb25fcm9sZXMiOnt9LCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ3eDIwMjIwNTEyMTAwMDQ4MjY2NzEiLCJnaXZlbl9uYW1lIjoi6JGj5YWI55SfIiwiZW1haWwiOiIyOTY2NDMwNDNAcXEuY29tIn0.ZrSXSj9rYW35uLFCOvq9mJ1Rn7pUlAojcmg88avoWa9PIeM709LtBjcut3Ve9K_nuFYeBS0qurTlsteHSy_Uu_y7QHmIQ9aKB5BZIe-nUoAXQngjWbr1ZyTT2LsRuqPAblKsrzkI4YvVqNcIvCdlbm1sDyHvRcyT2_vxxVOJ93o8qTBvBreiW7s4Z1BVpMPKmGfNpX8g8EM6ZKpWlO1DYOoNnZYSaBTt5QGKRiCOm2PtAbjHV0Ui5K1t1gINCgK2ub1tRak-_QgDOLxZErHrURbSHt9MsfzSjKISksGK6j3fkzVI54NZydTuQQa0ktNpYyEUNmkBoUrIe3TUfeu9rw")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
                .header("Sec-Ch-Ua", "\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"")
                .header("Sec-Ch-Ua-Mobile", "?0")
                .header("Sec-Ch-Ua-Platform", "Windows")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Customrequest", "CustomRequest/list/advanced")
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
        String url = "https://www.pkulaw.com/apy/9445d6e6deb7c9d24d21ee942f0dcc3de85b1cd12c47cc92bdfb.html";
        //  System.out.println(response.body());
        try (final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED)) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.waitForBackgroundJavaScript(600 * 1000);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());

            webClient.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            webClient.addRequestHeader("Accept-Encoding", "gzip, deflate, br");
            webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.9");
            webClient.addRequestHeader("Connection", "keep-alive");
            webClient.addRequestHeader("Content-Type", "application/json");
            webClient.addRequestHeader("Host", "www.pkulaw.com");
            webClient.addRequestHeader("Origin", "https://www.pkulaw.com");
            webClient.addRequestHeader("Referer", "https://www.pkulaw.com/advanced/law/chl");
            //  webClient.addRequestHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFXzZpaDM1eVRMSk1pZUkwdnFnOU1tVFFySjZSY1VTeGlYZU5kY01hb1lrIn0.eyJleHAiOjE2ODQ2NjA5OTksImlhdCI6MTY4NDY1OTE5OSwiYXV0aF90aW1lIjoxNjgzNjIxNTk2LCJqdGkiOiI0YjI0ZGMzZS0yMjI3LTRiZTktYjE5ZS0xMWI3ODQ5ZDc3OGEiLCJpc3MiOiJodHRwczovL2Nhcy5wa3VsYXcuY29tL2F1dGgvcmVhbG1zL2ZhYmFvIiwic3ViIjoiZTIyMzQyMTUtMmE1ZC00M2RiLWJlZjgtZWEyYTE1M2MzMzhiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGt1bGF3Iiwic2Vzc2lvbl9zdGF0ZSI6ImNiMzJjMWRjLWJhNjMtNDlhYy1iZWJmLThjMjIyYThiNzMyZiIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZXhhbSIsInJlZmVyZW5jZSIsImpvdXJuYWwiLCJsYXciLCJjYXNlcyIsImxhd2Zpcm0iLCJwZW5hbHR5IiwiZW5nbGlzaCIsInZpZGVvIiwiY3JpbWluYWwiLCJjYXNlIl19LCJzY29wZSI6Im9wZW5pZCBwa3VsYXctZXh0ZW5zaW9ucyB2NiByb2xlcyBlbWFpbCBwcm9maWxlIiwiZ3JvdXBVc2VySWQiOiIzNGMyZTcxYy04NWQ0LTQzNTgtOWJkNC0yODQ4NjcxOTgzNjgiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInBob25lTnVtYmVyIjoiMTU4MTEzMjY3MDUiLCJsb2dpblR5cGUiOiJwYXNzd29yZCIsImdyb3VwVXNlck5hbWUiOiLljJfkuqzlpKflraYiLCJuYW1lIjoi6JGj5YWI55SfIiwibmlja25hbWUiOiLokaPlhYjnlJ8iLCJpc0dyb3VwVXNlciI6IjAiLCJleHRlbnNpb25fcm9sZXMiOnt9LCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ3eDIwMjIwNTEyMTAwMDQ4MjY2NzEiLCJnaXZlbl9uYW1lIjoi6JGj5YWI55SfIiwiZW1haWwiOiIyOTY2NDMwNDNAcXEuY29tIn0.ZrSXSj9rYW35uLFCOvq9mJ1Rn7pUlAojcmg88avoWa9PIeM709LtBjcut3Ve9K_nuFYeBS0qurTlsteHSy_Uu_y7QHmIQ9aKB5BZIe-nUoAXQngjWbr1ZyTT2LsRuqPAblKsrzkI4YvVqNcIvCdlbm1sDyHvRcyT2_vxxVOJ93o8qTBvBreiW7s4Z1BVpMPKmGfNpX8g8EM6ZKpWlO1DYOoNnZYSaBTt5QGKRiCOm2PtAbjHV0Ui5K1t1gINCgK2ub1tRak-_QgDOLxZErHrURbSHt9MsfzSjKISksGK6j3fkzVI54NZydTuQQa0ktNpYyEUNmkBoUrIe3TUfeu9rw");
            webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");
            webClient.addRequestHeader("Sec-Ch-Ua", "\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"");
            webClient.addRequestHeader("Sec-Ch-Ua-Mobile", "?0");
            webClient.addRequestHeader("Sec-Ch-Ua-Platform", "\"Windows\"");
            webClient.addRequestHeader("Sec-Fetch-Dest", "document");
            webClient.addRequestHeader("Sec-Fetch-Mode", "navigate");
            webClient.addRequestHeader("Sec-Fetch-Site", "same-origin");
            webClient.addRequestHeader("Sec-Fetch-User", "?1");
            webClient.addRequestHeader("Upgrade-Insecure-Requests", "1");
            // webClient.addRequestHeader("Customrequest", "CustomRequest/list/advanced");


            CookieManager cookieManager = new CookieManager();
            Cookie cookie = new Cookie("www.pkulaw.com", "pkulaw_v6_sessionid", "m4lzcyoa4su5x1eewfxdikh5");
            cookieManager.addCookie(cookie);
            webClient.setCookieManager(cookieManager);
            HtmlPage page = webClient.getPage(url);
            String text = page.getBody().asNormalizedText();
            String s = page.asXml();
            System.out.println("--------------------------");
            System.out.println(s);
            System.out.println("--------------------------");
            DomElement gridleft = page.getElementById("gridleft");
            String text1 = gridleft.asNormalizedText();
            System.out.println(text1);
        } catch (IOException e) {
            log.info("HtmlUnit获取页面出错", e);
        }
        //  webClient.addCookie();
    }

    @Test
    public void testPku() {
        PkulawServiceBak pkulawService = new PkulawServiceBak();
        pkulawService.list();
    }

    @Test
    public void test111(){
        String s = " 1 1 ";
        System.out.println(s.trim());
    }
    @Test
    public void refreshtoken() {
        HttpCookie cookie = new HttpCookie("pkulaw_v6_sessionid", "4kw5tuntdf3thcgsrelhugqa");
        cookie.setDomain("www.pkulaw.com");
        cookie.setPath("/");

        HttpCookie cookie1 = new HttpCookie("SUB", "e2234215-2a5d-43db-bef8-ea2a153c338b");
        cookie1.setDomain(".pkulaw.com");
        cookie1.setPath("/");

        HttpCookie cookie2 = new HttpCookie("authormes", "bf20bed2d11ba001ebcb5ea4f0f65183b299c26aa1900a84b7822b69c68f18c1a5404d51976aefdabdfb");
        cookie2.setDomain("www.pkulaw.com");
        cookie2.setPath("/");

        String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFXzZpaDM1eVRMSk1pZUkwdnFnOU1tVFFySjZSY1VTeGlYZU5kY01hb1lrIn0.eyJleHAiOjE2ODQ4NTMwNTYsImlhdCI6MTY4NDg1MTI1NiwiYXV0aF90aW1lIjoxNjg0NzIxODAzLCJqdGkiOiJjMmRiYThjYy0zNDUxLTRjZjItOWRiNC1mZjc3MzQzMzNhMTYiLCJpc3MiOiJodHRwczovL2Nhcy5wa3VsYXcuY29tL2F1dGgvcmVhbG1zL2ZhYmFvIiwic3ViIjoiZTIyMzQyMTUtMmE1ZC00M2RiLWJlZjgtZWEyYTE1M2MzMzhiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGt1bGF3Iiwic2Vzc2lvbl9zdGF0ZSI6IjNkYmNhMWRhLTE2NjEtNGFhNC1iY2JkLTFiZTE0OWJiYzc1OCIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZXhhbSIsInJlZmVyZW5jZSIsImpvdXJuYWwiLCJsYXciLCJjYXNlcyIsImxhd2Zpcm0iLCJwZW5hbHR5IiwiZW5nbGlzaCIsInZpZGVvIiwiY3JpbWluYWwiLCJjYXNlIl19LCJzY29wZSI6Im9wZW5pZCBwa3VsYXctZXh0ZW5zaW9ucyB2NiByb2xlcyBlbWFpbCBwcm9maWxlIiwiZ3JvdXBVc2VySWQiOiIzNGMyZTcxYy04NWQ0LTQzNTgtOWJkNC0yODQ4NjcxOTgzNjgiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInBob25lTnVtYmVyIjoiMTU4MTEzMjY3MDUiLCJsb2dpblR5cGUiOiJwYXNzd29yZCIsImdyb3VwVXNlck5hbWUiOiLljJfkuqzlpKflraYiLCJuYW1lIjoi6JGj5YWI55SfIiwibmlja25hbWUiOiLokaPlhYjnlJ8iLCJpc0dyb3VwVXNlciI6IjAiLCJleHRlbnNpb25fcm9sZXMiOnt9LCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ3eDIwMjIwNTEyMTAwMDQ4MjY2NzEiLCJnaXZlbl9uYW1lIjoi6JGj5YWI55SfIiwiZW1haWwiOiIyOTY2NDMwNDNAcXEuY29tIn0.iwSuAR3dfF-Q91PpPyLB9daHUW8z3JLDrJAyha-SXlEYiz1NoWwdJk22ufTa9KzdMkEG3ZL7RL30f9SUhOuHi6M-6Ozi7YCtIuqO5i7H2uP2vDQTuHqpHIDhcrK-JfysMy3KfTfXueVyZ7_Qp-iYB5JwnTWxKQVRKThySmniHRbjigf7TuEiOvs05NJrXX7xxRU09I7qBCi_SJ8NJkGSP0Il8Qw3XSZe9NQ0Y57SvBg136K5qWDalekFyETkHpknVzH4tBvBDl8Rohz32FevIfc8ioCyh8IquZvcwA9Vf5FOif3wn2ak9_fuSaSOmc3NHKbpWCv1kIu0R218dmHBfA";
        Map<String, String> params = new HashMap<>();
        params.put("access_token", token.replace("Bearer ", ""));
        String url = "https://www.pkulaw.com/gateway/account/auth/refreshtoken";
        HttpResponse execute = HttpRequest.post(url)
                .timeout(-1)
                .cookie(cookie, cookie1, cookie2)
                .body(JSON.toJSONString(params))
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/json")
                .header("Host", "www.pkulaw.com")
                .header("Origin", "https://www.pkulaw.com")
                .header("Referer", "https://www.pkulaw.com/advanced/law/lar")
                .header("Authorization", token)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
                .header("Sec-Ch-Ua", "\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"")
                .header("Sec-Ch-Ua-Platform", "\"Windows\"")
                .header("Sec-Ch-Ua-Mobile", "?0")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                //.header("Upgrade-Insecure-Requests", "1")
                //  .header("Customrequest", "CustomRequest/list/advanced")
                .execute();
        System.out.println(execute.body());
    }

    @Test
    public void test12() {
        HttpCookie cookie = new HttpCookie("pkulaw_v6_sessionid", "0zelu5mcww31210mlshj1tjj");
        cookie.setDomain("www.pkulaw.com");
        cookie.setPath("/");

        HttpCookie cookie2 = new HttpCookie("authormes", "8b5f7d0c0516a653bbbdca910f905efabf6ec7c714ff255250052f00aef8292c546844242d35ec63bdfb");
        cookie.setDomain("www.pkulaw.com");
        cookie.setPath("/");


        HttpCookie cookie1 = new HttpCookie("gr_user_id", "4d3d27b0-eb97-469d-8cba-d9fa6bc63bac");
        cookie1.setDomain(".pkulaw.com");
        cookie1.setPath("/");

        HttpCookie cookie3 = new HttpCookie("SUB", "96339bd3-d843-48e3-955d-089f38fb7509");
        cookie1.setDomain(".pkulaw.com");
        cookie1.setPath("/");

        List<HttpCookie> cookies = new ArrayList<>();
        cookies.add(cookie);
        cookies.add(cookie1);
        cookies.add(cookie2);
        cookies.add(cookie3);
        PkuConfigEntity config = new PkuConfigEntity();
        config.setAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");
        config.setChUa("\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"");
        config.setAuthorization("Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFXzZpaDM1eVRMSk1pZUkwdnFnOU1tVFFySjZSY1VTeGlYZU5kY01hb1lrIn0.eyJleHAiOjE2ODU0MTQ4MzIsImlhdCI6MTY4NTQxMzAzMiwiYXV0aF90aW1lIjoxNjg1MzI5NDc5LCJqdGkiOiI4NGE3MTU2OC04MTJmLTQwMDYtYjlhOS1hN2I4ZTZjMjViMGQiLCJpc3MiOiJodHRwczovL2Nhcy5wa3VsYXcuY29tL2F1dGgvcmVhbG1zL2ZhYmFvIiwic3ViIjoiOTYzMzliZDMtZDg0My00OGUzLTk1NWQtMDg5ZjM4ZmI3NTA5IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGt1bGF3Iiwic2Vzc2lvbl9zdGF0ZSI6IjZhZDc1NDJjLWM3NjItNGJlNi05MzY2LWUxOTE1MjYyNDNkZCIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZXhhbSIsInJlZmVyZW5jZSIsInByb2N1cmF0b3JhdGUiLCJqb3VybmFsIiwibGF3IiwiY2FzZXMiLCJsYXdmaXJtIiwiZW5nbGlzaCIsInZpZGVvIiwiY3JpbWluYWwiLCJjYXNlIl19LCJzY29wZSI6Im9wZW5pZCBwa3VsYXctZXh0ZW5zaW9ucyB2NiByb2xlcyBlbWFpbCBwcm9maWxlIiwiZ3JvdXBVc2VySWQiOiI3ZjBmMzMyZC01N2FiLTQyMjgtODEwMy02MjhlOWRiMzM0NGQiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInBob25lTnVtYmVyIjoiMTgxOTQzNDcwOTAiLCJsb2dpblR5cGUiOiJwYXNzd29yZCIsImdyb3VwVXNlck5hbWUiOiLmuIXljY7lpKflraYiLCJuaWNrbmFtZSI6IuWSjOWQieWonCIsImlzR3JvdXBVc2VyIjoiMCIsImV4dGVuc2lvbl9yb2xlcyI6e30sInByZWZlcnJlZF91c2VybmFtZSI6InBob25lMjAyMTA3MDExMzA3NDc1NTQyOSIsImVtYWlsIjoiZWlkYWZhYmFvMTExQDE2My5jb20ifQ.HOZdDUPTgP5v6JEmZJ68bRZx3Op-U7jE5c96g_Xg7XAKWk085Bc60g9zLqLq0RjZN3-DoRdkMg83I7fTN_gNdhquUJnggHXW_j0ogdBd5BEn-evkWR69bMiVBdgE2LkRxWHXKFnuQuOmBoXHixtIPOJhmrOPsZuOXxgbBuqtL7mQjAvlmOritisuhXOAO8yBrL3p8dpRYXqqaSbW1LFFU05RVvjS9cln4dYL9mQOw_xQwcxlrWoe7rXTsO-pz8DC2eoRBYTOjij1d1YMFiweBLq9EbIBEQ4_6xFevPLu1QBt1jeuPsQaGx-X0Gif0wbfP6kt3eo4_6R8t8BQGDc1Dw");
        config.setPlatform("\"Windows\"");
        String url = "https://www.pkulaw.com/gateway/account/auth/refreshtoken";
        Map<String, String> params = new HashMap<>();
        params.put("access_token", config.getAuthorization().replace("Bearer ", ""));
        HttpResponse request = request(url, JSON.toJSONString(params), config, cookies);
        if (StringUtils.isNotEmpty(request.body())) {
            log.info("返回值为={}",request.body());
            JSONObject object = JSON.parseObject(request.body());
            String accessToken = object.getString("access_token");
            log.info("刷新token={}", accessToken);
            if (StringUtils.isNotEmpty(accessToken)) {
                config.setAuthorization("Bearer " + accessToken);
                PkuConfigEntity entity = new PkuConfigEntity();
                entity.setId(config.getId());
                entity.setAuthorization("Bearer " + accessToken);

            } else {
                log.info("获取刷新token失败={}", request.body());
            }
        }

    }

    public HttpResponse request(String url, String params, PkuConfigEntity config, List<HttpCookie> cookieList) {
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
                .header("Authorization", config.getAuthorization())
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
