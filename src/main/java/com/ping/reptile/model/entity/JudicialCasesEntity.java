package com.ping.reptile.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "pku_judicial_cases")
public class JudicialCasesEntity {


    @TableId(type = IdType.AUTO)
    private String id;

    @TableField("title")
    private String title;

    @TableField("cause")
    private String cause;

    @TableField("case_no")
    private String caseNo;

    @TableField("judge")
    private String judge;

    @TableField("doc_type")
    private String docType;

    @TableField("exposed_type")
    private String exposedType;

    @TableField("court")
    private String court;

    @TableField("conclusion_date")
    private Date conclusionDate;

    @TableField("case_type")
    private String caseType;

    @TableField("trial_proceedings")
    private String trialProceedings;

    @TableField("law_firm")
    private String lawFirm;

    @TableField("keyword")
    private String keyword;

    @TableField("html")
    private String html;

    @TableField("content")
    private String content;
}
