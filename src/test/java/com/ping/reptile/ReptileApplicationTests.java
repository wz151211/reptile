package com.ping.reptile;

import com.ping.reptile.service.CpwsService;
import com.ping.reptile.mapper.AreaMapper;
import com.ping.reptile.model.entity.AreaEntity;
import com.ping.reptile.service.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class ReptileApplicationTests {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private PunishService punishService;

    @Autowired
    private CasePunishService casePunishService;

    @Autowired
    private PkulawService pkulawService;

    @Autowired
    private PkulawUpdateService updateService;


    @Autowired
    private UpdateDocumentService updateDocumentService;

    @Autowired
    private CpwsService cpwsService;

    @Test
    void contextLoads() {

        // documentService.parse();
    }


    @Test
    public void punish() {
        punishService.page(1, 15);
    }

    @Test
    public void casePunish() {
        casePunishService.page(1, 150);
    }

    @Test
    public void pkulawPunish() {
        pkulawService.page(0, 100);
    }

    @Test
    public void updatePunish() {
        updateService.list();
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
        } catch (InterruptedException e) {
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


}
