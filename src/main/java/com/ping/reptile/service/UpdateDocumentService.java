package com.ping.reptile.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.mapper.CourtMapper;
import com.ping.reptile.mapper.Document1Mapper;
import com.ping.reptile.mapper.DocumentMapper;
import com.ping.reptile.model.entity.CourtEntity;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.model.vo.Dict;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.stream.Collectors.joining;

@Service
@Slf4j
public class UpdateDocumentService {

    @Autowired
    private DocumentMapper documentMapper;
    private List<Dict> docTypes = new ArrayList<>();
    private Map<String, String> docTypeMap = new HashMap<>();

    @Autowired
    private CourtMapper courtMapper;
    @Autowired
    private Document1Mapper document1Mapper;

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
        List<DocumentEntity> entities = documentMapper.selectList(Wrappers.<DocumentEntity>lambdaQuery().select(DocumentEntity::getId, DocumentEntity::getJsonContent).isNull(DocumentEntity::getCourtConsidered).last("limit 1000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        for (DocumentEntity entity : entities) {
            JSONObject jsonObject = JSON.parseObject(entity.getJsonContent());
            if (jsonObject == null) {
                continue;
            }

            JSONArray partys = jsonObject.getJSONArray("s17");
            String party = partys.stream().map(Object::toString).collect(joining(","));
            JSONArray keywords = jsonObject.getJSONArray("s45");
            String keyword = keywords.stream().map(Object::toString).collect(joining(","));
            String courtConsidered = jsonObject.getString("s26");
            String judgmentResult = jsonObject.getString("s27");
            String id = entity.getId();
            entity = new DocumentEntity();
            entity.setId(id);
            entity.setParty(party);
            entity.setJudgmentResult(judgmentResult);
            entity.setKeyword(keyword);
            entity.setCourtConsidered(courtConsidered);
            entity.setCreateTime(new Date());
            documentMapper.updateById(entity);
        }
    }


    public void save() {
        List<CourtEntity> entities = courtMapper.selectList(Wrappers.<CourtEntity>lambdaQuery().eq(CourtEntity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        DocumentEntity documentEntity = null;
        for (CourtEntity entity : entities) {
            documentEntity = new DocumentEntity();
            documentEntity.setId(entity.getDocId());
            /*Long count = documentMapper.selectCount(Wrappers.<DocumentEntity>lambdaQuery().eq(DocumentEntity::getId, entity.getId()));
            if (count > 0) {
                continue;
            }*/
            log.info("案件id={}", entity.getId());
            if (entity.getCourtInfo().contains("DocInfoVo")) {
                JSONObject object = JSON.parseObject(entity.getCourtInfo());
                String htmlCount = object.getJSONObject("DocInfoVo").getString("qwContent");
                documentEntity.setHtmlContent(htmlCount);
            } else {
                JSONObject jsonObject = JSON.parseObject(entity.getCourtInfo());
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
            }
            try {
                documentMapper.insert(documentEntity);
                courtMapper.update(null, Wrappers.<CourtEntity>lambdaUpdate().set(CourtEntity::getFlag, 1).eq(CourtEntity::getId, entity.getId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
