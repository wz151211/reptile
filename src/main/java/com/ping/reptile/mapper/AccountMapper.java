package com.ping.reptile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.model.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<AccountEntity> {
    default String getAccount() {
        AccountEntity entity = selectOne(Wrappers.<AccountEntity>lambdaQuery().eq(AccountEntity::getState, 1).or().eq(AccountEntity::getState, 3).last("limit 1").orderByAsc(AccountEntity::getState));
        if (entity != null) {
            return entity.getAccount();
        }
        return null;
    }

    default void updateState(String account, Integer state) {
        update(null, Wrappers.<AccountEntity>lambdaUpdate().set(AccountEntity::getState, state).eq(AccountEntity::getAccount, account));
    }
}
