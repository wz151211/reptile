package com.ping.reptile.utils;

import java.util.HashMap;
import java.util.Map;

public class DictUtils {

    private static Map<String, String> fullTextTypeMap = new HashMap<>();
    private static Map<String, String> caseTypeMap = new HashMap<>();
    private static Map<String, String> docTypeMap = new HashMap<>();
    private static Map<String, String> trialProceedingsMap = new HashMap<>();

    private static Map<String, String> causeMap = new HashMap<>();


    static {
        fullTextTypeMap.put("全文", "1");
        fullTextTypeMap.put("首部", "2");
        fullTextTypeMap.put("当事人段", "3");
        fullTextTypeMap.put("诉讼记录", "4");
        fullTextTypeMap.put("事实", "5");
        fullTextTypeMap.put("理由", "6");
        fullTextTypeMap.put("判决结果", "7");
        fullTextTypeMap.put("尾部", "8");
        fullTextTypeMap.put("其他", "255");


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

        causeMap.put("危害税收征管罪", "127");
        causeMap.put("税务行政管理（税务）", "xz1800");
        causeMap.put("财政行政管理（财政）", "xz2200");


        causeMap.put("强奸罪", "166");
        causeMap.put("强制猥亵、侮辱罪", "4218");
        causeMap.put("猥亵儿童罪", "169");

        causeMap.put("组织卖淫罪", "328");
        causeMap.put("强迫卖淫罪", "329");
        causeMap.put("引诱、容留、介绍卖淫罪", "331");
        causeMap.put("引诱幼女卖淫罪", "332");


        causeMap.put("盗掘古文化遗址、古墓葬罪", "283");
        causeMap.put("虚开增值税专用发票、用于骗取出口退税、抵扣税款发票罪", "132");
        causeMap.put("虚开发票罪", "4148");




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

    public static String getCause(String name) {
        return causeMap.get(name);
    }

    public static String getTrialProceedings(String name) {
        return trialProceedingsMap.get(name);
    }
}
