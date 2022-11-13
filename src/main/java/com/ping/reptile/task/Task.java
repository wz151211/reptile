package com.ping.reptile.task;

import com.ping.reptile.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
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
    private final Lock casePunishLock = new ReentrantLock();

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

    @Autowired
    private PkulawUpdateService updateService;

    @Autowired
    private UpdateDocumentService updateDocumentService;

    @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 60 * 30L)
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

    //    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 30L)
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

 //   @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void casePunish() {
        boolean tryLock = false;
        try {
            tryLock = casePunishLock.tryLock(2, TimeUnit.SECONDS);
            casePunishService.page(1, 100);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                casePunishLock.unlock();
            }
        }
    }

    //  @Scheduled(initialDelay = 5 * 1000L, fixedRate = 1000 * 60 * 30L)
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

    //@Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 30L)
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

  //  @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void tongYong11() {
        boolean tryLock = false;
        try {
            tryLock = tongYongLock.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.update();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                tongYongLock.unlock();
            }
        }
    }


    //@Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void insert() {
        boolean tryLock = false;
        try {
            tryLock = tongYongLock.tryLock(2, TimeUnit.SECONDS);
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:excel/**");
            String path = null;
            InputStream inputStream = null;
            for (Resource resource : resources) {
                URI url = resource.getURI();
                inputStream = resource.getInputStream();
                path = url.getPath();
            }
            updateService.insert(inputStream);
            //    updateService.list();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                tongYongLock.unlock();
            }
        }
    }
}
