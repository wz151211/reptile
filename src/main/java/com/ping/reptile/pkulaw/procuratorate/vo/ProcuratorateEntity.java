package com.ping.reptile.pkulaw.procuratorate.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("plulaw_pro")
public class ProcuratorateEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String gid;
    private String title;
    @TableField("case_no")
    private String caseNo;
    private String date;
    @TableField("release_date")
    private String releaseDate;
    private String cause;
    @TableField("doc_type")
    private String docType;
    private String procuratorate;
    private String business;
    private String considered;
    private String content;
    @TableField("create_time")
    private Date createTime;

}
