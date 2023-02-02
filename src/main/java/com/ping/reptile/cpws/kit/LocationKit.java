package com.ping.reptile.cpws.kit;

import com.ping.reptile.model.entity.DocumentEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LocationKit {
    private Map<String, String> provinces = new HashMap<>();

    {
        provinces.put("京", "北京市");
        provinces.put("津", "天津市");
        provinces.put("冀", "河北省");
        provinces.put("晋", "山西省");
        provinces.put("内", "内蒙古自治区");
        provinces.put("内蒙古", "内蒙古自治区");
        provinces.put("辽", "辽宁省");
        provinces.put("吉", "吉林省");
        provinces.put("黑", "黑龙江省");
        provinces.put("沪", "上海市");
        provinces.put("苏", "江苏省");
        provinces.put("浙", "浙江省");
        provinces.put("皖", "安徽省");
        provinces.put("闽", "福建省");
        provinces.put("赣", "江西省");
        provinces.put("鲁", "山东省");
        provinces.put("豫", "河南省");
        provinces.put("鄂", "湖北省");
        provinces.put("湘", "湖南省");
        provinces.put("粤", "广东省");
        provinces.put("桂", "广西壮族自治区");
        provinces.put("琼", "海南省");
        provinces.put("川", "四川省");
        provinces.put("蜀", "四川省");
        provinces.put("贵", "贵州省");
        provinces.put("黔", "贵州省");
        provinces.put("滇", "云南省");
        provinces.put("云", "云南省");
        provinces.put("渝", "重庆市");
        provinces.put("藏", "西藏自治区");
        provinces.put("秦", "陕西省");
        provinces.put("陕", "陕西省");
        provinces.put("甘", "甘肃省");
        provinces.put("陇", "甘肃省");
        provinces.put("青", "青海省");
        provinces.put("宁", "宁夏回族自治区");
        provinces.put("新", "新疆维吾尔自治区");
    }

    private String convert(String name) {
        for (String s : provinces.keySet()) {
            if (StringUtils.isNotEmpty(name) && name.contains(s)) {
                return provinces.get(s);
            }
        }
        return null;
    }

    public static void location(DocumentEntity entity) {

    }
}
