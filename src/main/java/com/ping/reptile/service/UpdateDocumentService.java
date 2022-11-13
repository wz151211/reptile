package com.ping.reptile.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.mapper.DocumentMapper;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.model.vo.Dict;
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
        List<DocumentEntity> entities = documentMapper.selectList(Wrappers.<DocumentEntity>lambdaQuery().select(DocumentEntity::getId, DocumentEntity::getJsonContent).isNull(DocumentEntity::getCause).last("limit 10000"));
        if (entities == null && entities.size() > 0) {
            return;
        }
        for (DocumentEntity entity : entities) {
            JSONObject jsonObject = JSON.parseObject(entity.getJsonContent());
            String trialProceedings = jsonObject.getString("s9");
            String docType = jsonObject.getString("s6");
            JSONArray causes = jsonObject.getJSONArray("s11");
            String cause = causes.stream().map(Object::toString).collect(joining(","));

            entity.setCause(cause);
            entity.setTrialProceedings(trialProceedings);
            entity.setDocType(docTypeMap.get(docType));
            entity.setJsonContent(null);
            entity.setCreateTime(new Date());
            documentMapper.updateById(entity);
        }
        update();
    }
}
