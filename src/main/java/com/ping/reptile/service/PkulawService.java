package com.ping.reptile.service;

import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.mapper.ConfigTempMapper;
import com.ping.reptile.mapper.CourtMapper;
import com.ping.reptile.mapper.DocumentMapper;
import com.ping.reptile.model.entity.ConfigTempEntity;
import com.ping.reptile.model.vo.Dict;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
            driver.get("https://www.pkulaw.com/advanced/law/chl/");
            TimeUnit.SECONDS.sleep(3);
            List<WebElement> mores = driver.findElements(By.className("more"));
            TimeUnit.SECONDS.sleep(3);
          //  mores.get(0).click();

            TimeUnit.SECONDS.sleep(3);

            List<WebElement> elements = driver.findElements(By.cssSelector("table a"));
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
