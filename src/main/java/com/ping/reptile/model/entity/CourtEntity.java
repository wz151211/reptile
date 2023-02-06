package com.ping.reptile.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "court")
public class CourtEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("parent_id")
    private String parentId;

    @TableField("name")
    private String name;

    @TableField("code")
    private String code;

    @TableField("province")
    private String province;

}
