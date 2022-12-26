package com.ping.reptile.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.mapper.DocumentMapper;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.model.vo.Dict;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.joining;

@Service
@Slf4j
public class UpdateDocumentService {

    @Autowired
    private DocumentMapper documentMapper;
    private List<Dict> docTypes = new ArrayList<>();
    private Map<String, String> docTypeMap = new HashMap<>();

    {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:doc/*.txt");
            for (Resource resource : resources) {
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

    public void update() {
        List<DocumentEntity> entities = documentMapper.selectList(Wrappers.<DocumentEntity>lambdaQuery().select(DocumentEntity::getId, DocumentEntity::getHtmlContent).isNull(DocumentEntity::getCaseNo).last("limit 1000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(e -> {
            if (StringUtils.isNotEmpty(e.getHtmlContent())) {
                String html = e.getHtmlContent();
                DocumentEntity entity = new DocumentEntity();
                if (StringUtils.isNotEmpty(html)) {
                    Document parse = Jsoup.parse(html);
                    Elements divs = parse.getElementsByTag("div");
                    if (divs != null && divs.size() > 0) {
                        for (int i = 0; i < divs.size(); i++) {
                            if (i >= 4) {
                                continue;
                            }
                            Element element = divs.get(i);
                            String text = element.ownText().trim();
                            if (StringUtils.isNotEmpty(text)) {
                                if (text.contains("法院")) {
                                    entity.setCourtName(text);
                                }
                                if (text.contains("书")) {
                                    text = text.replace(" ", "");
                                    entity.setCaseType(text.substring(0, 2) + "案件");
                                    entity.setDocType(text.substring(2));
                                }
                                if (text.contains("号")) {
                                    entity.setCaseNo(text);
                                }
                            }
                        }
                    }
                }
                entity.setCreateTime(new Date());
                entity.setHtmlContent(html);
 /*           JSONArray partys = jsonObject.getJSONArray("s17");
            String party = partys.stream().map(Object::toString).collect(joining(","));
            JSONArray keywords = jsonObject.getJSONArray("s45");
            String keyword = keywords.stream().map(Object::toString).collect(joining(","));
            String courtConsidered = jsonObject.getString("s26");
            String judgmentResult = jsonObject.getString("s27");*/
                entity.setId(e.getId());
//            entity.setParty(party);
//            entity.setJudgmentResult(judgmentResult);
//            entity.setKeyword(keyword);
//            entity.setCourtConsidered(courtConsidered);
//            entity.setCreateTime(new Date());
                try {
                    documentMapper.updateById(entity);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.info("entity={}", entity);
                }
            }
        });
    }


    public void save() {
        LocalDate localDate = LocalDate.of(2022, 11, 25);
        DateTime parse = DateUtil.parse("2022-11-20 00:00:00", "yyyy-MM-dd HH:mm:ss");
        List<DocumentEntity> entities = documentMapper.selectList(Wrappers.<DocumentEntity>lambdaQuery().le(DocumentEntity::getCreateTime, parse.toJdkDate()).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        DocumentEntity documentEntity = null;
        for (DocumentEntity entity : entities) {
            log.info("案件id={}", entity.getId());
            JSONObject jsonObject = JSON.parseObject(entity.getJsonContent());
            String id = jsonObject.getString("s5");
            String name = jsonObject.getString("s1");
            String caseNo = jsonObject.getString("s7");
            String courtName = jsonObject.getString("s2");
            String refereeDate = jsonObject.getString("s31");
            String caseType = jsonObject.getString("s8");
            String trialProceedings = jsonObject.getString("s9");
            String docType = jsonObject.getString("s6");
            JSONArray causes = jsonObject.getJSONArray("s11");
            String cause = causes.stream().map(Object::toString).collect(joining(","));
            JSONArray partys = jsonObject.getJSONArray("s17");
            String party = partys.stream().map(Object::toString).collect(joining(","));
            JSONArray keywords = jsonObject.getJSONArray("s45");
            String keyword = keywords.stream().map(Object::toString).collect(joining(","));
            String courtConsidered = jsonObject.getString("s26");
            String judgmentResult = jsonObject.getString("s27");
            String htmlContent = jsonObject.getString("qwContent");
            jsonObject.remove("qwContent");
            String jsonContent = jsonObject.toJSONString();
            log.info("案件名称={}", name);
            documentEntity.setId(id);
            documentEntity.setName(name);
            documentEntity.setCaseNo(caseNo);
            documentEntity.setCourtName(courtName);
            documentEntity.setRefereeDate(refereeDate);
            documentEntity.setCaseType(caseType);
            documentEntity.setParty(party);
            documentEntity.setCause(cause);
            documentEntity.setJudgmentResult(judgmentResult);
            documentEntity.setKeyword(keyword);
            documentEntity.setCourtConsidered(courtConsidered);
            documentEntity.setTrialProceedings(trialProceedings);
            documentEntity.setDocType(docTypeMap.get(docType));
            documentEntity.setJsonContent(jsonContent);
            documentEntity.setHtmlContent(htmlContent);
            documentEntity.setCreateTime(new Date());

            try {
                documentMapper.insert(documentEntity);
                //  documentMapper.update(null, Wrappers.<CourtEntity>lambdaUpdate().set(CourtEntity::getFlag, 1).eq(CourtEntity::getId, entity.getId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
