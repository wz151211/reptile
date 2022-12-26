package com.ping.reptile.cpws.task;

import com.ping.reptile.cpws.service.SyncDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*@Component
@Async*/
@Slf4j
public class CpwsTask2 {


    private final Lock lock8 = new ReentrantLock();
    private final Lock lock9 = new ReentrantLock();
    private final Lock lock10 = new ReentrantLock();
    private final Lock lock15 = new ReentrantLock();
    private final Lock lock19 = new ReentrantLock();
    private final Lock lock20 = new ReentrantLock();
    private final Lock lock68 = new ReentrantLock();
    private final Lock lock87 = new ReentrantLock();

    @Autowired
    private SyncDocumentService documentService;

    @Scheduled(initialDelay = 19 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws8() {
        boolean tryLock = false;
        try {
            tryLock = lock8.tryLock(2, TimeUnit.SECONDS);
            documentService.sync8();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock8.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 21 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws9() {
        boolean tryLock = false;
        try {
            tryLock = lock9.tryLock(2, TimeUnit.SECONDS);
            documentService.sync9();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock9.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 23 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws10() {
        boolean tryLock = false;
        try {
            tryLock = lock10.tryLock(2, TimeUnit.SECONDS);
            documentService.sync10();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock10.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 25 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws15() {
        boolean tryLock = false;
        try {
            tryLock = lock15.tryLock(2, TimeUnit.SECONDS);
            documentService.sync15();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock15.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 27 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws19() {
        boolean tryLock = false;
        try {
            tryLock = lock19.tryLock(2, TimeUnit.SECONDS);
            documentService.sync19();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock19.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 29 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws20() {
        boolean tryLock = false;
        try {
            tryLock = lock20.tryLock(2, TimeUnit.SECONDS);
            documentService.sync20();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock20.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 31 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws68() {
        boolean tryLock = false;
        try {
            tryLock = lock68.tryLock(2, TimeUnit.SECONDS);
            documentService.sync68();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock68.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 33 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws87() {
        boolean tryLock = false;
        try {
            tryLock = lock87.tryLock(2, TimeUnit.SECONDS);
            documentService.sync87();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock87.unlock();
            }
        }
    }
}
