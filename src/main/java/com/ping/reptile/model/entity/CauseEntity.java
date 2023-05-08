package com.ping.reptile.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("cause")
public class CauseEntity {
    private String id;
    private String pid;
    private String name;
}
