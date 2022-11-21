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

@Component
@Async
@Slf4j
public class CpwsTask {

    private final Lock lock = new ReentrantLock();
    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();
    private final Lock lock3 = new ReentrantLock();
    private final Lock lock4 = new ReentrantLock();
    private final Lock lock5 = new ReentrantLock();
    private final Lock lock6 = new ReentrantLock();
    private final Lock lock7 = new ReentrantLock();
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

    @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 60 * 30L)
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

  //  @Scheduled(initialDelay = 5 * 1000L, fixedRate = 1000 * 60 * 30L)
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

//    @Scheduled(initialDelay = 7 * 1000L, fixedRate = 1000 * 60 * 30L)
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

 //   @Scheduled(initialDelay = 9 * 1000L, fixedRate = 1000 * 60 * 30L)
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

//    @Scheduled(initialDelay = 11 * 1000L, fixedRate = 1000 * 60 * 30L)
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

//    @Scheduled(initialDelay = 13 * 1000L, fixedRate = 1000 * 60 * 30L)
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

 //   @Scheduled(initialDelay = 15 * 1000L, fixedRate = 1000 * 60 * 30L)
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

 //   @Scheduled(initialDelay = 17 * 1000L, fixedRate = 1000 * 60 * 30L)
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

    @Scheduled(initialDelay = 19 * 1000L, fixedRate = 1000 * 60 * 30L)
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

    @Scheduled(initialDelay = 21 * 1000L, fixedRate = 1000 * 60 * 30L)
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

    @Scheduled(initialDelay = 23 * 1000L, fixedRate = 1000 * 60 * 30L)
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

    @Scheduled(initialDelay = 25 * 1000L, fixedRate = 1000 * 60 * 30L)
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

    @Scheduled(initialDelay = 27 * 1000L, fixedRate = 1000 * 60 * 30L)
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

    @Scheduled(initialDelay = 29 * 1000L, fixedRate = 1000 * 60 * 30L)
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

    @Scheduled(initialDelay = 31 * 1000L, fixedRate = 1000 * 60 * 30L)
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

    @Scheduled(initialDelay = 33 * 1000L, fixedRate = 1000 * 60 * 30L)
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
