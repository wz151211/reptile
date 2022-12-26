package com.ping.reptile.model.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Pkulaw {

    private String orderbyExpression;

    private Integer pageIndex;

    private Integer pageSize;

    private List<JSONObject> fieldNodes;

    private Map<String, String> clusterFilters ;

    private Map<String, String> groupBy ;
}
