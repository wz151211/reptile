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
public class CpwsTask1 {

    private final Lock lock = new ReentrantLock();
    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();
    private final Lock lock3 = new ReentrantLock();
    private final Lock lock4 = new ReentrantLock();
    private final Lock lock5 = new ReentrantLock();
    private final Lock lock6 = new ReentrantLock();
    private final Lock lock7 = new ReentrantLock();


    @Autowired
    private SyncDocumentService documentService;

    @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws() {
        boolean tryLock = false;
        try {
            tryLock = lock.tryLock(2, TimeUnit.SECONDS);
            documentService.sync();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 5 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws1() {
        boolean tryLock = false;
        try {
            tryLock = lock1.tryLock(2, TimeUnit.SECONDS);
            documentService.sync1();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock1.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 7 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws2() {
        boolean tryLock = false;
        try {
            tryLock = lock2.tryLock(2, TimeUnit.SECONDS);
            documentService.sync2();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock2.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 9 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws3() {
        boolean tryLock = false;
        try {
            tryLock = lock3.tryLock(2, TimeUnit.SECONDS);
            documentService.sync3();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock3.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 11 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws4() {
        boolean tryLock = false;
        try {
            tryLock = lock4.tryLock(2, TimeUnit.SECONDS);
            documentService.sync4();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock4.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 13 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws5() {
        boolean tryLock = false;
        try {
            tryLock = lock5.tryLock(2, TimeUnit.SECONDS);
            documentService.sync5();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock5.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 15 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void cpws6() {
        boolean tryLock = false;
        try {
            tryLock = lock6.tryLock(2, TimeUnit.SECONDS);
            documentService.sync6();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock6.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 17 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void cpws7() {
        boolean tryLock = false;
        try {
            tryLock = lock7.tryLock(2, TimeUnit.SECONDS);
            documentService.sync7();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock7.unlock();
            }
        }
    }

}
