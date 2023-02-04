package com.ping.reptile.service;

import com.ping.reptile.mapper.AreaMapper;
import com.ping.reptile.model.entity.AreaEntity;
import com.ping.reptile.model.entity.DocumentEntity;
import com.ping.reptile.utils.AreaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class AreaService {
    @Autowired
    private AreaMapper areaMapper;
    private final Set<String> autonomous = new HashSet<>();

    private final Set<String> directlyCity = new HashSet<>();
    private final Set<String> level2 = new HashSet<>();
    private final Set<String> level3 = new HashSet<>();

    {
        autonomous.add("内蒙古自治区");
        autonomous.add("广西壮族自治区");
        autonomous.add("西藏自治区");
        autonomous.add("宁夏回族自治区");
        autonomous.add("新疆维吾尔自治区");
        autonomous.add("新疆生产建设兵团");

        directlyCity.add("北京市");
        directlyCity.add("天津市");
        directlyCity.add("重庆市");
        directlyCity.add("上海市");

        level2.add("市");
        level2.add("自治州");
        level2.add("盟");

        level3.add("区");
        level3.add("县");
        level3.add("旗");
    }

    public void convert(DocumentEntity entity) {
        String courtName = entity.getCourtName();
        entity.setProvince(AreaUtils.convert(entity.getCaseNo()));
        if (courtName.contains("省")) {
            int index = courtName.indexOf("省");
            String province = courtName.substring(0, index + 1);
            if (StringUtils.isEmpty(entity.getProvince())) {
                entity.setProvince(province);
            }
            courtName = courtName.substring(index + 1);

        }
        for (String s : autonomous) {
            if (courtName.contains(s)) {
                courtName = courtName.substring(s.length());
                entity.setProvince(s);
            }
        }
        for (String s : directlyCity) {
            if (courtName.contains(s)) {
                courtName = courtName.substring(s.length());
                entity.setProvince(s);
            }
        }

        for (String key : level2) {
            if (courtName.contains(key)) {
                int index = courtName.indexOf(key);
                String city = courtName.substring(0, index + key.length());
                entity.setCity(city);
                courtName = courtName.substring(index + key.length());
            }
        }

        for (String key : level3) {
            if (courtName.contains(key)) {
                int index = courtName.indexOf(key);
                String county = courtName.substring(0, index + 1);
                entity.setCounty(county);
                AreaEntity areaEntity = areaMapper.getByProvinceAndCityAndCounty(entity.getProvince(), entity.getCity(), entity.getCounty());
                if (areaEntity != null) {
                    entity.setProvince(areaEntity.getProvince());
                    entity.setCity(areaEntity.getCity());
                    entity.setCounty(areaEntity.getCounty());
                }
                break;
            }
        }
    }
}
