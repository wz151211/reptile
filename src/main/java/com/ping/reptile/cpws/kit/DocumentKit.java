package com.ping.reptile.cpws.kit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.model.vo.Result;

import java.util.Date;

import static java.util.stream.Collectors.joining;

public class DocumentKit {

    public static DocumentEntity toEntity(JSONObject jsonObject) {
        String id = jsonObject.getString("s5");
        String name = jsonObject.getString("s1");
        String caseNo = jsonObject.getString("s7");
        String courtName = jsonObject.getString("s2");
        String refereeDate = jsonObject.getString("s31");
        String caseType = jsonObject.getString("s8");
        String trialProceedings = jsonObject.getString("s9");
        String docType = jsonObject.getString("s6");
        JSONArray causes = jsonObject.getJSONArray("s11");
        String cause = null;
        if (causes != null) {
            cause = causes.stream().map(Object::toString).collect(joining(","));
        }
        JSONArray partys = jsonObject.getJSONArray("s17");
        String party = null;
        if (partys != null) {
            party = partys.stream().map(Object::toString).collect(joining(","));
        }
        JSONArray keywords = jsonObject.getJSONArray("s45");
        String keyword = null;
        if (keywords != null) {
            keyword = keywords.stream().map(Object::toString).collect(joining(","));
        }
        String courtConsidered = jsonObject.getString("s26");
        String judgmentResult = jsonObject.getString("s27");
        String htmlContent = jsonObject.getString("qwContent");
        jsonObject.remove("qwContent");
        String jsonContent = jsonObject.toJSONString();
        DocumentEntity entity = new DocumentEntity();
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
        //   entity.setDocType(docTypeMap.get(docType));
        entity.setJsonContent(jsonContent);
        entity.setHtmlContent(htmlContent);
        entity.setCreateTime(new Date());
        return entity;
    }
}
