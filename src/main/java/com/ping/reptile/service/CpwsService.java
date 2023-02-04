package com.ping.reptile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ping.reptile.kit.DocumentKit;
import com.ping.reptile.mapper.DocumentMapper;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.model.vo.Dict;
import com.ping.reptile.model.vo.Result;
import com.ping.reptile.utils.TripleDES;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.network.Network;
import org.openqa.selenium.devtools.v109.network.model.Response;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class CpwsService {
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private AreaService areaService;
    private ChromeDriver driver = null;
    private DevTools devTools = null;
    private WebDriverWait webDriverWait = null;
    private Actions actions = null;
    private int interval = 10;
    private AtomicInteger days = new AtomicInteger(0);
    private LocalDate date = LocalDate.of(2020, 10, 03);
    private final String indexUrl = "https://wenshu.court.gov.cn/";

    private List<Dict> areas = new ArrayList<>();
    private List<Dict> docTypes = new ArrayList<>();
    private Map<String, String> docTypeMap = new HashMap<>();


    {

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        options.setHeadless(true);
        options.addArguments("--no-sandbox");
        //  options.setExperimentalOption("excludeSwitches", "enable-automation");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        //  options.addArguments("--disable-blink-features=AutomationControlled");
        driver = new ChromeDriver(options);
        //  options.addArguments("--window-size=1920,1080");
        //   devTools = driver.getDevTools();
        //   devTools.createSession();
        webDriverWait = new WebDriverWait(driver, Duration.ofMillis(60 * 1000));
        actions = new Actions(driver);


    }

    {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:doc/*.txt");
            for (Resource resource : resources) {
                if ("area.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    areas.addAll(JSON.parseArray(text, Dict.class));
                }
                if ("docType.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    docTypes.addAll(JSON.parseArray(text, Dict.class));
                    for (Dict docType : docTypes) {
                        docTypeMap.put(docType.getCode(), docType.getName());
                    }
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public void login() throws InterruptedException {
        driver.get(indexUrl);
        //  webDriverWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.linkText("登录"))));
        WebElement element = null;
        try {
            element = driver.findElement(By.partialLinkText("欢迎您"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (element != null) {
            return;
        }
        TimeUnit.SECONDS.sleep(5);
        driver.findElement(By.linkText("登录")).click();
        TimeUnit.SECONDS.sleep(5);
        try {
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("contentIframe")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver.switchTo().frame(driver.findElement(By.id("contentIframe")));
        WebElement account = driver.findElement(By.name("username"));
        account.clear();
        account.sendKeys("17008375707");
        WebElement password = driver.findElement(By.name("password"));
        password.clear();
        password.sendKeys("123456Aa");
        TimeUnit.SECONDS.sleep(5);
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/form/div/div[3]/span")).click();
        //   TimeUnit.SECONDS.sleep(5);
        //  driver.get("https://wenshu.court.gov.cn/website/wenshu/181029CR4M5A62CH/index.html");
        params();
    }

    public void params() throws InterruptedException {
        // driver.get("https://wenshu.court.gov.cn/website/wenshu/181029CR4M5A62CH/index.html");
        try {
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("inputWrapper")));
            WebElement indexSearch = driver.findElement(By.className("advenced-search"));
            webDriverWait.until(ExpectedConditions.elementToBeClickable(indexSearch));
            indexSearch.click();
            WebElement resetBtn = driver.findElement(By.id("resetBtn"));
            //   webDriverWait.until(ExpectedConditions.elementToBeClickable(resetBtn));
            resetBtn.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //全文搜索
        String keyJs = "var temp = document.getElementById('qbValue');temp.value='家庭暴力'";
        driver.executeScript(keyJs);

        String qbTypeJs = "var temp = document.getElementById('qbType');temp.setAttribute('data-val','5');temp.innerText='事实';";
        driver.executeScript(qbTypeJs);
        //案件类型
        String caseTypeJs = "var temp = document.getElementById('s8');temp.setAttribute('data-val','03');temp.innerText='民事案件';";
        driver.executeScript(caseTypeJs);

        //文书类型
        String docTypeJs = "var temp = document.getElementById('s6');temp.setAttribute('data-val','01');temp.innerText='判决书';";
        driver.executeScript(docTypeJs);

        //审判程序
        String trialProceedingsJs = "var temp = document.getElementById('s9');temp.setAttribute('data-val','0301');temp.setAttribute('data-level','1');temp.innerText='民事一审';";
        driver.executeScript(trialProceedingsJs);

        String start = date.minusDays(days.get() + interval).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String end = date.minusDays(days.get()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        days.getAndIncrement();

        String startJs = "var temp = document.getElementById('cprqStart');temp.value='" + start + "'";
        driver.executeScript(startJs);
 /*       WebElement cprqStart = driver.findElement(By.id("cprqStart"));
        cprqStart.clear();
        cprqStart.sendKeys(start);*/

        String endJs = "var temp = document.getElementById('cprqEnd');temp.value='" + end + "'";
        driver.executeScript(endJs);
  /*      WebElement cprqEnd = driver.findElement(By.id("cprqEnd"));
        cprqEnd.clear();
        cprqEnd.sendKeys(end);*/
        WebElement searchBtn = driver.findElement(By.id("searchBtn"));
        webDriverWait.until(ExpectedConditions.elementToBeClickable(searchBtn));
        searchBtn.click();
        List<WebElement> pageButtons = driver.findElements(By.className("pageButton"));
        if (pageButtons != null && pageButtons.size() > 0) {
            try {
                TimeUnit.SECONDS.sleep(3);
                //*[@id="_view_1545184311000"]/div[2]/div[2]/a
                WebElement order = driver.findElement(By.xpath("//*[@id=\"_view_1545184311000\"]/div[2]/div[2]/a"));
                webDriverWait.until(ExpectedConditions.elementToBeClickable(order));
                order.click();
                TimeUnit.SECONDS.sleep(3);
                WebElement pageSizeSelect = driver.findElement(By.className("pageSizeSelect"));
                Select select = new Select(pageSizeSelect);
                List<WebElement> options = select.getOptions();
                options.get(2).click();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        page();
    }

    public void page() throws InterruptedException {
        TimeUnit.SECONDS.sleep(RandomUtil.randomInt(5, 10));
        //  driver.findElement(By.xpath("//*[@id=\"_view_1545184311000\"]/div[2]/div[2]/a")).click();
        try {
            webDriverWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("caseName"), 0));
        } catch (Exception e) {
            e.printStackTrace();
            List<WebElement> pageButtons = driver.findElements(By.className("pageButton"));
            if (pageButtons == null || pageButtons.size() == 0) {
                days.getAndAdd(interval);
            } else {
                WebElement nextPage = pageButtons.get(pageButtons.size() - 1);
                String attribute = nextPage.getAttribute("class");
                if (attribute.contains("disabled")) {
                    days.getAndAdd(interval);
                }
            }

            params();
        }
        List<WebElement> elements = driver.findElements(By.className("caseName"));
        String windowHandle = driver.getWindowHandle();
        for (WebElement element : elements) {
            String href = element.getAttribute("href");
            details(href);
            driver.switchTo().window(windowHandle);
        }
        List<WebElement> pageButtons = driver.findElements(By.className("pageButton"));
        if (pageButtons == null || pageButtons.size() == 0) {
            days.getAndAdd(interval);
            params();
            return;

        }
        WebElement nextPage = pageButtons.get(pageButtons.size() - 1);
        String attribute = nextPage.getAttribute("class");
        if (attribute.contains("disabled")) {
            days.getAndAdd(interval);
            params();
        } else {
            nextPage.click();
            TimeUnit.SECONDS.sleep(5);
            page();
        }

    }

    public void details(String docId) throws InterruptedException {
        TimeUnit.SECONDS.sleep(RandomUtil.randomInt(5,10));
        driver.switchTo().newWindow(WindowType.TAB);
        DevTools tools = driver.getDevTools();
        tools.createSession();
        tools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        tools.addListener(Network.responseReceived(), res -> {
            Response response = res.getResponse();
            String url = response.getUrl();
            String responseBody = tools.send(Network.getResponseBody(res.getRequestId())).getBody();
            if (url.contains("rest.q4w")) {
                Result result = JSON.parseObject(responseBody, Result.class);
                if (StringUtils.isNotEmpty(result.getSecretKey())) {
                    log.info("detail--code={},desc={}", result.getCode(), result.getDescription());
                    if (result.getSuccess()) {
                        String iv = DateUtil.format(new Date(), "yyyyMMdd");
                        String decrypt = TripleDES.decrypt(result.getSecretKey(), result.getResult(), iv);
                        JSONObject jsonObject = JSON.parseObject(decrypt);
                        String id = jsonObject.getString("s5");
                        String docType = jsonObject.getString("s6");
                        if (StringUtils.isEmpty(id)) {
                            log.info("案件详情:{}", jsonObject);
                            return;
                        }
                        DocumentEntity entity = DocumentKit.toEntity(jsonObject);
                        entity.setDocType(docTypeMap.get(docType));
                        areaService.convert(entity);
                        try {
                            documentMapper.insert(entity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        log.info("错误信息:{}", result);
                    }
                }

            }
        });
        driver.get(docId);
        try {
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("PDF_title")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        TimeUnit.SECONDS.sleep(RandomUtil.randomInt(3, 6));
        driver.close();
    }
}