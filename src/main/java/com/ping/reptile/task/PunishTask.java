package com.ping.reptile.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: W.Z
 * @Date: 2022/8/27 09:05
 */
@Component
public class PunishTask {


    @Scheduled(initialDelay = 5)
    public void task() {
        System.out.println("111111s");
    }
}
