package com.ping.reptile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.model.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface AccountMapper extends BaseMapper<AccountEntity> {
    default AccountEntity getAccount(Integer category) {
        return selectOne(Wrappers.<AccountEntity>lambdaQuery()
                .eq(AccountEntity::getCategory, category)
                .and(i -> i.eq(AccountEntity::getState, 1).or().eq(AccountEntity::getState, 3))
                .last("limit 1")
                .orderByAsc(AccountEntity::getUpdateDate));
    }

    default void updateState(String account, Integer state) {
        update(null, Wrappers.<AccountEntity>lambdaUpdate()
                .set(AccountEntity::getState, state)
                .set(AccountEntity::getUpdateDate, new Date())
                .eq(AccountEntity::getAccount, account));
    }

    default void updateCategory(String account, Integer category) {
        update(null, Wrappers.<AccountEntity>lambdaUpdate()
                .set(AccountEntity::getCategory, category)
                .set(AccountEntity::getUpdateDate, new Date())
                .eq(AccountEntity::getAccount, account));
    }

    default List<AccountEntity> getAccountByCategory(Integer category) {
        return selectList(Wrappers.<AccountEntity>lambdaQuery()
                .eq(AccountEntity::getCategory, category)
                .and(i -> i.eq(AccountEntity::getState, 1).or().eq(AccountEntity::getState, 3))
                .orderByAsc(AccountEntity::getUpdateDate));

    }
}
