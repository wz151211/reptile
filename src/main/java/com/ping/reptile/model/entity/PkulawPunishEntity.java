package com.ping.reptile.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: W.Z
 * @Date: 2022/8/27 13:11
 */
@Data
@TableName(value = "pkulaw_punish")
public class PkulawPunishEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    @TableField("title")
    private String title;

    @TableField("theme")
    private String theme;

    @TableField("category")
    private String category;

    @TableField("name")
    private String name;

    @TableField("level")
    private String level;

    @TableField("punish_unit")
    private String punishUnit;

    @TableField("area")
    private String area;

    @TableField("punish_date")
    private Date punishDate;

    @TableField("case_no")
    private String caseNo;

    @TableField("basis")
    private String basis;

    @TableField("content")
    private String content;

    @TableField("html")
    private String html;


}
