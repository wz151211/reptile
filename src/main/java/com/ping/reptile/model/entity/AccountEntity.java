package com.ping.reptile.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("account")
public class AccountEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("account")
    private String account;
    @TableField("state")
    private String state;
}
