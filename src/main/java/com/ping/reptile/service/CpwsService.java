package com.ping.reptile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.kit.DocumentKit;
import com.ping.reptile.mapper.AccountMapper;
import com.ping.reptile.mapper.ConfigTempMapper;
import com.ping.reptile.mapper.DocumentMapper;
import com.ping.reptile.model.entity.ConfigTempEntity;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.model.vo.Dict;
import com.ping.reptile.model.vo.Result;
import com.ping.reptile.utils.DictUtils;
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
    @Autowired
    private CustomProperties properties;
    @Autowired
    private ConfigTempMapper configTempMapper;
    @Autowired
    private AccountMapper accountMapper;

    private String account;
    private ChromeDriver driver = null;
    private WebDriverWait webDriverWait = null;
    private AtomicInteger days = new AtomicInteger(0);
    private LocalDate date = null;
    private ConfigTempEntity configTempEntity = null;
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
        WebElement element = null;
        try {
            element = driver.findElement(By.partialLinkText("欢迎您"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (element == null) {
            TimeUnit.SECONDS.sleep(5);
            driver.findElement(By.linkText("登录")).click();
            TimeUnit.SECONDS.sleep(5);
            try {
                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("contentIframe")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            driver.switchTo().frame(driver.findElement(By.id("contentIframe")));
            WebElement accountElement = driver.findElement(By.name("username"));
            accountElement.clear();
            account = accountMapper.getAccount();
            if (account == null) {
                return;
            }
            accountElement.sendKeys(account);
            WebElement password = driver.findElement(By.name("password"));
            password.clear();
            password.sendKeys("123456Aa");
            TimeUnit.SECONDS.sleep(2);
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/form/div/div[3]/span")).click();
            accountMapper.updateState(account, "2");
        } else {
            try {
                String text = element.getText();
                String s = text.split("，")[1];
                if (StringUtils.isEmpty(account)) {
                    account = s;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        params();
    }

    public void params() throws InterruptedException {
        try {
            configTempEntity = configTempMapper.selectById(properties.getId());
            if (date == null) {
                date = LocalDate.parse(configTempEntity.getRefereeDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            }
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("inputWrapper")));
            WebElement indexSearch = driver.findElement(By.className("advenced-search"));
            webDriverWait.until(ExpectedConditions.elementToBeClickable(indexSearch));
            indexSearch.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //全文搜索
        String fullTextName = configTempEntity.getFullTextName();
        if (StringUtils.isNotEmpty(fullTextName)) {
            String keyJs = "var temp = document.getElementById('qbValue');temp.value='" + fullTextName.trim() + "'";
            driver.executeScript(keyJs);
        }
        String fullTextType = configTempEntity.getFullTextType();
        if (StringUtils.isNotEmpty(fullTextType)) {
            String type = DictUtils.getFullTextType(fullTextType.trim());
            String qbTypeJs = "var temp = document.getElementById('qbType');temp.setAttribute('data-val','" + type + "');temp.innerText='" + fullTextType.trim() + "';";
            driver.executeScript(qbTypeJs);
        }
        //案件类型
        String caseType = configTempEntity.getCaseType();
        if (StringUtils.isNotEmpty(caseType)) {
            String type = DictUtils.getCaseType(caseType.trim());
            String caseTypeJs = "var temp = document.getElementById('s8');temp.setAttribute('data-val','" + type + "');temp.innerText='" + caseType.trim() + "';";
            driver.executeScript(caseTypeJs);
        }
        //文书类型
        String docType = configTempEntity.getDocType();
        if (StringUtils.isNotEmpty(docType)) {
            String type = DictUtils.getDocType(docType.trim());
            String docTypeJs = "var temp = document.getElementById('s6');temp.setAttribute('data-val','" + type + "');temp.innerText='" + docType.trim() + "';";
            driver.executeScript(docTypeJs);
        }
        //审判程序
        String trialProceedings = configTempEntity.getTrialProceedings();
        if (StringUtils.isNotEmpty(trialProceedings)) {
            String type = DictUtils.getTrialProceedings(trialProceedings.trim());
            String trialProceedingsJs = "var temp = document.getElementById('s9');temp.setAttribute('data-val','" + type + "');temp.setAttribute('data-level','1');temp.innerText='" + trialProceedings.trim() + "';";
            driver.executeScript(trialProceedingsJs);
        }

        String start = date.minusDays(days.get() + configTempEntity.getIntervalDays()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String end = date.minusDays(days.get()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        configTempMapper.updateRefereeDateById(properties.getId(), end);
        days.getAndIncrement();

        String startJs = "var temp = document.getElementById('cprqStart');temp.value='" + start + "'";
        driver.executeScript(startJs);

        String endJs = "var temp = document.getElementById('cprqEnd');temp.value='" + end + "'";
        driver.executeScript(endJs);
        WebElement searchBtn = driver.findElement(By.id("searchBtn"));
        searchBtn.click();
        List<WebElement> pageButtons = driver.findElements(By.className("pageButton"));
        if (pageButtons != null && pageButtons.size() > 0) {
            try {
                TimeUnit.SECONDS.sleep(3);
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
        TimeUnit.SECONDS.sleep(6);
        try {
            WebElement container = driver.findElement(By.className("container"));
            if (container.getText().contains("账号存在违规行为")) {
                try {
                    accountMapper.updateState(account, "3");
                    driver.get(indexUrl);
                    TimeUnit.SECONDS.sleep(5);
                    driver.findElement(By.linkText("退出")).click();
                    TimeUnit.SECONDS.sleep(3);
                    driver.findElement(By.className("layui-layer-btn0")).click();
                    TimeUnit.MINUTES.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                login();
                return;
            }
            webDriverWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("caseName"), 0));
        } catch (Exception e) {
            e.printStackTrace();
            List<WebElement> pageButtons = driver.findElements(By.className("pageButton"));
            if (pageButtons == null || pageButtons.size() == 0) {
                days.getAndAdd(configTempEntity.getIntervalDays());
            } else {
                WebElement nextPage = pageButtons.get(pageButtons.size() - 1);
                String attribute = nextPage.getAttribute("class");
                if (attribute.contains("disabled")) {
                    days.getAndAdd(configTempEntity.getIntervalDays());
                }
            }
            params();
            return;
        }
        String windowHandle = driver.getWindowHandle();
        List<WebElement> elements = driver.findElements(By.className("caseName"));
        for (WebElement element : elements) {
            String href = null;
            try {
                href = element.getAttribute("href");
                details(href);
                driver.close();
                driver.switchTo().window(windowHandle);
            } catch (Exception e) {
                e.printStackTrace();
                driver.switchTo().window(windowHandle);
            }
        }
        List<WebElement> pageButtons = driver.findElements(By.className("pageButton"));
        if (pageButtons == null || pageButtons.size() == 0) {
            days.getAndAdd(configTempEntity.getIntervalDays());
            params();
            return;

        }
        WebElement nextPage = pageButtons.get(pageButtons.size() - 1);
        String attribute = nextPage.getAttribute("class");
        if (attribute.contains("disabled")) {
            days.getAndAdd(configTempEntity.getIntervalDays());
            params();
            return;
        } else {
            nextPage.click();
            page();
        }

    }

    public void details(String docId) {
        try {
            TimeUnit.SECONDS.sleep(RandomUtil.randomInt(configTempEntity.getMin(), configTempEntity.getMax()));
            WebElement container = driver.findElement(By.className("container"));
            if (container != null && container.getText().contains("账号存在违规行为")) {
                try {
                    accountMapper.updateState(account, "3");
                    driver.get(indexUrl);
                    TimeUnit.SECONDS.sleep(5);
                    driver.findElement(By.linkText("退出")).click();
                    TimeUnit.SECONDS.sleep(3);
                    driver.findElement(By.className("layui-layer-btn0")).click();
                    TimeUnit.MINUTES.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                login();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
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
                            if (StringUtils.isNotEmpty(id)) {
                                DocumentEntity entity = DocumentKit.toEntity(jsonObject);
                                entity.setDocType(docTypeMap.get(docType));
                                areaService.convert(entity);
                                try {
                                    log.info("案件名称={}", entity.getName());
                                    documentMapper.insert(entity);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            log.info("错误信息:{}", result);
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            driver.get(docId);
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("PDF_title")));
            TimeUnit.SECONDS.sleep(4);
        } catch (TimeoutException e) {
            e.printStackTrace();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            driver.close();
            driver.switchTo().newWindow(WindowType.TAB);
            driver.get(docId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}