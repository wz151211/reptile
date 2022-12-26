package com.ping.reptile.cpws.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "cpws10")
public class Cpws10Entity extends BaseEntity {

    @TableField("state")
    private Integer state;
}
