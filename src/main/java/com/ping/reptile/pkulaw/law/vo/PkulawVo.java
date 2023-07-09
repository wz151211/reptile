package com.ping.reptile.pkulaw.law.vo;

import lombok.Data;

import java.util.List;

@Data
public class PkulawVo {

    private boolean isHaveEng;

    private String engLibrary;

    private String engGid;

    private String keywords;

    private String displayStyle;

    private String gid;

    private String title;

    private List<SummaryVo> summaries;
    private List<TimelinessVo> timeliness;

    private List<TimelinessVo> sections;

    private List<TimelinessVo> caseGrade;

    private List<TipsVo> tips;
}
