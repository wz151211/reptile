package com.ping.reptile;

import com.ping.reptile.service.CpwsService;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.CapabilityType;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class TestSelenium {

    @Test
    public void test1() throws InterruptedException {
        //     System.setProperty("webdriver.chrome.driver","E:\\tools\\selenuim\\drives\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        options.setHeadless(true);
        options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        options.addArguments("--no-sandbox");
        //  options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        //  options.setScriptTimeout(Duration.ofMillis(1000*30));
        driver = new ChromeDriver(options);

        driver.get("https://wenshu.court.gov.cn/website/wenshu/181107ANFZ0BXSK4/index.html?docId=V3D3+2vF8WLkCh5c4Zaq8MM+tEsth19vwG4aExSWzhtVZAgzXEKWQmI3IS1ZgB82CmSPHiaOuye7o9mKVrJbE5E05I0TrIFLNLK9Low1WMLX7Q6LHX9DlGSETpdxXEva");

/*        String c = "wzws_sessionid=gWI5Y2JkOIAxMTguMTg2LjEzLjc0oGPHSlKCNmY2OTAx; SESSION=374e8341-9bda-4684-b2e0-cc248fd6d6fc; wzws_cid=4406eca483c5abff80edbc130d26cb20ffc8589e76f8466f95a25e5755916dec07e150181e63be75d9e94aa3c4e63b3b655c1b27640d0af330b80a9fee9f664b4db977ee7b70bab861ccc89cd7cae47d";
        WebDriver.Options manage = driver.manage();
        for (String s : c.split(";")) {
            String[] split = s.split("=");
            Cookie cookie = new Cookie(split[0].trim(), split[1].trim(), "wenshu.court.gov.cn", "/", null);
            manage.addCookie(cookie);
        }*/
        Cookie cookie = new Cookie("SESSION", "7e9f0952-1f5c-4404-94b7-079edef91642", "wenshu.court.gov.cn", "/", null);
        driver.manage().addCookie(cookie);

        TimeUnit.SECONDS.sleep(5);
        System.out.println(driver.getTitle());
        String pageSource = driver.getPageSource();
        System.out.println(pageSource);
        Logs logs = driver.manage().logs();
        System.out.println("--------------------------------------");
        for (LogEntry network : logs.get(LogType.PROFILER)) {
            //   System.out.println(network.toString());
            //   System.out.println(network.getMessage());
            System.out.println(network.toJson());
        }

        /// driver.close();

    }

    @Test
    public void test3() throws InterruptedException {
        //     System.setProperty("webdriver.chrome.driver","E:\\tools\\selenuim\\drives\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        options.setHeadless(true);
        options.addArguments("--no-sandbox");
        //  options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        //  options.setScriptTimeout(Duration.ofMillis(1000*30));
        driver = new ChromeDriver(options);


        driver.get("https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?pageId=17639573f4a798078cde65d16daeb879&s8=02");

/*        String c = "wzws_sessionid=gWI5Y2JkOIAxMTguMTg2LjEzLjc0oGPHSlKCNmY2OTAx; SESSION=374e8341-9bda-4684-b2e0-cc248fd6d6fc; wzws_cid=4406eca483c5abff80edbc130d26cb20ffc8589e76f8466f95a25e5755916dec07e150181e63be75d9e94aa3c4e63b3b655c1b27640d0af330b80a9fee9f664b4db977ee7b70bab861ccc89cd7cae47d";
        WebDriver.Options manage = driver.manage();
        for (String s : c.split(";")) {
            String[] split = s.split("=");
            Cookie cookie = new Cookie(split[0].trim(), split[1].trim(), "wenshu.court.gov.cn", "/", null);
            manage.addCookie(cookie);
        }*/
        Cookie cookie = new Cookie("SESSION", "7e9f0952-1f5c-4404-94b7-079edef91642", "wenshu.court.gov.cn", "/", null);
        driver.manage().addCookie(cookie);

        TimeUnit.SECONDS.sleep(5);
        System.out.println(driver.getTitle());
        String pageSource = driver.getPageSource();
        System.out.println(pageSource);
        /// driver.close();

    }

    @Test
    public void test4() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        CpwsService cpwsService = new CpwsService();
       // cpwsService.login();
        cpwsService.params();
       // cpwsService.page();
    }
}
