package com.ping.reptile;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ping.reptile.model.entity.AreaEntity;
import com.ping.reptile.model.vo.Dict;
import com.ping.reptile.model.vo.Pair;
import com.ping.reptile.model.vo.Result;
import com.ping.reptile.utils.HtmlUnitUtils;
import com.ping.reptile.utils.IpUtils;
import com.ping.reptile.utils.ParamsUtils;
import com.ping.reptile.utils.TripleDES;
import org.apache.commons.lang3.StringUtils;
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
        //   prams.put("queryConditions", JSON.toJSONString(Lists.newArrayList(pair)));
        prams.put("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@queryDoc");
        prams.put("__RequestVerificationToken", "zodeoJpG1CofKsjjZd3W8Bxk");
        prams.put("wh", 224);
        prams.put("ww", 1275);

        HttpCookie cookie = new HttpCookie("SESSION", "60559138-392f-4d1a-9206-3794361d3ef8");
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

        String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w?HifJzoc9=4Ix3PwpGYhVuV5FiLw6hPR.1bzJlaaDiqoTA6NRsNE60VgzkcONRLTP9nAV9oFROnKWpIa6GG7Glfy_Hw.84vjEPOSwT6FOJ.cpMRXIpwedzQipiMiMXRvmeSaGvY_gfnU.kMmY5A7w5it0ptTjdbnCA8cKH.5zFx2_khF04TvDBcMTGn_IvAHiJ.ApKdlsRWo8DZ7Wbmln.7uOgRywoB6.PMgBLgJlvYeSMP1sI18y3p86mCGvJ8MZM.TH8zwVkmViU4YZmbd8COhWfQofyaGjW4oZgCVtYazNNnz9JxAUVlcfF2mCj0Ylao9gd4mYiaEeUT2H1EH8BS5xQINKJ4grOdCtHPhoIXgAlwaoe9JP4_zgRcBGu._WzGF7J31t8KYV0pIISmgfZJDZ2RS54huA6_";
        Map<String, Object> prams = new HashMap<>();
        prams.put("docId", "8+rxcRm4OtNRX7O4OVXsi5pLfkijeC0TGaMIMUW5IL22ZGu+aSPp+WI3IS1ZgB82CzBLvpPJT6eO1GslpSPjWc1Mw4EJSTDleJOqeLvkNBFUf3WdvQgzSTtRc8pzVWb7");
        prams.put("ciphertext", "1100011 1110000 1010001 111001 1110101 110010 1010001 1001110 1010111 110101 1100010 1001000 1001101 1101001 1001110 110001 1100011 1101000 1001001 1100001 1100101 1000011 1000111 1100111 110010 110000 110010 110011 110000 110110 110001 110110 101111 1001010 1001010 1010011 1101100 1111000 1010011 1110010 110110 101011 1101111 1000010 1010010 110000 110111 1110100 1110110 1010101 1011001 1101110 1001010 1110111 111101 111101");
        prams.put("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@docInfoSearch");
        prams.put("__RequestVerificationToken", "Gzcc3XRw224aRLxHthQq6RAz");
        prams.put("wh", 224);
        prams.put("ww", 1275);
        prams.put("cs", 0);
        HttpCookie cookie = new HttpCookie("SESSION", "60559138-392f-4d1a-9206-3794361d3ef8");
        cookie.setDomain("wenshu.court.gov.cn");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        List<HttpCookie> cookieList = new ArrayList<>();
        String cookieStr = "__utma=61363882.561354653.1675935803.1675935803.1675935803.1; __utmz=61363882.1675935803.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); UM_distinctid=1865ea4d79d2ac-050acc07f121f1-26031951-fa000-1865ea4d79e17; HM4hUBT0dDOn443S=2o.AJBlb6ZTssOjXgF1OimEDZgoIqkW9okI9QxSdvMLlrc700GEKnvKchoUu8hCu; wzws_sessionid=oGSMDwiAMTI0LjY0LjIyLjE2MYI2ZjY5MDGBMTRkNThh; SESSION=60559138-392f-4d1a-9206-3794361d3ef8; HM4hUBT0dDOn443T=4yA544EDWzJYJ3kLS4Xz4dn0dhVPNKILRf7CXHqTaXX1J802MmadSs4c.CJc8Zqm.aY6DKXDytyPgIONlJow5Wpl_I0w5JaGGn9RZp7GY67nJYlyN6PWNMxtozm5WqO3n5Oh5PmBNOJwFgpQGxreEWlIrkJwOKy3w5S.2yC06_7VPRvrJuCMf7zUltW51xIUJ_.tLNvdsHJv3LWvjkgrNsYE5FDSHaAyr5ReIWb25ELaXFBtU24uUQPfXsyrgwNdTr7lQjM29ygnEosUZzDBktjl9s8x5od8_RkOrpHy5v5fba_QEer4VIrLkXYxXNcutDF78zW_kx.JFiihiCe4a2JrejRU84ww1KVL.8uSttCBDhTJmu0kT4matDP.PugY0SlQ";
        for (String s : cookieStr.split(";")) {
            String[] split = s.split("=");
            HttpCookie cookie1 = new HttpCookie(split[0], split[1]);
            cookie1.setPath("/");
            cookie1.setHttpOnly(true);
            cookie1.setDomain("wenshu.court.gov.cn");
            cookieList.add(cookie1);

        }


        HttpResponse response = HttpRequest.post(url)
                .body(JSON.toJSONString(prams))
                .timeout(-1)
                .cookie(cookieList)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header(Header.HOST, "wenshu.court.gov.cn")
                .header("Origin", "https://wenshu.court.gov.cn")
                .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181107ANFZ0BXSK4/index.html?docId=zD9uQTa5PrCpCnXo5nqoExGaBqSP71UgBWbI566dfHhAy+8GCH6k8WI3IS1ZgB82CzBLvpPJT6eO1GslpSPjWc1Mw4EJSTDleJOqeLvkNBFUf3WdvQgzSYh4iIvJKoAg")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
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

        String iv = DateUtil.format(new Date(), "yyyyMMdd");
        byte[] bytes = Files.readAllBytes(new File("E:\\json.txt").toPath());
        JSONObject object = JSON.parseObject(new String(bytes));
        String key = object.getString("secretKey");
        String content = object.getString("result");
        String decrypt = TripleDES.decrypt(key, content, iv);
        System.out.println(decrypt);

    }

    @Test
    public void testId() throws IOException {

        String iv = DateUtil.format(new Date(), "yyyyMMdd");

        String key = "29I8s3yXEOVV2dRwx4N1h";
        String content = "MpkxweNzSWtjFEvxw7IO3EEiXaiWqkOcR2gOlD52REC29I8s3yXEOVV2dRwx4N1h";
        String decrypt = TripleDES.decrypt(key, content, iv);
        System.out.println(decrypt);
        //document.getElementsByClassName('LT_Filter_right')[0].insertAdjacentHTML('beforeend','<p data-key="s31" data-value="2022-06-01 TO 2023-06-01">发布日期：2022-06-01 TO 2023-06-01 <i class="fa fa-close"></i></p>')
    }


    @Test
    public void test7() {
        String key = "f18QPOPnVxMAdo5QlVC2SU0g";
        //  byte[] bytes = SecureUtil.des(key.getBytes(StandardCharsets.UTF_8)).decrypt(data);
        String date = DateUtil.format(new Date(), "yyyyMMdd");
        //String result = SecureUtil.des().setMode(CipherMode.decrypt).setIv(date.getBytes(StandardCharsets.UTF_8)).decryptStr(data);
        //  DES des = new DES(Mode.CBC, Padding.PKCS5Padding,key.getBytes(StandardCharsets.UTF_8),date.getBytes(StandardCharsets.UTF_8));
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "TripleDES");

            IvParameterSpec ivSpec = new IvParameterSpec(date.getBytes(StandardCharsets.UTF_8));
            File file = new File("C:\\Users\\hua-cloud\\Desktop\\密文.txt");
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
    public void test10() throws IOException {
        String html = "<div id=\"idx_map_content\" class=\"index_map_content\"> <div class=\"map_p map_p_beijing\" _name=\"北京\" _code=\"beijing\" data-val=\"1\">北京</div> <div class=\"map_p map_p_tianjin\" _name=\"天津\" _code=\"tianjin\" data-val=\"51\">天津</div> <div class=\"map_p map_p_shanghai\" _name=\"上海\" _code=\"shanghai\" data-val=\"1100\">上海</div> <div class=\"map_p map_p_chongqing\" _name=\"重庆\" _code=\"chongqing\" data-val=\"2950\">重庆</div> <div class=\"map_p map_p_hebei\" _name=\"河北\" _code=\"hebei\" data-val=\"100\">河北</div> <div class=\"map_p map_p_shanxi\" _name=\"山西\" _code=\"shanxi\" data-val=\"300\">山西</div> <div class=\"map_p map_p_liaoning\" _name=\"辽宁\" _code=\"liaoning\" data-val=\"600\">辽宁</div> <div class=\"map_p map_p_jilin\" _name=\"吉林\" _code=\"jilin\" data-val=\"750\">吉林</div> <div class=\"map_p map_p_heilongjiang\" _name=\"黑龙江\" _code=\"heilongjiang\" data-val=\"850\">黑龙江</div> <div class=\"map_p map_p_jiangsu\" _name=\"江苏\" _code=\"jiangsu\" data-val=\"1150\">江苏</div> <div class=\"map_p map_p_zhejiang\" _name=\"浙江\" _code=\"zhejiang\" data-val=\"1300\">浙江</div> <div class=\"map_p map_p_anhui\" _name=\"安徽\" _code=\"anhui\" data-val=\"1451\">安徽</div> <div class=\"map_p map_p_fujian\" _name=\"福建\" _code=\"fujian\" data-val=\"1600\">福建</div> <div class=\"map_p map_p_jiangxi\" _name=\"江西\" _code=\"jiangxi\" data-val=\"1700\">江西</div> <div class=\"map_p map_p_shandong\" _name=\"山东\" _code=\"shandong\" data-val=\"1850\">山东</div> <div class=\"map_p map_p_henan\" _name=\"河南\" _code=\"henan\" data-val=\"2050\">河南</div> <div class=\"map_p map_p_hubei\" _name=\"湖北\" _code=\"hubei\" data-val=\"2250\">湖北</div> <div class=\"map_p map_p_hunan\" _name=\"湖南\" _code=\"hunan\" data-val=\"2400\">湖南</div> <div class=\"map_p map_p_guangdong\" _name=\"广东\" _code=\"guangdong\" data-val=\"2550\">广东</div> <div class=\"map_p map_p_hainan\" _name=\"海南\" _code=\"hainan\" data-val=\"2900\">海南</div> <div class=\"map_p map_p_sichuan\" _name=\"四川\" _code=\"sichuan\" data-val=\"3000\">四川</div> <div class=\"map_p map_p_guizhou\" _name=\"贵州\" _code=\"guizhou\" data-val=\"3250\">贵州</div> <div class=\"map_p map_p_yunnan\" _name=\"云南\" _code=\"yunnan\" data-val=\"3350\">云南</div> <div class=\"map_p map_p_shanxi2\" _name=\"陕西\" _code=\"shanxi2\" data-val=\"3600\">陕西</div> <div class=\"map_p map_p_gansu\" _name=\"甘肃\" _code=\"gansu\" data-val=\"3750\">甘肃</div> <div class=\"map_p map_p_qinghai\" _name=\"青海\" _code=\"qinghai\" data-val=\"3900\">青海</div> <div class=\"map_p map_p_taiwan\" _name=\"台湾\" _code=\"taiwan\" data-val=\"\">台湾</div> <div class=\"map_p map_p_neimenggu\" _name=\"内蒙古\" _code=\"neimenggu\" data-val=\"451\">内蒙古</div> <div class=\"map_p map_p_guangxi\" _name=\"广西\" _code=\"guangxi\" data-val=\"2750\">广西</div> <div class=\"map_p map_p_xizang\" _name=\"西藏\" _code=\"xizang\" data-val=\"3500\">西藏</div> <div class=\"map_p map_p_ningxia\" _name=\"宁夏\" _code=\"ningxia\" data-val=\"4000\">宁夏</div> <div class=\"map_p map_p_xinjiang\" _name=\"新疆\" _code=\"xinjiang\" data-val=\"4050\">新疆</div> <div class=\"map_p map_p_aomen\" _name=\"澳门\" _code=\"aomen\" data-val=\"\">澳门</div> <div class=\"map_p map_p_xianggang\" _name=\"香港\" _code=\"xianggang\" data-val=\"\">香港</div> </div>";
        Document document = Jsoup.parse(html);
        Element mapContent = document.getElementById("idx_map_content");
        List<Dict> countList = new CopyOnWriteArrayList<>();
        Integer level = 0;
        for (Element child : mapContent.children()) {
            String name = child.attr("_name");
            String code = child.attr("data-val");
            System.out.println("name=" + name + "--code=" + code);
            List<Dict> courts = getCourt(Integer.parseInt(code), true, countList, level);
            for (Dict court : courts) {
                System.out.println(court);
            }
        }


    }


    public List<Dict> getCourt(Integer code, boolean searchParent, List<Dict> countList, Integer level) {
        level++;
        String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
        String pageId = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> params = new HashMap<>();
        params.put("pageId", pageId);
        params.put("s8", "02");
        params.put("provinceCode", code);
        params.put("searchParent", searchParent);
        params.put("cfg", "com.lawyee.judge.dc.parse.dto.LoadDicDsoDTO@loadFy");
        params.put("__RequestVerificationToken", ParamsUtils.random(24));
        params.put("wh", 470);
        params.put("ww", 1680);
        HttpResponse response = null;
        try {
            TimeUnit.SECONDS.sleep(10);
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
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                    //  .header("sec-ch-ua", properties.getSecChUa())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();
            System.out.println(response.body());
            Result result = JSON.parseObject(response.body(), Result.class);
            JSONObject object = JSON.parseObject(result.getResult());
            List<Dict> courts = JSON.parseArray(object.getJSONArray("fy").toJSONString(), Dict.class);
            if (courts.size() == 0) {
                return countList;
            }
            countList.addAll(courts);
            for (Dict court : courts) {
                getCourt(court.getId(), false, countList, level);
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


    @Test
    public void testCode() throws IOException, InterruptedException {
        String index = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2022/";
        Document document = Jsoup.connect(index + "index.html").get();
        TimeUnit.SECONDS.sleep(3);
        for (Element element : document.select(".provincetr a")) {
            String href = element.attr("href");
            String provinceName = element.text();
            String provinceCode = href.substring(0, 2) + "0000000000";
            AreaEntity provice = new AreaEntity();
            provice.setId(provinceCode);
            provice.setName(provinceName);
            provice.setPid("-1");
            provice.setLevel(1);
            provice.setPath("/" + provinceCode);
            System.out.println(provice);
            Document cityDocument = Jsoup.connect(index + "/" + href).get();
            TimeUnit.SECONDS.sleep(3);
            for (Element city : cityDocument.select(".citytr")) {
                Elements cityList = city.select("a");
                Element cityCode = cityList.get(0);
                Element cityName = cityList.get(1);
                AreaEntity cityOrg = new AreaEntity();
                cityOrg.setId(cityCode.text());
                cityOrg.setName(cityName.text());
                cityOrg.setPid(provinceCode);
                cityOrg.setLevel(2);
                cityOrg.setPath("/" + provinceCode + "/" + cityCode.text());
                System.out.println(cityOrg);
                String href1 = cityCode.attr("href");
                Document countyDocument = Jsoup.connect(index + "/" + href1).get();
                TimeUnit.SECONDS.sleep(3);
                for (Element county : countyDocument.select(".countytr")) {
                    Elements select = county.select("a");
                    if (select == null || select.size() == 0) {
                        continue;
                    }
                    Element countyCode = select.get(0);
                    Element countyName = select.get(1);

                    AreaEntity countyOrg = new AreaEntity();
                    countyOrg.setId(countyCode.text());
                    countyOrg.setName(countyName.text());
                    countyOrg.setPid(countyCode.text());
                    countyOrg.setLevel(3);
                    countyOrg.setPath("/" + provinceCode + "/" + cityOrg.getId() + "/" + countyCode.text());
                    System.out.println(countyOrg);
                }

            }

        }

    }

    @Test
    public void testHunit() {
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            //  TimeUnit.SECONDS.sleep(3);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.waitForBackgroundJavaScript(600 * 1000);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.addRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            webClient.addRequestHeader("Accept-Encoding", "gzip, deflate, br");
            webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.9");
            webClient.addRequestHeader("Connection", "keep-alive");
            webClient.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            webClient.addRequestHeader("Host", "wenshu.court.gov.cn");
            webClient.addRequestHeader("Origin", "https://wenshu.court.gov.cn");
            webClient.addRequestHeader("Referer", "ttps://wenshu.court.gov.cn/website/wenshu/181107ANFZ0BXSK4/index.html?docId=SrX4v8pf2ZV3dOYyGlv8PK1Dpo9u/0cTzprmqZbo3RjQ5fer9r0s1mI3IS1ZgB82CzBLvpPJT6dQT5gOapcsChpnkKSuaHezmR0PFBl6r19N8slYVDo/4IKxAH8d9mOT");
            webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
            webClient.addRequestHeader("Sec-Ch-Ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"");
            webClient.addRequestHeader("Sec-Ch-Ua-Platform", "\"Windows\"");
            webClient.addRequestHeader("Sec-Ch-Ua-Mobile", "?0");
            webClient.addRequestHeader("Sec-Fetch-Dest", "empty");
            webClient.addRequestHeader("Sec-Fetch-Mode", "cors");
            webClient.addRequestHeader("Sec-Fetch-Site", "same-origin");
            webClient.addRequestHeader("Sec-Fetch-User", "?1");
            List<HttpCookie> cookieList = new ArrayList<>();
            String cookieStr = "__utma=61363882.561354653.1675935803.1675935803.1675935803.1; __utmz=61363882.1675935803.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); UM_distinctid=1865ea4d79d2ac-050acc07f121f1-26031951-fa000-1865ea4d79e17; HM4hUBT0dDOn443S=2o.AJBlb6ZTssOjXgF1OimEDZgoIqkW9okI9QxSdvMLlrc700GEKnvKchoUu8hCu; wzws_sessionid=oGSMDwiAMTI0LjY0LjIyLjE2MYI2ZjY5MDGBMTRkNThh; SESSION=60559138-392f-4d1a-9206-3794361d3ef8; HM4hUBT0dDOn443T=4yA544EDWzJYJ3kLS4Xz4dn0dhVPNKILRf7CXHqTaXX1J802MmadSs4c.CJc8Zqm.aY6DKXDytyPgIONlJow5Wpl_I0w5JaGGn9RZp7GY67nJYlyN6PWNMxtozm5WqO3n5Oh5PmBNOJwFgpQGxreEWlIrkJwOKy3w5S.2yC06_7VPRvrJuCMf7zUltW51xIUJ_.tLNvdsHJv3LWvjkgrNsYE5FDSHaAyr5ReIWb25ELaXFBtU24uUQPfXsyrgwNdTr7lQjM29ygnEosUZzDBktjl9s8x5od8_RkOrpHy5v5fba_QEer4VIrLkXYxXNcutDF78zW_kx.JFiihiCe4a2JrejRU84ww1KVL.8uSttCBDhTJmu0kT4matDP.PugY0SlQ";

            String[] cookies = cookieStr.split(";");
            for (String s : cookies) {
                String[] s1 = s.split("=");
                HttpCookie cookie = new HttpCookie(s1[0], s1[1]);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setDomain("wenshu.court.gov.cn");
                cookieList.add(cookie);
            }
//            HttpCookie cookie1 = new HttpCookie("SESSION", "60559138-392f-4d1a-9206-3794361d3ef8");
//            cookie1.setHttpOnly(true);
//            cookie1.setPath("/");
//            cookie1.setDomain("wenshu.court.gov.cn");
            //           cookieList.add(cookie1);
            CookieManager cookieManager = new CookieManager();
            for (HttpCookie cookie : cookieList) {
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
            String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w?HifJzoc9=4Ix3PwpGYhVuV5FiLw6hPR.1bzJlaaDiqoTA6NRsNE60VgzkcONRLTP9nAV9oFROnKWpIa6GG7Glfy_Hw.84vjEPOSwT6FOJ.cpMRXIpwedzQipiMiMXRvmeSaGvY_gfnU.kMmY5A7w5it0ptTjdbnCA8cKH.5zFx2_khF04TvDBcMTGn_IvAHiJ.ApKdlsRWo8DZ7Wbmln.7uOgRywoB6.PMgBLgJlvYeSMP1sI18y3p86mCGvJ8MZM.TH8zwVkmViU4YZmbd8COhWfQofyaGjW4oZgCVtYazNNnz9JxAUVlcfF2mCj0Ylao9gd4mYiaEeUT2H1EH8BS5xQINKJ4grOdCtHPhoIXgAlwaoe9JP4_zgRcBGu._WzGF7J31t8KYV0pIISmgfZJDZ2RS54huA6_";
            HtmlPage page = webClient.getPage(url);
            Document parse = Jsoup.parse(page.asXml());
            String text = parse.text();
            System.out.println(text);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
