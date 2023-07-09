package com.ping.reptile.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.mapper.AccountMapper;
import com.ping.reptile.model.entity.AccountEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AccountService {
    @Autowired
    private AccountMapper accountMapper;

    public AccountEntity getAccount(Integer category) {
        List<AccountEntity> entities = accountMapper.getAccountByCategory(category);
        AccountEntity temp = null;
        if (entities.size() < 5) {
            List<AccountEntity> accounts = accountMapper.getAccountByCategory(0);
            if (accounts.size() == 0) {
                log.info("没有可分配账号，请添加账号");
            } else {
                int count = entities.size();
                for (AccountEntity account : accounts) {
                    if (count >= 8) {
                        continue;
                    }
                    accountMapper.updateCategory(account.getAccount(), category);
                    count++;
                }
                temp = accounts.get(0);
            }
        }
        if (temp != null) {
            accountMapper.update(null, Wrappers.<AccountEntity>lambdaUpdate().set(AccountEntity::getUpdateDate, new Date()).eq(AccountEntity::getId, temp.getId()));
            return temp;
        }
        AccountEntity entity = accountMapper.getAccount(category);
        if (entity != null) {
            if (entity.getUpdateDate() != null && entity.getUpdateDate().plusHours(2).isBefore(LocalDateTime.now())) {
                return entity;
            } else {
                log.info("已经没有可用账号，进入休眠");
                try {
                    TimeUnit.HOURS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                entity = accountMapper.getAccount(category);
                if (entity.getUpdateDate() != null && entity.getUpdateDate().plusHours(2).isBefore(LocalDateTime.now())) {
                    return entity;
                }
            }
        }
        log.info("已经没有可用账号");
        return null;
    }

    public void updateState(String account, Integer state) {
        accountMapper.updateState(account, state);
    }
}
