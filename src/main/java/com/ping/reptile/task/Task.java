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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: W.Z
 * @Date: 2022/8/27 09:05
 */
@Component
@Async
@Slf4j
public class Task {
    private final Lock docLock = new ReentrantLock();
    private final Lock punishLock = new ReentrantLock();
    private final Lock casePunishLock = new ReentrantLock();

    private final Lock pkulawLock = new ReentrantLock();

    private final Lock tongYongLock = new ReentrantLock();

    private final Lock judicialCasesLock = new ReentrantLock();
    @Autowired
    private DocumentService documentService;
    @Autowired
    private PunishService punishService;
    @Autowired
    private PkulawService_bak pkulawService;

    @Autowired
    private CasePunishService casePunishService;

    @Autowired
    private PkulawUpdateService updateService;

    @Autowired
    private UpdateDocumentService updateDocumentService;
    @Autowired
    private JudicialCasesService judicialCasesService;

    // @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void document() {
        boolean tryLock = false;
        try {
            tryLock = docLock.tryLock(2, TimeUnit.SECONDS);
            documentService.page(1, 15);
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

    @Scheduled(initialDelay = 5 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void pkulaw() {
        pkulawService.page(0, 20);
    }

    //  @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void documentUpdate() {
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

    //  @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 30L)
    public void JudicialCases() {
        boolean tryLock = false;
        try {
            tryLock = judicialCasesLock.tryLock(2, TimeUnit.SECONDS);
            List<String> codes = new ArrayList<>();
            codes.add("02");
            codes.add("03");
            codes.add("04");
            codes.add("0");
            codes.add("06");
            codes.add("07");
            codes.add("08");
            codes.add("09");
            codes.add("10");
            for (String code : codes) {
                judicialCasesService.page(0, 50, code);
            }

        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                judicialCasesLock.unlock();
            }
        }
    }
}
