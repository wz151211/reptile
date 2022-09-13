package com.ping.reptile.task;

import com.ping.reptile.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: W.Z
 * @Date: 2022/8/27 09:05
 */
@Component
@Slf4j
@Async
public class Task {
    private final Lock docLock = new ReentrantLock();
    private final Lock punishLock = new ReentrantLock();

    private final Lock pkulawLock = new ReentrantLock();

    private final Lock tongYongLock = new ReentrantLock();
    @Autowired
    private DocumentService documentService;
    @Autowired
    private PunishService punishService;
    @Autowired
    private PkulawService pkulawService;

    @Autowired
    private TongYongPkulawService tongYongPkulawService;
    @Autowired
    private CasePunishService casePunishService;

    //   @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void document() {
        boolean tryLock = false;
        try {
            tryLock = docLock.tryLock(2, TimeUnit.SECONDS);
            documentService.page(1, 100);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                docLock.unlock();
            }
        }
    }

    //   @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void punish() {
        boolean tryLock = false;
        try {
            tryLock = punishLock.tryLock(2, TimeUnit.SECONDS);
            punishService.page(1, 100);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                punishLock.unlock();
            }
        }
    }

   // @Scheduled(initialDelay = 5 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void pkulaw() {
        boolean tryLock = false;
        try {
            tryLock = pkulawLock.tryLock(2, TimeUnit.SECONDS);
            pkulawService.page(0, 10);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                pkulawLock.unlock();
            }
        }
    }
    @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void tongYong() {
        boolean tryLock = false;
        try {
            tryLock = tongYongLock.tryLock(2, TimeUnit.SECONDS);
            tongYongPkulawService.page(0, 10);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                tongYongLock.unlock();
            }
        }
    }
}
