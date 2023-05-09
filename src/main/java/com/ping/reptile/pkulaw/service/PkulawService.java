package com.ping.reptile.pkulaw.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ping.reptile.kit.DocumentKit;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.model.vo.Result;
import com.ping.reptile.utils.TripleDES;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v112.network.Network;
import org.openqa.selenium.devtools.v112.network.model.Response;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PkulawService {
    private ChromeDriver driver = null;


    {
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "localhost:9000");
        options.addArguments("--headless=new");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.addArguments("--disable-blink-features=AutomationControlled");
        driver = new ChromeDriver(options);
        //  webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void list() {
        try {
            driver.get("https://www.pkulaw.com/");
            TimeUnit.SECONDS.sleep(3);
            WebElement advCheckBtn = driver.findElement(By.id("advCheckBtn"));
            TimeUnit.SECONDS.sleep(3);
            advCheckBtn.click();
            List<WebElement> lis = driver.findElements(By.tagName("li"));
            List<WebElement> lis2 = driver.findElements(By.cssSelector(".el-dropdown-menu__item"));
            for (WebElement li : lis) {
                if (li.getText().equals("不分组")) {
                    li.click();
                    return;
                }
            }

   /*         WebElement pageButton = driver.findElement(By.className("btn-next"));
            if (pageButton == null) {
                return;
            }
            String attribute = pageButton.getAttribute("disabled");
            if (attribute.contains("disabled")) {
                return;
            }*/
            DevTools tools = driver.getDevTools();
            tools.createSession();
            tools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            tools.addListener(Network.responseReceived(), res -> {
                Response response = res.getResponse();
                String url = response.getUrl();
                String responseBody = tools.send(Network.getResponseBody(res.getRequestId())).getBody();
                System.out.println(url);
                System.out.println(responseBody);
            });
            List<WebElement> elements = driver.findElements(By.cssSelector("table tr .name"));
            String windowHandle = driver.getWindowHandle();
            for (WebElement element : elements) {
                String href = element.getAttribute("href");
                driver.switchTo().newWindow(WindowType.TAB);
                driver.get(href);
                TimeUnit.SECONDS.sleep(10);
                driver.switchTo().window(windowHandle);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
