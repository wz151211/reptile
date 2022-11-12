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
        prams.put("sortTyp", "caseWeight");
        prams.put("keyword", "家庭暴力");
        prams.put("TypeKey", "1:家庭暴力");
        prams.put("judgement_id", "1");
        prams.put("ajtz_id", "3950");
        prams.put("focus_name", "案例搜索：家庭暴力 , 文书性质：判决 , 基本案情：家庭暴力");
        prams.put("last_search_uuid", "5c297dd757863f575d1eb45d2b8bc1c9");
        prams.put("signType", "2");
        prams.put("case_level_id", "1");
        prams.put("search_action_type", "1");

        HttpCookie cookie = new HttpCookie("BJYSESSION", "7a60tfqt8d2l2eb14tql7ul5q6");
        cookie.setDomain(".jufaanli.com");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24 * 90);


        HttpCookie cookie2 = new HttpCookie("t", "4dff576d487540b0ce56c2b03032084a");
        cookie2.setDomain("www.jufaanli.com");
        cookie2.setPath("/");
        //cookie2.setMaxAge();

        HttpCookie cookie3 = new HttpCookie("vid", "10-2");
        cookie3.setDomain("www.jufaanli.com");
        cookie3.setPath("/");


        HttpCookie cookie4 = new HttpCookie("HWWAFSESID", "1f4657389f5720c03a");
        cookie4.setDomain("www.jufaanli.com");
        cookie4.setPath("/");

        HttpCookie cookie5 = new HttpCookie("HWWAFSESTIME", "1668066148472");
        cookie5.setDomain("www.jufaanli.com");
        cookie5.setPath("/");


        HttpCookie cookie6 = new HttpCookie("Hm_lvt_7d935fee641e9bdd8fd6b28e9a2b62dc", "1667878133,1668066150");
        cookie6.setDomain(".jufaanli.com");
        cookie6.setPath("/");

        HttpCookie cookie7 = new HttpCookie("tf", "3577b10c9d45dfb19c9345b5afadde93");
        cookie7.setDomain("www.jufaanli.com");
        cookie7.setPath("/");

        HttpCookie cookie8 = new HttpCookie("Hm_lpvt_7d935fee641e9bdd8fd6b28e9a2b62dc", "1668066322");
        cookie8.setDomain(".jufaanli.com");
        cookie8.setPath("/");


        HttpResponse response = HttpRequest.post(url)
                .form(prams)
                .timeout(-1)
                // .cookie(cookie, cookie2, cookie3, cookie4, cookie5, cookie6, cookie7, cookie8)
                .cookie("t=4dff576d487540b0ce56c2b03032084a; BJYSESSION=7a60tfqt8d2l2eb14tql7ul5q6; vid=10-2; HWWAFSESID=1f4657389f5720c03a; HWWAFSESTIME=1668066148472; Hm_lvt_7d935fee641e9bdd8fd6b28e9a2b62dc=1667878133,1668066150; tf=3577b10c9d45dfb19c9345b5afadde93; Hm_lpvt_7d935fee641e9bdd8fd6b28e9a2b62dc=1668066322")
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header(Header.HOST, "www.jufaanli.com")
                .header("Origin", "https://www.jufaanli.com")
                .header("Pragma", "no-cache")
                .header("Referer", "https://www.jufaanli.com/new_searchcase?TypeKey=1%3A%E5%AE%B6%E5%BA%AD%E6%9A%B4%E5%8A%9B+6%3Ajudgement_1_%E5%88%A4%E5%86%B3+H%3Aajtz_3950_%E5%AE%B6%E5%BA%AD%E6%9A%B4%E5%8A%9B&search_uuid=d4e82c6cd1da572c533e3a16f03a2f00&Page_pt=3")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"107\", \"Chromium\";v=\"107\", \"Not=A?Brand\";v=\"24\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("X-Requested-With", "XMLHttpRequest")
                .execute();

        System.out.println(response.body());
    }
}
