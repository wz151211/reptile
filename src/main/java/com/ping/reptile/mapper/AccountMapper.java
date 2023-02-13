package com.ping.reptile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.model.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Mapper
public interface AccountMapper extends BaseMapper<AccountEntity> {
    default AccountEntity getAccount() {
        AccountEntity entity = selectOne(Wrappers.<AccountEntity>lambdaQuery().eq(AccountEntity::getState, 1).or().eq(AccountEntity::getState, 3).last("limit 1").orderByAsc(AccountEntity::getUpdateDate));
        if (entity != null) {
            if (entity.getUpdateDate() != null && entity.getUpdateDate().plusHours(2).isBefore(LocalDateTime.now())) {
                return entity;
            } else {
                System.out.println("已经没有可用账号，进入休眠");
                try {
                    TimeUnit.HOURS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                entity = selectOne(Wrappers.<AccountEntity>lambdaQuery().eq(AccountEntity::getState, 1).or().eq(AccountEntity::getState, 3).last("limit 1").orderByAsc(AccountEntity::getUpdateDate));
                if (entity.getUpdateDate() != null && entity.getUpdateDate().plusHours(2).isBefore(LocalDateTime.now())) {
                    return entity;
                }
            }

        }
        System.out.println("已经没有可用账号");
        return null;
    }

    default void updateState(String account, Integer state) {
        update(null, Wrappers.<AccountEntity>lambdaUpdate().set(AccountEntity::getState, state).set(AccountEntity::getUpdateDate, new Date()).eq(AccountEntity::getAccount, account));
    }

    default List<AccountEntity> getAccountList() {
        return selectList(Wrappers.<AccountEntity>lambdaQuery().eq(AccountEntity::getState, 1).or().eq(AccountEntity::getState, 3).eq(AccountEntity::getCategory, 0).orderByAsc(AccountEntity::getUpdateDate));
    }
}
