package com.ping.reptile.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Theme {
    private String name;
    private String value;
    private String text;
    private String path;
}
