package com.ping.reptile.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "pku_config")
public class PkuConfigEntity {


    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("agent")
    private String agent;

    @TableField("url")
    private String url;

    @TableField("ua")
    private String chUa;

    @TableField("platform")
    private String platform;

    @TableField("params")
    private String params;

    @TableField("session")
    private String session;

    @TableField("authorization")
    private String authorization;

    @TableField("cookie")
    private String cookie;

    @TableField("doc_date")
    private String docDate;

    @TableField("page_num")
    private Integer pageNum;

    @TableField("page_size")
    private Integer pageSize;
}
