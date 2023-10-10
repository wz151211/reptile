package com.ping.reptile.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.pkulaw.law.PkulawMapper;
import com.ping.reptile.pkulaw.law.vo.PkulawEntity;
import com.ping.reptile.temp.UpdateHtmlEntity;
import com.ping.reptile.temp.UpdateHtmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class UpdatePkuLaw {
    @Autowired
    private PkulawMapper pkulawMapper;
    private AtomicInteger page = new AtomicInteger(0);
    private int pageSize = 10000;
    @Autowired
    private UpdateHtmlMapper htmlMapper;

    public void testUpdate() {
        log.info("page={}", page.get());
        List<PkulawEntity> entities = pkulawMapper.selectList(Wrappers.<PkulawEntity>lambdaQuery().last("limit " + (page.get()) * pageSize + ", " + pageSize));
        log.info("page={},size={}", page.get(), entities.size());
        entities.parallelStream().filter(e -> StringUtils.isNotEmpty(e.getAllText())).forEach(e -> {
            Document parse = Jsoup.parse(e.getAllText());
            e.setText(parse.text());
            UpdateHtmlEntity htmlEntity = new UpdateHtmlEntity();
            BeanUtils.copyProperties(e, htmlEntity);
            try {
                htmlMapper.insert(htmlEntity);
                pkulawMapper.deleteById(e.getId());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });
    }
}
