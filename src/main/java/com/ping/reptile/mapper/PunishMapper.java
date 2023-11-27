package com.ping.reptile.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.model.entity.PunishEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: W.Z
 * @date: 2022/8/26 17:21
 * @desc:
 */
@Mapper
public interface PunishMapper extends BaseMapper<PunishEntity> {

    default List<PunishEntity> getCaseNoList() {
        QueryWrapper<PunishEntity> query = Wrappers.query();
        query.select(" punish_unit as punishUnit , max(case_no) as caseNo  ");
        query.lambda().groupBy(PunishEntity::getPunishUnit);
        return selectList(query);
    }
}
