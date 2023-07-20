package com.ping.reptile.pkulaw.punish.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("pkulaw_punish")
public class PunishEntity {

    @Id
    private String id;

    private String title;

    private String caseNo;

    private String theme;

    private String category;

    private String punishmentTarget;

    private String level;

    private String punishUnit;

    private Date punishDate;

    private String basis;

    private String area;

    private String province;

    private String city;

    private String county;

    private String html;

    private Date createTime;
}
