package com.ping.reptile;

import com.ping.reptile.service.DocumentService;
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


    @Test
    void contextLoads() {

       // documentService.parse();
    }


    @Test
    public void punish() {
        punishService.page(1, 15);
    }


}
