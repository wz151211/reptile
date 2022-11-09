package com.ping.reptile;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.junit.jupiter.api.Test;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: W.Z
 * @Date: 2022/11/9 22:39
 */
public class TestJuFa {

    @Test
    public void testList() {

        String url = "https://www.jufaanli.com/home/search/searchJson";
        Map<String, Object> prams = new HashMap<>();
        prams.put("page", "2");
        prams.put("searchNum", "10");
        prams.put("searchTime", System.currentTimeMillis());
        prams.put("nowReason", "0");
        prams.put("sortTyp", "sortType");
        prams.put("keyword", "盗窃");
        prams.put("TypeKey", "1:盗窃");
        prams.put("reason_id", "1769");
        prams.put("focus_name", "案例搜索：盗窃 , 案由：盗窃罪");
        prams.put("last_search_uuid", "be16e90fc6305110a2ded1539bb997ed");
        prams.put("signType", "2");
        prams.put("case_level_id", "1");
        prams.put("search_action_type", "1");

        HttpCookie cookie = new HttpCookie("BJYSESSION", "1na89vh8vld8hrga1u58s4l017");
        cookie.setDomain(".jufaanli.com");
        cookie.setPath("/");
        cookie.setHttpOnly(true);


        HttpCookie cookie2 = new HttpCookie("t", "13b92b76d41659f27e312181bd3bca0f");
        cookie2.setDomain("www.jufaanli.com");
        cookie2.setPath("/");

        HttpCookie cookie3 = new HttpCookie("vid", "10-1");
        cookie3.setDomain("www.jufaanli.com");
        cookie3.setPath("/");



        HttpCookie cookie4 = new HttpCookie("HWWAFSESID", "d70527698364bf2495");
        cookie4.setDomain("www.jufaanli.com");
        cookie4.setPath("/");

        HttpCookie cookie5 = new HttpCookie("HWWAFSESTIME", "1668004250333");
        cookie5.setDomain("www.jufaanli.com");
        cookie5.setPath("/");


        HttpCookie cookie6 = new HttpCookie("Hm_lvt_7d935fee641e9bdd8fd6b28e9a2b62dc", "1667823310,1668004253");
        cookie6.setDomain(".jufaanli.com");
        cookie6.setPath("/");

        HttpCookie cookie7 = new HttpCookie("tf", "6b0396f0a1cc23f4b30a9f021a1293bb");
        cookie7.setDomain("www.jufaanli.com");
        cookie7.setPath("/");

        HttpCookie cookie8 = new HttpCookie("Hm_lpvt_7d935fee641e9bdd8fd6b28e9a2b62dc", "1668005096");
        cookie8.setDomain(".jufaanli.com");
        cookie8.setPath("/");

        HttpCookie cookie9 = new HttpCookie("is_remember", "1");
        cookie9.setDomain(".jufaanli.com");
        cookie9.setPath("/");


        HttpCookie cookie10 = new HttpCookie("login_time", "2022-11-09+22%3A32%3A02");
        cookie10.setDomain(".jufaanli.com");
        cookie10.setPath("/");



        HttpResponse response = HttpRequest.post(url)
                .form(prams)
                .timeout(-1)
                .cookie(cookie,cookie2,cookie3,cookie4,cookie5,cookie6,cookie7,cookie8,cookie9,cookie10)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header(Header.HOST, "www.jufaanli.com")
                .header("Origin", "https://www.jufaanli.com")
                .header("Referer", "https://www.jufaanli.com/new_searchcase?TypeKey=1%3A%E7%9B%97%E7%AA%83+8%3Areason_1769_%E7%9B%97%E7%AA%83%E7%BD%AA&search_uuid=012b4fc5efd520f74dfb13fb9c1340a0&Page_pt=2")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"107\", \"Chromium\";v=\"107\", \"Not=A?Brand\";v=\"24\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"macOS\"")
                .header("X-Requested-With", "XMLHttpRequest")
                .execute();

        System.out.println(response.body());
    }
}
