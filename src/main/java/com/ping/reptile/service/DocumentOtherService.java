package com.ping.reptile.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.mapper.ConfigMapper;
import com.ping.reptile.model.entity.ConfigEntity;
import com.ping.reptile.model.vo.Result;
import com.ping.reptile.utils.ParamsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DocumentOtherService {
    private String requestVerificationToken = ParamsUtils.random(24);
    @Autowired
    private ConfigMapper configMapper;
    private ScheduledExecutorService service = Executors.newScheduledThreadPool(5);


    @Autowired
    private CustomProperties properties;

    private ConfigEntity config = null;

    @PostConstruct
    public void task() {
        service.scheduleAtFixedRate(() -> map(), 10, 10, TimeUnit.SECONDS);
    }


    public void map() {
        if (config == null) {
            config = configMapper.selectById(properties.getId());
        }
        String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
        Map<String, Object> params = new HashMap<>();
        params.put("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@wsCountSearch");
        params.put("__RequestVerificationToken", requestVerificationToken);
        params.put("wh", 699);
        params.put("ww", 1280);
        params.put("cs", 0);
        HttpResponse response = null;
        try {
            response = HttpRequest.post(url)
                    .form(params)
                    .timeout(-1)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    //  .header("X-Real-IP", IpUtils.getIp())
                    //   .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Length", params.toString().length() + "")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Host", "wenshu.court.gov.cn")
                    .header("Origin", "https://wenshu.court.gov.cn")
                    .header("Pragma", "no-cache")
                    .header("Referer", "https://wenshu.court.gov.cn/")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", config.getAgent())
                    .header("sec-ch-ua", config.getChua())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();
            Result result = JSON.parseObject(response.body(), Result.class);
        } catch (Exception e) {
            log.error("发送列表请求出错", e);
        }
    }
}
