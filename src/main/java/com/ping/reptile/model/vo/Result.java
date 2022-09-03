package com.ping.reptile.model.vo;

import lombok.Data;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 10:27
 */
@Data
public class Result {

    private Integer code;

    private String description;

    private String secretKey;

    private String result;

    private Boolean success;
}
