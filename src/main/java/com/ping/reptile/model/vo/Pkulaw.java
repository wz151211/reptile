package com.ping.reptile.model.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Pkulaw {

    private String orderbyExpression;

    private Integer pageIndex;

    private Integer pageSize;

    private List<Node> fieldNodes = new ArrayList<>();

    private Map<String, String> clusterFilters = new HashMap<>();

    private Map<String, String> groupBy = new HashMap<>();
}
