package com.ping.reptile.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: W.Z
 * @date: 2022/8/22 9:27
 * @desc:
 */

@ConfigurationProperties(prefix = "custom")
@Component
@Data
public class CustomProperties {
    private Integer id = 1;
    private Integer category = 3;
    private Integer intervalDays = 10;
    private String account;
    private Integer min = 10;

    private Integer max = 30;

}
