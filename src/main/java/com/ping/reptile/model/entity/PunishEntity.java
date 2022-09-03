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
@TableName(value = "punish")
public class PunishEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    @TableField("case_no")
    private String caseNo;

    @TableField("name")
    private String name;

    @TableField("punish_date")
    private Date punishDate;

    @TableField("punish_unit")
    private String punishUnit;

    @TableField("type")
    private String type;

    @TableField("basis")
    private String basis;

    @TableField("result")
    private String result;

    @TableField("content")
    private String content;

    @TableField("theme")
    private String theme;

    @TableField("area_code")
    private String areaCode;

    @TableField("area_name")
    private String areaName;


}
