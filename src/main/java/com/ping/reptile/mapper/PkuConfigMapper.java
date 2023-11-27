package com.ping.reptile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.model.entity.PkuConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

@Mapper
public interface PkuConfigMapper extends BaseMapper<PkuConfigEntity> {

    default void updateDocDateById(LocalDate docDate, Integer id) {
       update(null, Wrappers.<PkuConfigEntity>lambdaUpdate().set(PkuConfigEntity::getDocDate, docDate).eq(PkuConfigEntity::getId, id));
    }
}
