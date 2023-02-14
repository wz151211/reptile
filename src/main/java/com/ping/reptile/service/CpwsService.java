package com.ping.reptile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ping.reptile.common.properties.CustomProperties;
import com.ping.reptile.kit.DocumentKit;
import com.ping.reptile.mapper.AccountMapper;
import com.ping.reptile.mapper.ConfigTempMapper;
import com.ping.reptile.mapper.CourtMapper;
import com.ping.reptile.mapper.DocumentMapper;
import com.ping.reptile.model.entity.AccountEntity;
import com.ping.reptile.model.entity.ConfigTempEntity;
import com.ping.reptile.model.entity.CourtEntity;
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
import java.time.LocalDateTime;
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
    private AccountService accountService;

    @Autowired
    private CourtMapper courtMapper;

    private String account;
    private ChromeDriver driver = null;
    private WebDriverWait webDriverWait = null;
    private AtomicInteger days = new AtomicInteger(0);
    private LocalDate date = null;
    private LocalDate endDate = null;
    private LocalDateTime loginDate = LocalDateTime.now();
    private ConfigTempEntity configTempEntity = null;
    private final String indexUrl = "https://wenshu.court.gov.cn/";

    private final List<Dict> areas = new ArrayList<>();
    private final List<Dict> docTypes = new ArrayList<>();
    private final Map<String, String> docTypeMap = new HashMap<>();


    {

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        options.setHeadless(true);
        options.addArguments("--no-sandbox");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.addArguments("--disable-blink-features=AutomationControlled");
        driver = new ChromeDriver(options);
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

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

    public void login() {
        driver.get(indexUrl);
        WebElement element = null;
        try {
            element = driver.findElement(By.partialLinkText("欢迎您"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (element == null) {
            try {
                TimeUnit.SECONDS.sleep(5);
                driver.findElement(By.linkText("登录")).click();
                TimeUnit.SECONDS.sleep(5);
                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("contentIframe")));
            } catch (Exception e) {
                e.printStackTrace();
                driver.navigate().refresh();
            }
            driver.switchTo().frame(driver.findElement(By.id("contentIframe")));
            WebElement accountElement = driver.findElement(By.name("username"));
            accountElement.clear();
            AccountEntity entity = accountService.getAccount(properties.getCategory());
            if (entity == null) {
                return;
            } else {
                account = entity.getAccount();
            }
            accountElement.sendKeys(account);
            WebElement password = driver.findElement(By.name("password"));
            password.clear();
            password.sendKeys("123456Aa");
            try {
                TimeUnit.SECONDS.sleep(2);
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/form/div/div[3]/span")).click();
                accountService.updateState(account, 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void logout() {
        try {
            driver.get(indexUrl);
            TimeUnit.SECONDS.sleep(3);
            driver.findElement(By.linkText("退出")).click();
            TimeUnit.SECONDS.sleep(3);
            driver.findElement(By.className("layui-layer-btn0")).click();
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void params() {
        try {
            TimeUnit.SECONDS.sleep(2);
            if (loginDate.plusHours(2).isBefore(LocalDateTime.now())) {
                logout();
                accountService.updateState(account, 3);
                TimeUnit.MINUTES.sleep(3);
                loginDate = LocalDateTime.now();
                login();
            }
            configTempEntity = configTempMapper.selectById(properties.getId());
            if (date == null) {
                date = LocalDate.parse(configTempEntity.getRefereeDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if (endDate == null) {
                endDate = LocalDate.parse(configTempEntity.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if (date.minusDays(days.get()).isBefore(endDate)) {
                if (configTempEntity.getSearchType() == 1) {
                    courtMapper.updateStateByName(configTempEntity.getCourtName(), 1);
                    CourtEntity court = courtMapper.getCourt();
                    configTempMapper.updateCourtNameById(properties.getId(), court.getName());
                    configTempMapper.updateRefereeDateById(properties.getId(), LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    days.set(0);
                }
                if (configTempEntity.getSearchType() == 2) {
                    log.info("当前查询条件数据已查询完成");
                    return;
                }

            }
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("inputWrapper")));
            WebElement indexSearch = driver.findElement(By.className("advenced-search"));
            TimeUnit.SECONDS.sleep(3);
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

        //法院名称
        String courtName = configTempEntity.getCourtName();
        if (StringUtils.isEmpty(courtName) && configTempEntity.getSearchType() == 1) {
            CourtEntity court = courtMapper.getCourt();
            courtName = court.getName();
            courtMapper.updateStateByName(courtName, 2);
            configTempMapper.updateCourtNameById(properties.getId(), court.getName());
        }
        if (StringUtils.isNotEmpty(courtName)) {
            String keyJs = "var temp = document.getElementById('s2');temp.value='" + courtName.trim() + "'";
            driver.executeScript(keyJs);
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

        //文书类型
        String cause = configTempEntity.getCause();
        if (StringUtils.isNotEmpty(cause)) {
            String type = DictUtils.getCause(cause.trim());
            String causeJs = "var temp = document.getElementById('s16');temp.setAttribute('data-val','" + type + "');temp.setAttribute('data-level','3');temp.innerText='" + cause.trim() + "';";
            driver.executeScript(causeJs);
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
        try {
            WebElement searchBtn = driver.findElement(By.id("searchBtn"));
            TimeUnit.SECONDS.sleep(3);
            searchBtn.click();
        } catch (InterruptedException e) {
            e.printStackTrace();
            driver.navigate().refresh();
            params();
        }
        List<WebElement> pageButtons = driver.findElements(By.className("pageButton"));
        if (pageButtons != null && pageButtons.size() > 0) {
            try {
                TimeUnit.SECONDS.sleep(3);
                WebElement order = driver.findElement(By.xpath("//*[@id=\"_view_1545184311000\"]/div[2]/div[2]/a"));
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
        days.getAndAdd(configTempEntity.getIntervalDays());
        params();
    }

    public void page() {
        try {
            TimeUnit.SECONDS.sleep(6);
            violation();
            LocalDateTime start = LocalDateTime.now();
            if ((start.getMinute() <= 2) || (start.getMinute() >= 30 && start.getMinute() <= 31)) {
                TimeUnit.MINUTES.sleep(3);
            }
            webDriverWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("caseName"), 0));
        } catch (Exception e) {
            e.printStackTrace();
            List<WebElement> pageButtons = driver.findElements(By.className("pageButton"));
            if (pageButtons == null || pageButtons.size() == 0) {
                return;
            }
            WebElement nextPage = pageButtons.get(pageButtons.size() - 1);
            String attribute = nextPage.getAttribute("class");
            if (attribute.contains("disabled")) {
                return;
            }
        }
        String windowHandle = driver.getWindowHandle();
        List<WebElement> elements = driver.findElements(By.className("caseName"));
        for (WebElement element : elements) {
            String href = null;
            try {
                href = element.getAttribute("href");
                details(href);
                driver.close();
                try {
                    Set<String> handles = driver.getWindowHandles();
                    if (handles != null && handles.size() > 1) {
                        for (String handle : handles) {
                            if (handle.equals(windowHandle)) {
                                continue;
                            }
                            driver.switchTo().window(handle);
                            driver.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                driver.switchTo().window(windowHandle);
            } catch (Exception e) {
                e.printStackTrace();
                driver.switchTo().window(windowHandle);
            }
        }
        List<WebElement> pageButtons = driver.findElements(By.className("pageButton"));
        if (pageButtons == null || pageButtons.size() == 0) {
            return;
        }
        WebElement nextPage = pageButtons.get(pageButtons.size() - 1);
        String attribute = nextPage.getAttribute("class");
        if (attribute.contains("disabled")) {
            return;
        } else {
            nextPage.click();
            page();
        }
    }

    public void details(String docId) {
        try {
            TimeUnit.SECONDS.sleep(RandomUtil.randomInt(configTempEntity.getMin(), configTempEntity.getMax()));
            LocalDateTime start = LocalDateTime.now();
            if ((start.getMinute() <= 1) || (start.getMinute() >= 30 && start.getMinute() <= 31)) {
                TimeUnit.MINUTES.sleep(3);
            }
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
        } catch (TimeoutException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            long start = System.currentTimeMillis();
            driver.get(docId);
            long end = System.currentTimeMillis();
            if (end - start > 10 * 1000) {
                driver.close();
                return;
            }
            violation();
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("PDF_title")));
            TimeUnit.SECONDS.sleep(2);
        } catch (TimeoutException e) {
            e.printStackTrace();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            driver.close();
            driver.switchTo().newWindow(WindowType.TAB);
            long start = System.currentTimeMillis();
            driver.get(docId);
            long end = System.currentTimeMillis();
            if (end - start > 10 * 1000) {
                driver.close();
                return;
            }
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("PDF_title")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void violation() {
        try {
            WebElement container = driver.findElement(By.className("container"));
            if (container != null && container.getText().contains("账号存在违规行为")) {
                try {
                    accountService.updateState(account, -12);
                    logout();
                    TimeUnit.MINUTES.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                login();
                params();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}