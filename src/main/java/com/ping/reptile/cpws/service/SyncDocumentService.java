package com.ping.reptile.cpws.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.cpws.entity.*;
import com.ping.reptile.cpws.mapper.*;
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
import java.util.*;

import static java.util.stream.Collectors.joining;

@Service
@Slf4j
public class SyncDocumentService {
    @Autowired
    private CpwsMapper cpwsMapper;
    @Autowired
    private Cpws01Mapper cpws01Mapper;
    @Autowired
    private Cpws02Mapper cpws02Mapper;
    @Autowired
    private Cpws03Mapper cpws03Mapper;
    @Autowired
    private Cpws04Mapper cpws04Mapper;
    @Autowired
    private Cpws05Mapper cpws05Mapper;
    @Autowired
    private Cpws06Mapper cpws06Mapper;
    @Autowired
    private Cpws07Mapper cpws07Mapper;
    @Autowired
    private Cpws08Mapper cpws08Mapper;
    @Autowired
    private Cpws09Mapper cpws09Mapper;
    @Autowired
    private Cpws10Mapper cpws10Mapper;
    @Autowired
    private Cpws15Mapper cpws15Mapper;
    @Autowired
    private Cpws19Mapper cpws19Mapper;
    @Autowired
    private Cpws20Mapper cpws20Mapper;
    @Autowired
    private Cpws68Mapper cpws68Mapper;
    @Autowired
    private Cpws87Mapper cpws87Mapper;

    @Autowired
    private DocumentMapper documentMapper;

    private int count = 200;

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

    public void sync() {
        List<CpwsEntity> entities = cpwsMapper.selectList(Wrappers.<CpwsEntity>lambdaQuery().eq(CpwsEntity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 1).eq(CpwsEntity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 1).eq(CpwsEntity::getId, entity.getId()));
                } else {
                    cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 4).eq(CpwsEntity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync1() {
        List<Cpws01Entity> entities = cpws01Mapper.selectList(Wrappers.<Cpws01Entity>lambdaQuery().eq(Cpws01Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws01Mapper.update(null, Wrappers.<Cpws01Entity>lambdaUpdate().set(Cpws01Entity::getFlag, 1).eq(Cpws01Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws01Mapper.update(null, Wrappers.<Cpws01Entity>lambdaUpdate().set(Cpws01Entity::getFlag, 1).eq(Cpws01Entity::getId, entity.getId()));
                } else {
                    cpws01Mapper.update(null, Wrappers.<Cpws01Entity>lambdaUpdate().set(Cpws01Entity::getFlag, 4).eq(Cpws01Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync2() {
        List<Cpws02Entity> entities = cpws02Mapper.selectList(Wrappers.<Cpws02Entity>lambdaQuery().eq(Cpws02Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws02Mapper.update(null, Wrappers.<Cpws02Entity>lambdaUpdate().set(Cpws02Entity::getFlag, 1).eq(Cpws02Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws02Mapper.update(null, Wrappers.<Cpws02Entity>lambdaUpdate().set(Cpws02Entity::getFlag, 1).eq(Cpws02Entity::getId, entity.getId()));
                } else {
                    cpws02Mapper.update(null, Wrappers.<Cpws02Entity>lambdaUpdate().set(Cpws02Entity::getFlag, 4).eq(Cpws02Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync3() {
        List<Cpws03Entity> entities = cpws03Mapper.selectList(Wrappers.<Cpws03Entity>lambdaQuery().eq(Cpws03Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws03Mapper.update(null, Wrappers.<Cpws03Entity>lambdaUpdate().set(Cpws03Entity::getFlag, 1).eq(Cpws03Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws03Mapper.update(null, Wrappers.<Cpws03Entity>lambdaUpdate().set(Cpws03Entity::getFlag, 1).eq(Cpws03Entity::getId, entity.getId()));
                } else {
                    cpws03Mapper.update(null, Wrappers.<Cpws03Entity>lambdaUpdate().set(Cpws03Entity::getFlag, 4).eq(Cpws03Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync4() {
        List<Cpws04Entity> entities = cpws04Mapper.selectList(Wrappers.<Cpws04Entity>lambdaQuery().eq(Cpws04Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws04Mapper.update(null, Wrappers.<Cpws04Entity>lambdaUpdate().set(Cpws04Entity::getFlag, 1).eq(Cpws04Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws04Mapper.update(null, Wrappers.<Cpws04Entity>lambdaUpdate().set(Cpws04Entity::getFlag, 1).eq(Cpws04Entity::getId, entity.getId()));
                } else {
                    cpws04Mapper.update(null, Wrappers.<Cpws04Entity>lambdaUpdate().set(Cpws04Entity::getFlag, 4).eq(Cpws04Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync5() {
        List<Cpws05Entity> entities = cpws05Mapper.selectList(Wrappers.<Cpws05Entity>lambdaQuery().eq(Cpws05Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws05Mapper.update(null, Wrappers.<Cpws05Entity>lambdaUpdate().set(Cpws05Entity::getFlag, 1).eq(Cpws05Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws05Mapper.update(null, Wrappers.<Cpws05Entity>lambdaUpdate().set(Cpws05Entity::getFlag, 1).eq(Cpws05Entity::getId, entity.getId()));
                } else {
                    cpws05Mapper.update(null, Wrappers.<Cpws05Entity>lambdaUpdate().set(Cpws05Entity::getFlag, 4).eq(Cpws05Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync6() {
        List<Cpws06Entity> entities = cpws06Mapper.selectList(Wrappers.<Cpws06Entity>lambdaQuery().eq(Cpws06Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws06Mapper.update(null, Wrappers.<Cpws06Entity>lambdaUpdate().set(Cpws06Entity::getFlag, 1).eq(Cpws06Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws06Mapper.update(null, Wrappers.<Cpws06Entity>lambdaUpdate().set(Cpws06Entity::getFlag, 1).eq(Cpws06Entity::getId, entity.getId()));
                } else {
                    cpws06Mapper.update(null, Wrappers.<Cpws06Entity>lambdaUpdate().set(Cpws06Entity::getFlag, 4).eq(Cpws06Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync7() {
        List<Cpws07Entity> entities = cpws07Mapper.selectList(Wrappers.<Cpws07Entity>lambdaQuery().eq(Cpws07Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws07Mapper.update(null, Wrappers.<Cpws07Entity>lambdaUpdate().set(Cpws07Entity::getFlag, 1).eq(Cpws07Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws07Mapper.update(null, Wrappers.<Cpws07Entity>lambdaUpdate().set(Cpws07Entity::getFlag, 1).eq(Cpws07Entity::getId, entity.getId()));
                } else {
                    cpws07Mapper.update(null, Wrappers.<Cpws07Entity>lambdaUpdate().set(Cpws07Entity::getFlag, 4).eq(Cpws07Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync8() {
        List<Cpws08Entity> entities = cpws08Mapper.selectList(Wrappers.<Cpws08Entity>lambdaQuery().eq(Cpws08Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws08Mapper.update(null, Wrappers.<Cpws08Entity>lambdaUpdate().set(Cpws08Entity::getFlag, 1).eq(Cpws08Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws08Mapper.update(null, Wrappers.<Cpws08Entity>lambdaUpdate().set(Cpws08Entity::getFlag, 1).eq(Cpws08Entity::getId, entity.getId()));
                } else {
                    cpws08Mapper.update(null, Wrappers.<Cpws08Entity>lambdaUpdate().set(Cpws08Entity::getFlag, 4).eq(Cpws08Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync9() {
        List<Cpws09Entity> entities = cpws09Mapper.selectList(Wrappers.<Cpws09Entity>lambdaQuery().eq(Cpws09Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws09Mapper.update(null, Wrappers.<Cpws09Entity>lambdaUpdate().set(Cpws09Entity::getFlag, 1).eq(Cpws09Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws09Mapper.update(null, Wrappers.<Cpws09Entity>lambdaUpdate().set(Cpws09Entity::getFlag, 1).eq(Cpws09Entity::getId, entity.getId()));
                } else {
                    cpws09Mapper.update(null, Wrappers.<Cpws09Entity>lambdaUpdate().set(Cpws09Entity::getFlag, 4).eq(Cpws09Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync10() {
        List<Cpws10Entity> entities = cpws10Mapper.selectList(Wrappers.<Cpws10Entity>lambdaQuery().eq(Cpws10Entity::getState, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws10Mapper.update(null, Wrappers.<Cpws10Entity>lambdaUpdate().set(Cpws10Entity::getState, 1).eq(Cpws10Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws10Mapper.update(null, Wrappers.<Cpws10Entity>lambdaUpdate().set(Cpws10Entity::getState, 1).eq(Cpws10Entity::getId, entity.getId()));
                } else {
                    cpws10Mapper.update(null, Wrappers.<Cpws10Entity>lambdaUpdate().set(Cpws10Entity::getState, 4).eq(Cpws10Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync15() {
        List<Cpws15Entity> entities = cpws15Mapper.selectList(Wrappers.<Cpws15Entity>lambdaQuery().eq(Cpws15Entity::getFlag, 0).like(Cpws15Entity::getCourtInfo, "盗窃").last("limit " + count));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws15Mapper.update(null, Wrappers.<Cpws15Entity>lambdaUpdate().set(Cpws15Entity::getFlag, 1).eq(Cpws15Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws15Mapper.update(null, Wrappers.<Cpws15Entity>lambdaUpdate().set(Cpws15Entity::getFlag, 1).eq(Cpws15Entity::getId, entity.getId()));
                } else {
                    cpws15Mapper.update(null, Wrappers.<Cpws15Entity>lambdaUpdate().set(Cpws15Entity::getFlag, 4).eq(Cpws15Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync19() {
        List<Cpws19Entity> entities = cpws19Mapper.selectList(Wrappers.<Cpws19Entity>lambdaQuery().eq(Cpws19Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws19Mapper.update(null, Wrappers.<Cpws19Entity>lambdaUpdate().set(Cpws19Entity::getFlag, 1).eq(Cpws19Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws19Mapper.update(null, Wrappers.<Cpws19Entity>lambdaUpdate().set(Cpws19Entity::getFlag, 1).eq(Cpws19Entity::getId, entity.getId()));
                } else {
                    cpws19Mapper.update(null, Wrappers.<Cpws19Entity>lambdaUpdate().set(Cpws19Entity::getFlag, 4).eq(Cpws19Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync20() {
        List<Cpws20Entity> entities = cpws20Mapper.selectList(Wrappers.<Cpws20Entity>lambdaQuery().eq(Cpws20Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws20Mapper.update(null, Wrappers.<Cpws20Entity>lambdaUpdate().set(Cpws20Entity::getFlag, 1).eq(Cpws20Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws20Mapper.update(null, Wrappers.<Cpws20Entity>lambdaUpdate().set(Cpws20Entity::getFlag, 1).eq(Cpws20Entity::getId, entity.getId()));
                } else {
                    cpws20Mapper.update(null, Wrappers.<Cpws20Entity>lambdaUpdate().set(Cpws20Entity::getFlag, 4).eq(Cpws20Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync68() {
        List<Cpws68Entity> entities = cpws68Mapper.selectList(Wrappers.<Cpws68Entity>lambdaQuery().eq(Cpws68Entity::getState, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws68Mapper.update(null, Wrappers.<Cpws68Entity>lambdaUpdate().set(Cpws68Entity::getState, 1).eq(Cpws68Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws68Mapper.update(null, Wrappers.<Cpws68Entity>lambdaUpdate().set(Cpws68Entity::getState, 1).eq(Cpws68Entity::getId, entity.getId()));
                } else {
                    cpws68Mapper.update(null, Wrappers.<Cpws68Entity>lambdaUpdate().set(Cpws68Entity::getState, 4).eq(Cpws68Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public void sync87() {
        List<Cpws87Entity> entities = cpws87Mapper.selectList(Wrappers.<Cpws87Entity>lambdaQuery().eq(Cpws87Entity::getFlag, 0).last("limit 10000"));
        if (entities == null || entities.size() == 0) {
            return;
        }
        entities.parallelStream().forEach(entity -> {
            DocumentEntity documentEntity = toDocumentEntity(entity);
            try {
                documentMapper.insert(documentEntity);
                cpws87Mapper.update(null, Wrappers.<Cpws87Entity>lambdaUpdate().set(Cpws87Entity::getFlag, 1).eq(Cpws87Entity::getId, entity.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    cpws87Mapper.update(null, Wrappers.<Cpws87Entity>lambdaUpdate().set(Cpws87Entity::getFlag, 1).eq(Cpws87Entity::getId, entity.getId()));
                } else {
                    cpws87Mapper.update(null, Wrappers.<Cpws87Entity>lambdaUpdate().set(Cpws87Entity::getFlag, 4).eq(Cpws87Entity::getId, entity.getId()));

                }
                e.printStackTrace();
            }
        });
    }

    public DocumentEntity toDocumentEntity(BaseEntity e) {

        DocumentEntity entity = new DocumentEntity();
        entity.setId(e.getDocId());
        log.info("案件id={}", e.getId());
        JSONObject object = null;
        object = JSON.parseObject(e.getCourtInfo());
        if (StringUtils.isNotEmpty(e.getCourtInfo())) {
            if (e.getCourtInfo().contains("DocInfoVo")) {
                String html = object.getJSONObject("DocInfoVo").getString("qwContent");
                if (StringUtils.isNotEmpty(html)) {
                    Document parse = Jsoup.parse(html);
                    Elements divs = parse.getElementsByTag("div");
                    if (divs != null && divs.size() > 0) {
                        for (int i = 0; i < divs.size(); i++) {
                            if (i >= 5) {
                                continue;
                            }
                            Element element = divs.get(i);
                            String text = element.ownText().trim();
                            if (StringUtils.isNotEmpty(text)) {
                                if (text.contains("法院")) {
                                    entity.setCourtName(text);
                                }
                                if (text.contains("书")) {
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
            } else if (e.getCourtInfo().contains("Title")) {
                String pubDate = object.getString("PubDate");
                String title = object.getString("Title");
                String html = object.getString("Html");
                entity.setName(title);
                entity.setRefereeDate(pubDate);
                entity.setHtmlContent(html);
                entity.setCreateTime(new Date());
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
                                    entity.setCaseType(text.substring(0, 3) + "案件");
                                    entity.setDocType(text.substring(3));
                                }
                                if (text.contains("号")) {
                                    entity.setCaseNo(text);
                                }
                            }
                        }
                    }
                }
            } else if (e.getCourtInfo().contains("qwContent")) {
                String id = object.getString("s5");
                if (StringUtils.isNotEmpty(id)) {
                    String name = object.getString("s1");
                    String caseNo = object.getString("s7");
                    String courtName = object.getString("s2");
                    String refereeDate = object.getString("s31");
                    String caseType = object.getString("s8");
                    String trialProceedings = object.getString("s9");
                    String docType = object.getString("s6");
                    JSONArray causes = object.getJSONArray("s11");
                    String cause = null;
                    if (causes != null) {
                        cause = causes.stream().map(Object::toString).collect(joining(","));
                    }
                    JSONArray partys = object.getJSONArray("s17");
                    String party = null;
                    if (partys != null) {
                        party = partys.stream().map(Object::toString).collect(joining(","));
                    }
                    JSONArray keywords = object.getJSONArray("s45");
                    String keyword = null;
                    if (keywords != null) {
                        keyword = keywords.stream().map(Object::toString).collect(joining(","));
                    }
                    String courtConsidered = object.getString("s26");
                    String judgmentResult = object.getString("s27");
                    String htmlContent = object.getString("qwContent");
                    object.remove("qwContent");
                    String jsonContent = object.toJSONString();
                    log.info("案件名称={}", name);
                    entity.setId(id);
                    entity.setName(name);
                    entity.setCaseNo(caseNo);
                    entity.setCourtName(courtName);
                    entity.setRefereeDate(refereeDate);
                    entity.setCaseType(caseType);
                    entity.setParty(party);
                    entity.setCause(cause);
                    entity.setJudgmentResult(judgmentResult);
                    entity.setKeyword(keyword);
                    entity.setCourtConsidered(courtConsidered);
                    entity.setTrialProceedings(trialProceedings);
                    entity.setDocType(docTypeMap.get(docType));
                    entity.setJsonContent(jsonContent);
                    entity.setHtmlContent(htmlContent);
                    entity.setCreateTime(new Date());
                } else {
                    log.info("案件详情:{}", object);
                }

            }
            log.info("案件名称={}", entity.getName());
            documentMapper.insert(entity);
        }
        return entity;
    }
}
