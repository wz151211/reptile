package com.ping.reptile;

import com.ping.reptile.service.CpwsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class ReptileApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ReptileApplication.class, args);
        CpwsService cpwsService = applicationContext.getBean(CpwsService.class);
        try {
            cpwsService.login();
            cpwsService.params();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
