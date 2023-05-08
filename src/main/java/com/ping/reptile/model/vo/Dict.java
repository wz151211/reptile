package com.ping.reptile.model.vo;

import lombok.Data;

/**
 * @Author: W.Z
 * @Date: 2022/8/27 00:12
 */
@Data
public class Dict {
    private Integer id;
    private String parentid;
    private String code;
    private String parent;
    private String name;
    private String text;
    private String province;
}
