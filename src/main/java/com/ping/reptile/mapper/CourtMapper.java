package com.ping.reptile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.model.entity.CourtEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourtMapper extends BaseMapper<CourtEntity> {

    default List<CourtEntity> findAll() {
        return selectList(Wrappers.lambdaQuery());
    }

    default CourtEntity findByName(String name) {
        return selectOne((Wrappers.<CourtEntity>lambdaQuery().eq(CourtEntity::getName, name)));
    }

    default CourtEntity getCourt() {
        return selectOne(Wrappers.<CourtEntity>lambdaQuery().eq(CourtEntity::getComplete, 0).last("limit 1").orderByAsc(CourtEntity::getId));
    }

    default void updateStateByName(String name, Integer complete) {
        update(null, Wrappers.<CourtEntity>lambdaUpdate().set(CourtEntity::getComplete, complete).eq(CourtEntity::getName, name));
    }
}
