package com.ping.reptile.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.reptile.pkulaw.law.PkulawMapper;
import com.ping.reptile.pkulaw.law.vo.PkulawEntity;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class UpdatePkuLaw {
    @Autowired
    private PkulawMapper pkulawMapper;
    private AtomicInteger page = new AtomicInteger(42);
    private int pageSize = 10000;

    public void testUpdate() {
        page.getAndIncrement();

        List<PkulawEntity> entities = pkulawMapper.selectList(Wrappers.<PkulawEntity>lambdaQuery().ge(PkulawEntity::getId, "3576746").last("limit " + (page.get()) * pageSize + ", " + pageSize));
        log.info("page={},size={}", page.get(), entities.size());
        entities.parallelStream().forEach(e -> {
            Document parse = Jsoup.parse(e.getAllText());
            Element divFullText = null;
            try {
                divFullText = parse.getElementById("divFullText");
                if (divFullText != null) {
                    e.setAllText(divFullText.html());
                    pkulawMapper.update(null, Wrappers.<PkulawEntity>lambdaUpdate().set(PkulawEntity::getAllText, e.getAllText()).eq(PkulawEntity::getId, e.getId()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
