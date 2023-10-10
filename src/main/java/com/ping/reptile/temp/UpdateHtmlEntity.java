package com.ping.reptile.temp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("pkulaw_law_new")
public class UpdateHtmlEntity {
    @TableId(type = IdType.INPUT)
    private Integer id;

    @TableField("library")
    private String library;

    @TableField("gid")
    private String gid;

    @TableField("title")
    private String title;

    @TableField("issue_department")
    private String issueDepartment;

    @TableField("ratify_department")
    private String ratifyDepartment;

    @TableField("document_no")
    private String documentNo;

    @TableField("ratify_date")
    private String ratifDate;

    @TableField("issue_date")
    private String issueDate;

    @TableField("implement_date")
    private String implementDate;

    @TableField("timeliness_dic")
    private String timelinessDic;

    @TableField("invalid_basis")
    private String invalidBasis;

    @TableField("effectiveness_dic")
    private String effectivenessDic;

    @TableField("category")
    private String category;

    @TableField("keywords")
    private String keywords;

    @TableField("topic_type")
    private String topicType;

    @TableField("all_text")
    private String allText;

    @TableField("text")
    private String text;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
