package com.ping.reptile.utils;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ping.reptile.model.vo.Result;

import java.net.HttpCookie;
import java.util.Map;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 10:54
 */
public class HttpUtils {


    public Result post(String url, Map<String, Object> params, String cookieValue) {

        // String params = "pageId=b82c990835c64d7432b8ec2a4748f777&s8=03&s6=01&sortFields=s50%3Adesc&ciphertext=1010000+1011001+110010+1100010+111000+1110101+1110011+1000010+1011001+1100100+1010111+1111010+110011+1111000+1001010+1110110+1000110+1100001+1011001+1010111+1000111+111000+1001110+1001111+110010+110000+110010+110010+110000+111000+110010+110000+1001101+1001011+1110111+1110011+1100101+1010000+1001100+1001010+111001+110101+1000100+1110110+1110110+1001111+1001001+1000001+1110001+1110111+1101000+110111+1100111+1000001+111101+111101&pageNum=1&queryCondition=%5B%7B%22key%22%3A%22s8%22%2C%22value%22%3A%2203%22%7D%2C%7B%22key%22%3A%22s6%22%2C%22value%22%3A%2201%22%7D%5D&cfg=com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO%40queryDoc&__RequestVerificationToken=74SstifxsK1uQiVOW0AtGLZ6&wh=620&ww=1680&cs=0";
        HttpCookie cookie = new HttpCookie("SESSION", cookieValue);
        cookie.setDomain("wenshu.court.gov.cn");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        HttpResponse response = HttpRequest.post(url + "?" + params)
                //.form(prams)
                .timeout(-1)
                .cookie(cookie)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header(Header.HOST, "wenshu.court.gov.cn")
                .header("Origin", "https://wenshu.court.gov.cn")
                .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?pageId=b82c990835c64d7432b8ec2a4748f777&s8=03&s6=01")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"104\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "macOS")
                .header("X-Requested-With", "XMLHttpRequest")
                .execute();
        return null;

    }


}
