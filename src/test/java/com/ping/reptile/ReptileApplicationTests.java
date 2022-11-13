package com.ping.reptile;

import com.ping.reptile.service.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ReptileApplicationTests {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private CompensateDocumentService compensateDocumentService;
    @Autowired
    private PunishService punishService;

    @Autowired
    private CasePunishService casePunishService;

    @Autowired
    private PkulawService pkulawService;

    @Autowired
    private PkulawUpdateService updateService;


    @Autowired
    private UpdateDocumentService updateDocumentService;

    @Test
    void contextLoads() {

        // documentService.parse();
    }


    @Test
    public void punish() {
        punishService.page(1, 15);
    }

    @Test
    public void casePunish() {
        casePunishService.page(1, 150);
    }

    @Test
    public void pkulawPunish() {
        pkulawService.page(0, 100);
    }

    @Test
    public void updatePunish() {
        updateService.list();
    }

    @Test
    public void document() {
        compensateDocumentService.page(1, 15);
    }

    @Test
    public void updateDocument() {
        updateDocumentService.update();
    }


}
