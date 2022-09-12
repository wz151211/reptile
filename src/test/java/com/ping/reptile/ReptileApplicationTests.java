package com.ping.reptile;

import com.ping.reptile.service.CasePunishService;
import com.ping.reptile.service.DocumentService;
import com.ping.reptile.service.PkulawService;
import com.ping.reptile.service.PunishService;
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
    private PunishService punishService;

    @Autowired
    private CasePunishService casePunishService;

    @Autowired
    private PkulawService pkulawService;


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


}
