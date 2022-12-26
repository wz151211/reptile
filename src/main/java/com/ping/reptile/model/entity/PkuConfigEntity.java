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

    @TableField("agent")
    private String agent;

    @TableField("params")
    private String params;

    @TableField("session")
    private String session;

    @TableField("doc_date")
    private String docDate;

    @TableField("page_num")
    private Integer pageNum;

    @TableField("page_size")
    private Integer pageSize;

    @TableField("grouping")
    private Integer grouping;

    @TableField("case_type")
    private String caseType;
}
