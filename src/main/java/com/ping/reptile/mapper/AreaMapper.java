package com.ping.reptile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.model.entity.AreaEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AreaMapper extends BaseMapper<AreaEntity> {

    default AreaEntity getByProvinceAndCityAndCounty(String province, String city, String county) {
        return selectOne(Wrappers.<AreaEntity>lambdaQuery()
                .eq(StringUtils.isNotEmpty(province), AreaEntity::getProvince, province)
                .eq(StringUtils.isNotEmpty(city), AreaEntity::getCity, city)
                .eq(StringUtils.isNotEmpty(county), AreaEntity::getCounty, county));

    }
}
