package com.ping.reptile.utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;

/**
 * @author: W.Z
 * @date: 2021/6/3 14:23
 * @desc:
 */
@Slf4j
public class HtmlUnitUtils {

    public static HtmlPage getHtmlPage(String url) {
        try (final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED)) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.waitForBackgroundJavaScript(600 * 1000);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            return webClient.getPage(url);

        } catch (IOException e) {
            log.info("HtmlUnit获取页面出错", e);
        }

        return null;
    }

    public static HtmlPage getHtmlPage(String url, String cookie, String origin) {
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {


            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.waitForBackgroundJavaScript(600 * 1000);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.addRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            webClient.addRequestHeader("X-Real-IP", IpUtils.getIp());
            webClient.addRequestHeader("X-Forwarded-For", IpUtils.getIp());
            webClient.addRequestHeader("Accept-Encoding", "gzip, deflate, br");
            webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.9");
            webClient.addRequestHeader("Connection", "keep-alive");
            webClient.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            webClient.addRequestHeader("Host", "wenshu.court.gov.cn");
            webClient.addRequestHeader("Origin", "https://wenshu.court.gov.cn");
            webClient.addRequestHeader("Pragma", "no-cache");
            webClient.addRequestHeader("Referer", url);
            webClient.addRequestHeader("Sec-Fetch-Dest", "empty");
            webClient.addRequestHeader("Sec-Fetch-Mode", "cors");
            webClient.addRequestHeader("Sec-Fetch-Site", "same-origin");
            webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
            webClient.addRequestHeader("sec-ch-ua", "\"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"108\", \"Google Chrome\";v=\"108\"");
            webClient.addRequestHeader("sec-ch-ua-mobile", "?0");
            webClient.addRequestHeader("sec-ch-ua-platform", "macOS");
            webClient.addRequestHeader("X-Requested-With", "XMLHttpRequest");

            URL u = new URL(url);
            webClient.addCookie(cookie, u, origin);
            return webClient.getPage(url);

        } catch (IOException e) {
            log.info("HtmlUnit获取页面出错", e);
        }

        return null;
    }
}
