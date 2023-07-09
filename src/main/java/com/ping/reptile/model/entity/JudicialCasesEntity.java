package com.ping.reptile.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "pkulaw_judicial_cases")
public class JudicialCasesEntity {


    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("gid")
    private String gid;

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

    @TableField("referee_date")
    private Date refereeDate;

    @TableField("case_type")
    private String caseType;

    @TableField("trial_proceedings")
    private String trialProceedings;

    @TableField("law_firm")
    private String lawFirm;

    @TableField("keyword")
    private String keyword;

    @TableField("case_grade")
    private String caseGrade;

    @TableField("html")
    private String html;

  /*  @TableField("content")
    private String content;*/

    @TableField("create_time")
    private Date createTime;
}
