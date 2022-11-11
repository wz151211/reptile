package com.ping.reptile.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "config")
public class ConfigEntity {


    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("agent")
    private String agent;

    @TableField("chua")
    private String chua;

    @TableField("token")
    private String token;

    @TableField("doc_date")
    private String docDate;

    @TableField("punish_date")
    private String punishDate;

    @TableField("pkulaw_date")
    private String pkulawDate;

    @TableField("cookie")
    private String cookie;

    @TableField("authorization")
    private String authorization;

    @TableField("page_num")
    private Integer pageNum;

    @TableField("page_size")
    private Integer pageSize;


    @TableField("enable")
    private Integer enable;
}
