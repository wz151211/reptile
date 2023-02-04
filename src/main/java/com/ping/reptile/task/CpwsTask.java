package com.ping.reptile.task;

import com.ping.reptile.service.CpwsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*@Component
@Async*/
@Slf4j
public class CpwsTask {

    @Autowired
    private CpwsService cpwsService;

    @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 60 * 60 * 12L)
    public void document() {
        try {
            // cpwsService.login();
            cpwsService.params();
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
