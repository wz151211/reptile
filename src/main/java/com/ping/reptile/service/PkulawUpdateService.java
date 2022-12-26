package com.ping.reptile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.mapper.ConfigMapper;
import com.ping.reptile.mapper.PkulawPunishInsertMapper;
import com.ping.reptile.mapper.PkulawPunishMapper;
import com.ping.reptile.model.entity.ConfigEntity;
import com.ping.reptile.model.entity.PkulawPunishEntity;
import com.ping.reptile.model.entity.PkulawPunishInsertEntity;
import com.ping.reptile.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpCookie;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class PkulawUpdateService {
    @Autowired
    private CustomProperties properties;
    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private PkulawPunishMapper punishMapper;

    private ConfigEntity config = null;
    private LocalDate date = null;
    private AtomicInteger days = new AtomicInteger(0);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            30,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public void list() {

        if (config == null) {
            config = configMapper.selectById(properties.getId());
        }
        try {
            //  List<PkulawPunishEntity> entities = punishMapper.selectList(Wrappers.<PkulawPunishEntity>lambdaQuery().select(PkulawPunishEntity::getId).like(PkulawPunishEntity::getContent, "继续阅读"));
            List<PkulawPunishEntity> entities = punishMapper.selectList(Wrappers.<PkulawPunishEntity>lambdaQuery().select(PkulawPunishEntity::getId).isNull(PkulawPunishEntity::getContent));
            for (int i = 0; i < entities.size(); i++) {
                log.info("总数={},处理到index={}", entities.size(), i);
                PkulawPunishEntity entity = entities.get(i);
                String gid = entity.getId();
                if (executor.getQueue().size() > 1000) {
                    try {
                        TimeUnit.MINUTES.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                executor.execute(() -> {
                    details(gid);
                });
                //  details(gid);
            }
        } catch (Exception e) {
            log.error("列表获取出错", e);
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }

    public void details(String gid) {
        log.info("线程池中任务数量={}", executor.getQueue().size());
        String url = "https://www.pkulaw.com/apy/" + gid + ".html";
        List<HttpCookie> cookies = new ArrayList<>();
        HttpCookie cookie4 = new HttpCookie("pkulaw_v6_sessionid","");
        cookie4.setDomain("www.pkulaw.com");
        cookie4.setPath("/");
        cookie4.setHttpOnly(true);
        cookie4.setSecure(false);
        cookies.add(cookie4);
        try (final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED)) {
            TimeUnit.SECONDS.sleep(3);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.waitForBackgroundJavaScript(600 * 1000);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.addRequestHeader("X-Real-IP", IpUtils.getIp());
            webClient.addRequestHeader("X-Forwarded-For", IpUtils.getIp());
            webClient.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            webClient.addRequestHeader("Accept-Encoding", "gzip, deflate, br");
            webClient.addRequestHeader("Accept-Language", "zhe-CN,zh;q=0.9");
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
            HtmlPage page = webClient.getPage(url);
            Document parse = Jsoup.parse(page.asXml());
            Element element = parse.getElementById("gridleft");
            String title = element.getElementsByClass("title").get(0).text();
            Element fields = element.getElementsByClass("fields").get(0);
            Elements spans = fields.getElementsByTag("li");
            PkulawPunishEntity entity = new PkulawPunishEntity();
            entity.setId(gid);
            entity.setTitle(title);
            if (spans.size() <= 6) {
                for (int i = 0; i < spans.size(); i++) {
                    String value = "";
                    for (Element text : spans.get(i).getElementsByAttribute("title")) {
                        value += text.attr("title");
                    }
                    if (i == 0) {
                        entity.setTheme(value);
                    }
                    if (i == 1) {
                        entity.setCategory(value);
                    }
                    if (i == 2) {
                        entity.setName(value);
                    }
                    if (i == 3) {
                        entity.setPunishUnit(value);
                    }
                    if (i == 4) {
                        try {
                            entity.setPunishDate(DateUtil.parse(value, "yyyy.MM.dd").toJdkDate());
                        } catch (Exception e) {
                            log.info("发布日期格式化出错，date={},id={}", value, gid);
                            e.printStackTrace();
                        }
                    }
                    if (i == 5) {
                        entity.setCaseNo(value);
                    }
                }
            } else {
                for (int i = 0; i < spans.size(); i++) {
                    String value = "";
                    for (Element text : spans.get(i).getElementsByAttribute("title")) {
                        value += text.attr("title");
                    }
                    if (i == 0) {
                        entity.setTheme(value);
                    }
                    if (i == 1) {
                        entity.setCategory(value);
                    }
                    if (i == 2) {
                        entity.setName(value);
                    }
                    if (i == 3) {
                        entity.setLevel(value);
                    }
                    if (i == 4) {
                        entity.setPunishUnit(value);
                    }
                    if (i == 5) {
                        entity.setArea(value);
                    }
                    if (i == 6) {
                        try {
                            entity.setPunishDate(DateUtil.parse(value, "yyyy.MM.dd").toJdkDate());
                        } catch (Exception e) {
                            log.info("发布日期格式化出错，date={},id={}", value, gid);
                            e.printStackTrace();
                        }
                    }
                    if (i == 7) {
                        entity.setCaseNo(value);
                    }
                    if (i == 8) {
                        for (Element sibling : spans.get(i - 1).nextElementSiblings()) {
                            value += (sibling.getElementsByTag("a").text() + "|");
                        }
                        entity.setBasis(value);

                    }
                }
            }

            Element contentElement = element.getElementById("divFullText");
            entity.setHtml(element.html());
            entity.setContent(contentElement.text());
            log.info("当事人名称={}", entity.getName());
            punishMapper.updateById(entity);
        } catch (Exception e) {
            log.error("gid={}", gid);
            log.info("HtmlUnit获取页面出错", e);
        }
    }


    @Autowired
    private PkulawPunishInsertMapper pkulawPunishInsertMapper;
    private AtomicInteger count = new AtomicInteger(0);

    public void insert(InputStream inputStream) {
        EasyExcel.read(inputStream, PkulawPunishInsertEntity.class, new ReadListener<PkulawPunishInsertEntity>() {
            @Override
            public void invoke(PkulawPunishInsertEntity pkulawPunishEntity, AnalysisContext analysisContext) {
                if (count.get() > 30000) {
                    return;
                }
                log.info("读取到数据={}", pkulawPunishEntity.getTitle());
                if (StringUtils.isNoneEmpty(pkulawPunishEntity.getPunishDate())) {
                    if (pkulawPunishEntity.getPunishDate().contains(".")) {
                        pkulawPunishEntity.setPunishDate(pkulawPunishEntity.getPunishDate().replace(".", "-"));
                    } else {
                        pkulawPunishEntity.setPunishDate(null);
                    }
                }
                try {
                    String id = pkulawPunishEntity.getId();
                    StringBuilder sb = new StringBuilder();
                    sb.append(id.substring(0, 19));
                    sb.append(RandomUtil.randomInt(10000, 90000));
                    sb.append(id.substring(24));
                    pkulawPunishEntity.setId(sb.toString());
           /*         Long count = pkulawPunishInsertMapper.selectCount(Wrappers.<PkulawPunishInsertEntity>lambdaQuery().eq(PkulawPunishInsertEntity::getId, id));
                    if (count == 0) {
                        try {
                            pkulawPunishInsertMapper.insert(pkulawPunishEntity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            pkulawPunishInsertMapper.updateById(pkulawPunishEntity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }*/
                    pkulawPunishInsertMapper.insert(pkulawPunishEntity);
                   count.getAndIncrement();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                log.info("读取完毕");
            }
        }).sheet().doRead();
    }
}
