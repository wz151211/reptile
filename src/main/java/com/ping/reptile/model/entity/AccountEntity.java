package com.ping.reptile.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("account")
public class AccountEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("account")
    private String account;
    @TableField("state")
    private Integer state;
    @TableField("update_date")
    private LocalDateTime updateDate;
    @TableField("category")
    private Integer category;
}
