package com.ping.reptile.pkulaw.law.vo;

import lombok.Data;

import java.util.List;

@Data
public class Result {
    private Integer total;
    private Integer sum;
    private List<PkulawVo> data;

}
