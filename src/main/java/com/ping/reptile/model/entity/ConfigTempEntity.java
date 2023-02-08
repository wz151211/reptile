package com.ping.reptile.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "config")
public class ConfigTempEntity {


    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("referee_date")
    private String refereeDate;

    @TableField("end_date")
    private String endDate;

    @TableField("interval_days")
    private Integer intervalDays;

    @TableField("min")
    private Integer min;

    @TableField("max")
    private Integer max;

    @TableField("full_text_name")
    private String fullTextName;

    @TableField("full_text_type")
    private String fullTextType;

    @TableField("court_name")
    private String courtName;

    @TableField("case_type")
    private String caseType;

    @TableField("doc_type")
    private String docType;

    @TableField("trial_proceedings")
    private String trialProceedings;

}
