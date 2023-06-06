package com.ping.reptile.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Item {
    private Integer order;
    private Integer combineAs;
    private String start;
    private String end;
}
