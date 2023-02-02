package com.ping.reptile.cpws.service;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("org")
public class Org {
    public String id;
    public String pid;
    public String name;
    public int level;
    public String path;
    public String province;
    public String city;
    public String county;

}
