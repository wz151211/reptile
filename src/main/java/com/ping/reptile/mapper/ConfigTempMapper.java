package com.ping.reptile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.model.entity.ConfigTempEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigTempMapper extends BaseMapper<ConfigTempEntity> {

    default void updateRefereeDateById(Integer id, String date) {
        update(null, Wrappers.<ConfigTempEntity>lambdaUpdate().set(ConfigTempEntity::getRefereeDate, date).eq(ConfigTempEntity::getId, id));
    }

}
