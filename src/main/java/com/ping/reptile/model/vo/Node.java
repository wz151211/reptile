package com.ping.reptile.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Node {

    private String type;
    private Integer order;
    private String showText;
    private String fieldName;
    private Integer combineAs;
    private String range;
    private List<Item> fieldItems = new ArrayList<>();

}
