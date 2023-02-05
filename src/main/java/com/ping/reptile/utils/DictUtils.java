package com.ping.reptile.utils;

import java.util.HashMap;
import java.util.Map;

public class DictUtils {

    private static Map<String, String> fullTextTypeMap = new HashMap<>();
    private static Map<String, String> caseTypeMap = new HashMap<>();
    private static Map<String, String> docTypeMap = new HashMap<>();
    private static Map<String, String> trialProceedingsMap = new HashMap<>();

    static {
        fullTextTypeMap.put("1", "全文");
        fullTextTypeMap.put("2", "首部");
        fullTextTypeMap.put("3", "当事人段");
        fullTextTypeMap.put("4", "诉讼记录");
        fullTextTypeMap.put("5", "事实");
        fullTextTypeMap.put("6", "理由");
        fullTextTypeMap.put("7", "判决结果");
        fullTextTypeMap.put("8", "尾部");
        fullTextTypeMap.put("255", "其他");


        caseTypeMap.put("管辖案件", "01");
        caseTypeMap.put("刑事案件", "02");
        caseTypeMap.put("民事案件", "03");
        caseTypeMap.put("行政案件", "04");
        caseTypeMap.put("国家赔偿与司法救助案件", "05");
        caseTypeMap.put("区际司法协助案件", "06");
        caseTypeMap.put("国际司法协助案件", "07");
        caseTypeMap.put("司法制裁案件", "08");
        caseTypeMap.put("非诉保全审查案件", "09");
        caseTypeMap.put("执行案件", "10");
        caseTypeMap.put("强制清算与破产案件", "11");
        caseTypeMap.put("其他案件", "99");

        docTypeMap.put("全部", "00");
        docTypeMap.put("判决书", "01");
        docTypeMap.put("裁定书", "02");
        docTypeMap.put("调解书", "03");
        docTypeMap.put("决定书", "04");
        docTypeMap.put("通知书", "05");
        docTypeMap.put("令", "09");
        docTypeMap.put("其他", "10");


        trialProceedingsMap.put("刑事一审", "0201");
        trialProceedingsMap.put("刑事二审", "0202");
        trialProceedingsMap.put("民事一审", "0301");
        trialProceedingsMap.put("民事二审", "0302");
        trialProceedingsMap.put("行政一审", "0401");
        trialProceedingsMap.put("行政二审", "0402");

    }

    public static String getFullTextType(String name) {
        return fullTextTypeMap.get(name);
    }

    public static String getCaseType(String name) {
        return caseTypeMap.get(name);
    }

    public static String getDocType(String name) {
        return docTypeMap.get(name);
    }

    public static String getTrialProceedings(String name) {
        return trialProceedingsMap.get(name);
    }
}
