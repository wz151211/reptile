package com.ping.reptile;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.mapper.AreaMapper;
import com.ping.reptile.mapper.CauseMapper;
import com.ping.reptile.mapper.CourtMapper;
import com.ping.reptile.mapper.DocumentMapper;
import com.ping.reptile.model.entity.AreaEntity;
import com.ping.reptile.model.entity.CauseEntity;
import com.ping.reptile.model.entity.CourtEntity;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.model.vo.Dict;
import com.ping.reptile.model.vo.Result;
import com.ping.reptile.service.*;
import com.ping.reptile.utils.IpUtils;
import com.ping.reptile.utils.ParamsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class ReptileApplicationTests {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CasePunishService casePunishService;

    @Autowired
    private UpdateDocumentService updateDocumentService;

    @Autowired
    private CpwsService cpwsService;
    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private PunishService punishService;

    @Test
    void contextLoads() {

        // documentService.parse();
    }


    @Test
    public void punish() {
        punishService.page(1, 20);
    }

    @Test
    public void casePunish() {
        casePunishService.page(1, 20);
    }

    @Test
    public void updateDocument() {
        updateDocumentService.update();
    }

    @Test
    public void updateDocument1() {
        updateDocumentService.save();
    }

    @Test
    public void cpws() {
        try {
            cpwsService.login();
            //cpwsService.params();
            //  cpwsService.page();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private AreaMapper orgMapper;

    @Test
    public void testOrg() throws InterruptedException, IOException {
        String index = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2022/";
        Document document = Jsoup.connect(index + "index.html").get();
        TimeUnit.SECONDS.sleep(3);
        for (Element element : document.select(".provincetr a")) {
            String href = element.attr("href");
            String provinceName = element.text();
            String provinceCode = href.substring(0, 2) + "0000000000";
            AreaEntity provice = new AreaEntity();
            provice.setId(provinceCode);
            provice.setName(provinceName);
            provice.setProvince(provinceName);
            provice.setPid("-1");
            provice.setLevel(1);
            provice.setPath("/" + provinceCode);
            System.out.println(provice);
            orgMapper.insert(provice);
            Document cityDocument = Jsoup.connect(index + "/" + href).get();
            TimeUnit.SECONDS.sleep(1);
            for (Element city : cityDocument.select(".citytr")) {
                Elements cityList = city.select("a");
                Element cityCode = cityList.get(0);
                Element cityName = cityList.get(1);
                AreaEntity cityOrg = new AreaEntity();
                cityOrg.setId(cityCode.text());
                cityOrg.setName(cityName.text());
                cityOrg.setPid(provinceCode);
                cityOrg.setLevel(2);
                cityOrg.setProvince(provinceName);
                cityOrg.setCity(cityName.text());
                cityOrg.setPath("/" + provinceCode + "/" + cityCode.text());
                System.out.println(cityOrg);
                orgMapper.insert(cityOrg);
                String href1 = cityCode.attr("href");
                Document countyDocument = Jsoup.connect(index + "/" + href1).get();
                TimeUnit.SECONDS.sleep(1);
                for (Element county : countyDocument.select(".countytr")) {
                    Elements select = county.select("a");
                    if (select == null || select.size() == 0) {
                        continue;
                    }
                    Element countyCode = select.get(0);
                    Element countyName = select.get(1);

                    AreaEntity countyOrg = new AreaEntity();
                    countyOrg.setId(countyCode.text());
                    countyOrg.setProvince(provinceName);
                    countyOrg.setCity(cityOrg.getCity());
                    countyOrg.setCounty(countyName.text());
                    countyOrg.setName(countyName.text());
                    countyOrg.setPid(countyCode.text());
                    countyOrg.setLevel(3);
                    countyOrg.setPath("/" + provinceCode + "/" + cityOrg.getId() + "/" + countyCode.text());
                    System.out.println(countyOrg);
                    orgMapper.insert(countyOrg);
                }

            }

        }
    }

    @Autowired
    private CourtMapper courtMapper;

    @Test
    public void testCourt() {
        //  String html = "<div id=\"idx_map_content\" class=\"index_map_content\"> <div class=\"map_p map_p_beijing\" _name=\"北京\" _code=\"beijing\" data-val=\"1\">北京</div> <div class=\"map_p map_p_tianjin\" _name=\"天津\" _code=\"tianjin\" data-val=\"51\">天津</div> <div class=\"map_p map_p_shanghai\" _name=\"上海\" _code=\"shanghai\" data-val=\"1100\">上海</div> <div class=\"map_p map_p_chongqing\" _name=\"重庆\" _code=\"chongqing\" data-val=\"2950\">重庆</div> <div class=\"map_p map_p_hebei\" _name=\"河北\" _code=\"hebei\" data-val=\"100\">河北</div> <div class=\"map_p map_p_shanxi\" _name=\"山西\" _code=\"shanxi\" data-val=\"300\">山西</div> <div class=\"map_p map_p_liaoning\" _name=\"辽宁\" _code=\"liaoning\" data-val=\"600\">辽宁</div> <div class=\"map_p map_p_jilin\" _name=\"吉林\" _code=\"jilin\" data-val=\"750\">吉林</div> <div class=\"map_p map_p_heilongjiang\" _name=\"黑龙江\" _code=\"heilongjiang\" data-val=\"850\">黑龙江</div> <div class=\"map_p map_p_jiangsu\" _name=\"江苏\" _code=\"jiangsu\" data-val=\"1150\">江苏</div> <div class=\"map_p map_p_zhejiang\" _name=\"浙江\" _code=\"zhejiang\" data-val=\"1300\">浙江</div> <div class=\"map_p map_p_anhui\" _name=\"安徽\" _code=\"anhui\" data-val=\"1451\">安徽</div> <div class=\"map_p map_p_fujian\" _name=\"福建\" _code=\"fujian\" data-val=\"1600\">福建</div> <div class=\"map_p map_p_jiangxi\" _name=\"江西\" _code=\"jiangxi\" data-val=\"1700\">江西</div> <div class=\"map_p map_p_shandong\" _name=\"山东\" _code=\"shandong\" data-val=\"1850\">山东</div> <div class=\"map_p map_p_henan\" _name=\"河南\" _code=\"henan\" data-val=\"2050\">河南</div> <div class=\"map_p map_p_hubei\" _name=\"湖北\" _code=\"hubei\" data-val=\"2250\">湖北</div> <div class=\"map_p map_p_hunan\" _name=\"湖南\" _code=\"hunan\" data-val=\"2400\">湖南</div> <div class=\"map_p map_p_guangdong\" _name=\"广东\" _code=\"guangdong\" data-val=\"2550\">广东</div> <div class=\"map_p map_p_hainan\" _name=\"海南\" _code=\"hainan\" data-val=\"2900\">海南</div> <div class=\"map_p map_p_sichuan\" _name=\"四川\" _code=\"sichuan\" data-val=\"3000\">四川</div> <div class=\"map_p map_p_guizhou\" _name=\"贵州\" _code=\"guizhou\" data-val=\"3250\">贵州</div> <div class=\"map_p map_p_yunnan\" _name=\"云南\" _code=\"yunnan\" data-val=\"3350\">云南</div> <div class=\"map_p map_p_shanxi2\" _name=\"陕西\" _code=\"shanxi2\" data-val=\"3600\">陕西</div> <div class=\"map_p map_p_gansu\" _name=\"甘肃\" _code=\"gansu\" data-val=\"3750\">甘肃</div> <div class=\"map_p map_p_qinghai\" _name=\"青海\" _code=\"qinghai\" data-val=\"3900\">青海</div> <div class=\"map_p map_p_taiwan\" _name=\"台湾\" _code=\"taiwan\" data-val=\"\">台湾</div> <div class=\"map_p map_p_neimenggu\" _name=\"内蒙古\" _code=\"neimenggu\" data-val=\"451\">内蒙古</div> <div class=\"map_p map_p_guangxi\" _name=\"广西\" _code=\"guangxi\" data-val=\"2750\">广西</div> <div class=\"map_p map_p_xizang\" _name=\"西藏\" _code=\"xizang\" data-val=\"3500\">西藏</div> <div class=\"map_p map_p_ningxia\" _name=\"宁夏\" _code=\"ningxia\" data-val=\"4000\">宁夏</div> <div class=\"map_p map_p_xinjiang\" _name=\"新疆\" _code=\"xinjiang\" data-val=\"4050\">新疆</div> <div class=\"map_p map_p_aomen\" _name=\"澳门\" _code=\"aomen\" data-val=\"\">澳门</div> <div class=\"map_p map_p_xianggang\" _name=\"香港\" _code=\"xianggang\" data-val=\"\">香港</div> </div>";
        String html = "<div id=\"idx_map_content\" class=\"index_map_content\"> <div class=\"map_p map_p_anhui\" _name=\"安徽\" _code=\"anhui\" data-val=\"1451\">安徽</div> <div class=\"map_p map_p_fujian\" _name=\"福建\" _code=\"fujian\" data-val=\"1600\">福建</div> <div class=\"map_p map_p_jiangxi\" _name=\"江西\" _code=\"jiangxi\" data-val=\"1700\">江西</div> <div class=\"map_p map_p_shandong\" _name=\"山东\" _code=\"shandong\" data-val=\"1850\">山东</div> <div class=\"map_p map_p_henan\" _name=\"河南\" _code=\"henan\" data-val=\"2050\">河南</div> <div class=\"map_p map_p_hubei\" _name=\"湖北\" _code=\"hubei\" data-val=\"2250\">湖北</div> <div class=\"map_p map_p_hunan\" _name=\"湖南\" _code=\"hunan\" data-val=\"2400\">湖南</div> <div class=\"map_p map_p_guangdong\" _name=\"广东\" _code=\"guangdong\" data-val=\"2550\">广东</div> <div class=\"map_p map_p_hainan\" _name=\"海南\" _code=\"hainan\" data-val=\"2900\">海南</div> <div class=\"map_p map_p_sichuan\" _name=\"四川\" _code=\"sichuan\" data-val=\"3000\">四川</div> <div class=\"map_p map_p_guizhou\" _name=\"贵州\" _code=\"guizhou\" data-val=\"3250\">贵州</div> <div class=\"map_p map_p_yunnan\" _name=\"云南\" _code=\"yunnan\" data-val=\"3350\">云南</div> <div class=\"map_p map_p_shanxi2\" _name=\"陕西\" _code=\"shanxi2\" data-val=\"3600\">陕西</div> <div class=\"map_p map_p_gansu\" _name=\"甘肃\" _code=\"gansu\" data-val=\"3750\">甘肃</div> <div class=\"map_p map_p_qinghai\" _name=\"青海\" _code=\"qinghai\" data-val=\"3900\">青海</div> <div class=\"map_p map_p_taiwan\" _name=\"台湾\" _code=\"taiwan\" data-val=\"\">台湾</div> <div class=\"map_p map_p_neimenggu\" _name=\"内蒙古\" _code=\"neimenggu\" data-val=\"451\">内蒙古</div> <div class=\"map_p map_p_guangxi\" _name=\"广西\" _code=\"guangxi\" data-val=\"2750\">广西</div> <div class=\"map_p map_p_xizang\" _name=\"西藏\" _code=\"xizang\" data-val=\"3500\">西藏</div> <div class=\"map_p map_p_ningxia\" _name=\"宁夏\" _code=\"ningxia\" data-val=\"4000\">宁夏</div> <div class=\"map_p map_p_xinjiang\" _name=\"新疆\" _code=\"xinjiang\" data-val=\"4050\">新疆</div> <div class=\"map_p map_p_aomen\" _name=\"澳门\" _code=\"aomen\" data-val=\"\">澳门</div> <div class=\"map_p map_p_xianggang\" _name=\"香港\" _code=\"xianggang\" data-val=\"\">香港</div> </div>";
        Document document = Jsoup.parse(html);
        Element mapContent = document.getElementById("idx_map_content");
        List<Dict> countList = new CopyOnWriteArrayList<>();
        Integer level = 0;
        TestHttp testHttp = new TestHttp();
        for (Element child : mapContent.children()) {
            String name = child.attr("_name");
            String code = child.attr("data-val");
            System.out.println("name=" + name + "--code=" + code);
            if (StringUtils.isEmpty(code)) {
                continue;
            }
            List<Dict> courts = testHttp.getCourt(0, true, countList, level);
            for (Dict court : courts) {
                System.out.println(court);
                CourtEntity entity = new CourtEntity();
                entity.setId(court.getId());
                entity.setCode(court.getCode());
                entity.setParentId(court.getParentid());
                entity.setProvince(name);
                entity.setName(court.getName());
                try {
                    courtMapper.insert(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testCourt1() {
        List<Dict> countList = new CopyOnWriteArrayList<>();
        Integer level = 0;

        List<Dict> courts = getCourt(0, true, countList, level);
        for (Dict court : courts) {
            System.out.println(court);
            CourtEntity entity = new CourtEntity();
            entity.setId(court.getId());
            entity.setCode(court.getCode());
            entity.setParentId(court.getParentid());
            //   entity.setProvince(name);
            entity.setName(court.getName());
            try {
                courtMapper.insert(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public List<Dict> getCourt(Integer code, boolean searchParent, List<Dict> countList, Integer level) {
        level++;
        String url = "https://wenshu.court.gov.cn/website/parse/rest.q4w";
        String pageId = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> params = new HashMap<>();
        params.put("pageId", pageId);
        params.put("s8", "02");
        params.put("provinceCode", code);
        params.put("searchParent", searchParent);
        params.put("cfg", "com.lawyee.judge.dc.parse.dto.LoadDicDsoDTO@loadFy");
        params.put("__RequestVerificationToken", ParamsUtils.random(24));
        params.put("wh", 470);
        params.put("ww", 1680);
        HttpResponse response = null;
        try {
            TimeUnit.SECONDS.sleep(10);
            response = HttpRequest.post(url)
                    .form(params)
                    .timeout(-1)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("X-Real-IP", IpUtils.getIp())
                    .header("X-Forwarded-For", IpUtils.getIp())
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Host", "wenshu.court.gov.cn")
                    .header("Origin", "https://wenshu.court.gov.cn")
                    .header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?pageId=" + pageId + "&s8=02")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                    //  .header("sec-ch-ua", properties.getSecChUa())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "Windows")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute();
            System.out.println(response.body());
            Result result = JSON.parseObject(response.body(), Result.class);
            JSONObject object = JSON.parseObject(result.getResult());
            List<Dict> courts = JSON.parseArray(object.getJSONArray("fy").toJSONString(), Dict.class);
            if (courts.size() == 0) {
                return countList;
            }
            for (Dict court : courts) {
                System.out.println(court);
                CourtEntity entity = new CourtEntity();
                entity.setId(court.getId());
                entity.setCode(court.getCode());
                entity.setParentId(court.getParentid());
                //   entity.setProvince(name);
                entity.setName(court.getName());
                try {
                    courtMapper.insert(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            countList.addAll(courts);
            for (Dict court : courts) {
                getCourt(court.getId(), false, countList, level);
            }
            return countList;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Test
    public void testpc() throws Exception {
        List<DocumentEntity> entities = documentMapper.selectList(Wrappers.lambdaQuery());
        for (DocumentEntity entity : entities) {
            String htmlContent = entity.getHtmlContent();
            String docPath = "D:\\pc\\2020\\" + entity.getCaseNo() + "-" + entity.getName() + ".docx";
            File file = new File(docPath);
            if (file.exists()) {
                docPath = "D:\\pc\\2020\\" + entity.getCaseNo() + "-" + entity.getName() + "-" + RandomUtil.randomString(5) + ".docx";
            }
            htmlAsAltChunk2Docx(htmlContent, docPath);
        }
    }

    public void htmlAsAltChunk2Docx(String html, String docxPath) throws Exception {
        List<String> strings = Files.readAllLines(Paths.get(html));
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string)
                    .append("\n");
        }

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        //   wordMLPackage.setFontMapper(IFontHandler.getFontMapper());
        // Add the Html altChunk
        //  String html = sb.toString();
        mdp.addAltChunk(AltChunkType.Html, html.getBytes());

        // Round trip
        WordprocessingMLPackage pkgOut = mdp.convertAltChunks();

        pkgOut.save(new File(docxPath));
    }

    @Autowired
    private CauseMapper causeMapper;

    @Test
    private void test() {
        List<Dict> causes = new ArrayList<>();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:doc/*.txt");
            for (Resource resource : resources) {
                if ("cause.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    causes.addAll(JSON.parseArray(text, Dict.class));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Dict cause : causes) {
            CauseEntity entity = new CauseEntity();
            entity.setId(cause.getId().toString());
            entity.setPid(cause.getParent());
            entity.setName(cause.getText());
            causeMapper.insert(entity);
        }
    }
}
