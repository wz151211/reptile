package com.ping.reptile.pkulaw.cases;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ping.reptile.model.entity.JudicialCasesEntity;
import com.ping.reptile.pkulaw.law.vo.PkulawEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JudicialCasesMapper extends BaseMapper<JudicialCasesEntity> {
}
