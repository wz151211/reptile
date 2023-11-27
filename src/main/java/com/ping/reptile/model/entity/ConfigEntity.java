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

    @TableField("params")
    private String params;

    @TableField("token")
    private String token;

    @TableField("doc_date")
    private String docDate;

    @TableField("page_num")
    private Integer pageNum;

    @TableField("page_size")
    private Integer pageSize;

    @TableField("category")
    private Integer category;

    @TableField("case_type")
    private String caseType;

    @TableField("chua")
    private String chua;
}
