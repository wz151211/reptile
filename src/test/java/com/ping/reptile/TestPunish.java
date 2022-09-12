package com.ping.reptile;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ping.reptile.model.vo.Param;
import com.ping.reptile.model.vo.Result;
import com.ping.reptile.utils.IpUtils;
import com.ping.reptile.utils.ParamsUtils;
import com.ping.reptile.utils.TripleDES;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: W.Z
 * @date: 2022/8/22 11:49
 * @desc:
 */
public class TestPunish {


    @Test
    public void testList() {
        String url = "https://cfws.samr.gov.cn/queryDoc";
        Map<String, Object> params = new HashMap<>();
        params.put("pageSize", 150);
        params.put("pageNum", 1);
        params.put("sortFields", "23_s:asc,16_s:asc");
        params.put("ciphertext", ParamsUtils.cipher());

        Param p1 = new Param();
        p1.setId("2019-01-01,2022-12-31");
        p1.setKey("23_s");
        p1.setName("处罚日期");

        Param area = new Param();
        area.setId("310000");
        area.setKey("17_s");
        area.setName("行政区划");

        Param type = new Param();
        type.setId("07");
        type.setKey("8_ss");
        type.setName("处罚种类");

        Param theme = new Param();
        theme.setId("04");
        theme.setKey("49_ss");
        theme.setName("主题");

        Param caseNo = new Param();
        caseNo.setId("沪市监静处〔2020〕06");
        caseNo.setKey("2_s");
        caseNo.setName("处罚文号："+caseNo.getId());

        Param full = new Param();
        full.setId("沪市监静处〔2020〕");
        full.setKey("51_s");
        full.setName("全文检索："+ full.getId());

        Param y = new Param();
        y.setId("2021");
        y.setKey("24_i");

        Param unit = new Param();
        unit.setId("上海市金山区市场监督管理局");
        unit.setKey("14_s,52_s");
        unit.setName("处罚机关："+unit.getId());

        params.put("queryCondition", JSON.toJSONString(Lists.newArrayList(p1)));
       // params.put("queryCondition", "[{\"id\":\"2019-12-31,2019-12-31\",\"key\":\"23_s\",\"name\":\"处罚日期\"},{\"id\":\"110109\",\"key\":\"17_s\",\"name\":\"行政区划\"},{\"id\":\"02\",\"key\":\"8_ss\",\"name\":\"处罚种类\"},{\"id\":\"05\",\"key\":\"49_ss\",\"name\":\"主题\"}]\n");
        HttpResponse response = HttpRequest.post(url)
                .form(params)
                .timeout(-1)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Host", "cfws.samr.gov.cn")
                .header("Origin", "https://cfws.samr.gov.cn")
                .header("Referer", "https://cfws.samr.gov.cn/list.html?49_ss=01")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"104\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("X-Requested-With", "XMLHttpRequest")
                .execute();
        System.out.println(response.body());
    }


    @Test
    public void testDetail() throws IOException {
        String url = "https://cfws.samr.gov.cn/getDoc";
        String docId = "bj_fzc_hd_fzk_zgc__ranhao__147534778361204";

        Map<String, Object> params = new HashMap<>();
        params.put("ciphertext", ParamsUtils.cipher());
        params.put("docid",docId);
        HttpResponse response = HttpRequest.post(url)
                .form(params)
                .timeout(-1)
                .header("Accept", "*/*")
                .header("X-Real-IP", IpUtils.getIp())
                .header("X-Forwarded-For", IpUtils.getIp())
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("HOST", "cfws.samr.gov.cn")
                .header("Origin", "https://cfws.samr.gov.cn")
                .header("Referer", "https://cfws.samr.gov.cn/detail.html?docid=" + docId)
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"104\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("X-Requested-With", "XMLHttpRequest")
                .execute();
        Result result = JSON.parseObject(response.body(), Result.class);
        System.out.println(result);
        JSONObject object = JSON.parseObject(result.getResult());
        byte[] i7s = Base64.getDecoder().decode(object.getString("i7").getBytes(StandardCharsets.UTF_8));
        System.out.println(new String(i7s));

        PDDocument document = PDDocument.load(i7s);
        PDFTextStripper textStripper = new PDFTextStripper();
        String text = textStripper.getText(document);
        System.out.println(text);
        document.close();

    }

    @Test
    public void test3() throws IOException {
        String key = "PgolaVRleHQtNS41LjYKc3RhcnR4cmVmCjQxMTgKJSVFT0YK";
        byte[] bytes = Files.readAllBytes(new File("C:\\Users\\WZ\\Desktop\\华云中盛\\爬虫\\text.txt").toPath());
        String text = new String(bytes);
        String iv = DateUtil.format(new Date(), "yyyyMMdd");
        String decrypt = TripleDES.decrypt(key, text, iv);
        System.out.println(decrypt);
    }

    @Test
    public void test4() throws IOException {
        for (int i = 0; i < 20; i++) {
            System.out.println(IpUtils.getIp());
        }


    }
}
