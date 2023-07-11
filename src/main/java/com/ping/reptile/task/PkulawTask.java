package com.ping.reptile.task;

import com.ping.reptile.pkulaw.cases.JudicialCasesService;
import com.ping.reptile.pkulaw.law.PkulawService;
import com.ping.reptile.pkulaw.procuratorate.ProcuratorateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Async
@Slf4j
public class PkulawTask {

    @Autowired
    private PkulawService pkulawService;

    @Autowired
    private ProcuratorateService procuratorateService;
    @Autowired
    private JudicialCasesService judicialCasesService;

    @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 60 * 60 * 24L)
    public void document() {
        try {
            pkulawService.page(0, 100);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    //   @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 60 * 60 * 24L)
    public void procuratorateDocument() {
        try {
            procuratorateService.page(0, 100);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    // @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 60 * 60 * 24L)
    public void judicialCases() {
        try {
            judicialCasesService.page(0, 100);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
