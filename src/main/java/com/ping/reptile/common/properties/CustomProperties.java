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


    private Integer pageNum = 1;

    private Integer pageSize = 15;

    private String cookie;

    private String userAgent;

    private String secChUa;

    private String type;

    private String docDate;

    private String punishDate;

}
