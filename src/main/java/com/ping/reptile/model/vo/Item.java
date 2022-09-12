package com.ping.reptile.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Item {
    private Integer order;
    private Integer combineAs;
    private String start;
    private String end;
    private List<String> values;
    private List<Theme> items;
    private List<String> value;
    private List<String> keywordTagData;
    private List<String> filterNodes = new ArrayList<>();

}
