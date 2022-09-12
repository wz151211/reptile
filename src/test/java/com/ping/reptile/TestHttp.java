package com.ping.reptile;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ping.reptile.model.vo.Dict;
import com.ping.reptile.model.vo.Pair;
import com.ping.reptile.model.vo.Result;
import com.ping.reptile.utils.IpUtils;
import com.ping.reptile.utils.ParamsUtils;
import com.ping.reptile.utils.TripleDES;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @Author: W.Z
 * @Date: 2022/8/19 22:04
 */
public class TestHttp {

    @Test
    public void test1() {
        String url = "https://account.court.gov.cn/api/login";
        Map<String, Object> prams = new HashMap<>();
        prams.put("username", "15639748635");
        prams.put("password", "M6mkAsyKsjFtL5t21Iankhhjc3R76cJI65wGKEl48HiQPzQXDlYx384ls6XZHCC3EZXBj9uItc2Ap8D5pVIUHwoRD%2FZtD%2Be755Jzi8MW8LAuVg3NcKEDM3piNiiQuIUT9cupe%2Bj2Qmn85AQIEsZlFDyviT6unocRX9CcEQuTDV17WiIz0jpRI4jdksBktYnjOXnHV44et7g7Wd7wzNFkXMzJN1oevzUYmN48Aoc5oSiM7Y26BvnM%2BEd1peJdiDsuuJ0NgslmnN8Vxr4ZbP9azU4C%2FxfJQxLz9rISq2gdPNGBAm%2BDTHpeOuz8hWMvmBUG3CHXoejwiODzzzQtbLTXVQ%3D%3D");
        prams.put("appDomain", "wenshu.court.gov.cn");
        HttpResponse response = HttpRequest
                .post(url)
                .form(prams)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Accept", "*/*")
                .header("Accept-Language", "zh-CN,zh-Hans;q=0.9")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Host", "account.court.gov.cn")
                .header("Origin", "https://account.court.gov.cn")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.5 Safari/605.1.15")
                .header("Connection", "keep-alive")
                .header("Referer", "https://account.court.gov.cn/app?back_url=https%3A%2F%2Faccount.court.gov.cn%2Foauth%2Fauthorize%3Fresponse_type%3Dcode%26client_id%3Dzgcpwsw%26redirect_uri%3Dhttps%253A%252F%252Fwenshu.court.gov.cn%252FCallBackController%252FauthorizeCallBack%26state%3D766ecbe8-71f8-43ad-b5f1-f4651510a49f%26timestamp%3D1661009840483%26signature%3D1366DC2A6B94AC73964EFA12F3EFE5E918DA463DFE7873823D2E07072F348D00%26scope%3Duserinfo")
                .header("X-Requested-With", "XMLHttpRequest")
                .timeout(-1)
                .execute();
        String body = response.body();
        System.out.println(body);
        List<HttpCookie> cookies = response.getCookies();
        for (HttpCookie cookie : cookies) {
            System.out.println(cookie.getName() + "==" + cookie.getValue());
        }
        Map<String, List<String>> headers = response.headers();
        headers.forEach((k, v) -> System.out.println(k + ":" + v));

    }

    @Test
    public void test2() {

        String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
        Map<String, Object> prams = new HashMap<>();
        prams.put("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@wsCountSearch");
        prams.put("__RequestVerificationToken", "jiWhfjkoT60AjgZbLR1qcMO7");
        prams.put("wh", 329);
        prams.put("ww", 1675);
        prams.put("cs", 0);
        HttpResponse response = HttpRequest.post(url).form(prams).timeout(-1).execute();

        System.out.println(response.body());
        List<HttpCookie> cookies = response.getCookies();
        for (HttpCookie cookie : cookies) {
            System.out.println(cookie.getName() + "==" + cookie.getValue());
        }
        Map<String, List<String>> headers = response.headers();
        headers.forEach((k, v) -> System.out.println(k + ":" + v));
    }


    @Test
    public void test3() {

        String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
        Map<String, Object> prams = new HashMap<>();
        prams.put("pageId", "65af81bb5e50608bf59282406c5308ad");
        prams.put("s8", "02");
        prams.put("sortFields", "s51:desc");
        prams.put("ciphertext", "110000+111001+1001100+1001001+110110+1100001+1001110+1011000+1101001+1100010+1001000+1000110+1110011+1100011+1000110+1001000+1101110+1100111+1110000+1100011+1000111+1100010+1101011+110111+110010+110000+110010+110010+110000+111000+110010+110001+1110011+1100001+1101010+1100111+1100011+1001111+110110+1100001+1000101+1000001+1100101+1001111+1100101+1010100+1101101+1000100+1000111+1100100+1110000+1100001+1011000+1010001+111101+111101");
        prams.put("pageNum", 1);
        prams.put("pageSize", 100);
        Pair pair = new Pair();
        pair.setKey("s8");
        pair.setValue("02");
        prams.put("queryConditions", JSON.toJSONString(Lists.newArrayList(pair)));
        prams.put("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@queryDoc");
        prams.put("__RequestVerificationToken", "Rdarvyt3JAWTkgkgXIEkOeCM");
        prams.put("wh", 470);
        prams.put("ww", 1680);

        HttpCookie cookie = new HttpCookie("SESSION", "d5d2086f-68ef-4830-a73d-dbc0b4e5719f");
        cookie.setDomain("wenshu.court.gov.cn");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        HttpResponse response = HttpRequest.post(url)
                .form(prams)
                .timeout(-1)
                .cookie(cookie)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("X-Real-IP", IpUtils.getIp())
                .header("X-Forwarded-For", IpUtils.getIp())
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header(Header.HOST, "wenshu.court.gov.cn")
                .header("Origin", "https://wenshu.court.gov.cn")
                .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?pageId=09f0101a7efe4af5d475aa661f85df56&s8=02")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "Windows")
                .header("X-Requested-With", "XMLHttpRequest")
                .execute();
        System.out.println(response.body());

        Result result = JSON.parseObject(response.body(), Result.class);
        if (result.getSuccess()) {
            String iv = DateUtil.format(new Date(), "yyyyMMdd");

            String decrypt = TripleDES.decrypt(result.getSecretKey(), result.getResult(), iv);
            System.out.println(decrypt);

        }
    }


    @Test
    public void test4() {

        String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
        Map<String, Object> prams = new HashMap<>();
        prams.put("docId", "4205e0c2d6544b0e8c3aae4300ff0eff");
        prams.put("ciphertext", "1101101 111001 1100100 1010101 1100001 1001010 1011001 1000011 1010110 111001 1001110 1000001 1110001 1000011 1110110 111001 1110111 1001000 110010 1110100 1100001 1110111 1101000 1100001 110010 110000 110010 110010 110000 111000 110010 110010 1110000 1110000 1100101 1110110 1001111 1000010 1001010 1111010 1110000 1010100 1100001 1110011 110110 1011000 1111010 110010 1010101 1110111 1010010 101111 111000 1110111 111101 111101");
        prams.put("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@docInfoSearch");
        prams.put("__RequestVerificationToken", "Fm9DBO8ET65uQkyBDFyoa8QN");
        prams.put("wh", 254);
        prams.put("ww", 1915);
        prams.put("cs", 0);
        HttpCookie cookie = new HttpCookie("SESSION", "8f807c63-0cd3-4b11-93e1-0d0fb84f0341");
        cookie.setDomain("wenshu.court.gov.cn");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);


        HttpResponse response = HttpRequest.post(url)
                .form(prams)
                .timeout(-1)
                .cookie(cookie)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header(Header.HOST, "wenshu.court.gov.cn")
                .header("Origin", "https://wenshu.court.gov.cn")
                .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181107ANFZ0BXSK4/index.html?docId=4205e0c2d6544b0e8c3aae4300ff0eff")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"104\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "Windows")
                .header("X-Requested-With", "XMLHttpRequest")
                .execute();

        System.out.println(response.body());
        Result result = JSON.parseObject(response.body(), Result.class);
        if (result.getSuccess()) {
            String iv = DateUtil.format(new Date(), "yyyyMMdd");

            String decrypt = TripleDES.decrypt(result.getSecretKey(), result.getResult(), iv);
            System.out.println(decrypt);
        }

        List<HttpCookie> cookies = response.getCookies();
        for (HttpCookie c : cookies) {
            System.out.println(c.getName() + "==" + c.getValue());
        }
        Map<String, List<String>> headers = response.headers();
        headers.forEach((k, v) -> System.out.println(k + ":" + v));
    }


    @Test
    public void test5() throws IOException {
        String key = "SPeTWZeCb2cxTCu20Fvb8oSh";
        String iv = DateUtil.format(new Date(), "yyyyMMdd");
        byte[] bytes = Files.readAllBytes(new File("/Users/monkey/Desktop/爬虫/列表密文.txt").toPath());
        String decrypt = TripleDES.decrypt(key, new String(bytes), iv);
        System.out.println(decrypt);

    }


    @Test
    public void test7() {
        String key = "WLnTC8aFmEfpLQadqdSXe1Ie";
        //  byte[] bytes = SecureUtil.des(key.getBytes(StandardCharsets.UTF_8)).decrypt(data);
        String date = DateUtil.format(new Date(), "yyyyMMdd");
        //String result = SecureUtil.des().setMode(CipherMode.decrypt).setIv(date.getBytes(StandardCharsets.UTF_8)).decryptStr(data);
        //  DES des = new DES(Mode.CBC, Padding.PKCS5Padding,key.getBytes(StandardCharsets.UTF_8),date.getBytes(StandardCharsets.UTF_8));
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "TripleDES");

            IvParameterSpec ivSpec = new IvParameterSpec(date.getBytes(StandardCharsets.UTF_8));
            File file = new File("/Users/monkey/Desktop/爬虫/详情密文.txt");
            byte[] ecryptedMessageBytes = Files.readAllBytes(file.toPath());

            // String secretMessage = IOUtils.resourceToString(file.getPath(), Charset.forName("UTF-8"));
 /*

            Cipher encryptCipher = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

            byte[] secretMessagesBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessagesBytes);
            String encodedMessage = java.util.Base64.getEncoder().encodeToString(encryptedMessageBytes);*/


            Cipher decryptCipher = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
            byte[] decryptedMessageBytes = decryptCipher.doFinal(Base64.getDecoder().decode(ecryptedMessageBytes));
            String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);

            System.out.println(decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test8() throws IOException {

        String url = "https://wenshu.court.gov.cn/tongyiLogin/authorize";
        HttpResponse response = HttpRequest.post(url)
                //.form(prams)
                .timeout(-1)
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
        List<HttpCookie> cookies = response.getCookies();
        for (HttpCookie cookie : cookies) {
            System.out.println(cookie.getName() + "===" + cookie.getValue());
        }
        System.out.println(response.body());
        HttpResponse response1 = HttpRequest.get(response.body()).execute();

        for (HttpCookie cookie : response1.getCookies()) {
            System.out.println(cookie.getName() + "===" + cookie.getValue());
        }
        System.out.println(response1.body());

        test1();


    }

    @Test
    public void test9() {
        LocalDate date = LocalDate.of(2022, 8, 8);

        System.out.println(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(date.minusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE));

        LocalDate parse = LocalDate.parse("2022-08-16", DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.println(parse.format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(ParamsUtils.cipher());

    }

    @Test
    public void test10() {
        List<Dict> courts = getCourt("400");
        for (Dict court : courts) {
            System.out.println(court);
        }

    }


    public List<Dict> getCourt(String code) {
        String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
        String pageId = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> params = new HashMap<>();
        params.put("pageId", pageId);
        params.put("s8", "02");
        params.put("parentCode", code);
        params.put("cfg", "com.lawyee.judge.dc.parse.dto.LoadDicDsoDTO@loadFyByCode");
        params.put("__RequestVerificationToken", ParamsUtils.random(24));
        params.put("wh", 470);
        params.put("ww", 1680);
        HttpResponse response = null;
        try {
            TimeUnit.SECONDS.sleep(1);
            response = HttpRequest.post(url)
                    .form(params)
                    .timeout(-1)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("X-Real-IP", IpUtils.getIp())
                    .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Host", "wenshu.court.gov.cn")
                    .header("Origin", "https://wenshu.court.gov.cn")
                    .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?pageId=" + pageId + "&s8=02")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    //  .header("User-Agent", properties.getUserAgent())
                    //  .header("sec-ch-ua", properties.getSecChUa())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();
            System.out.println(response.body());
            Result result = JSON.parseObject(response.body(), Result.class);
            JSONObject object = JSON.parseObject(result.getResult());
            List<Dict> courts = JSON.parseArray(object.getJSONArray("fy").toJSONString(), Dict.class);
            List<Dict> countList = new CopyOnWriteArrayList<>();
            if (courts.size() == 0) {
                return countList;
            }
            countList.addAll(courts);
            for (Dict court : courts) {
                countList.addAll(getCourt(court.getCode()));
            }
            return countList;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

}
