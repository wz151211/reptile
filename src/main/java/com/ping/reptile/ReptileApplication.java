package com.ping.reptile;

import com.ping.reptile.service.CpwsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReptileApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ReptileApplication.class, args);
        CpwsService bean = applicationContext.getBean(CpwsService.class);
        try {
            bean.login();
            bean.params();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
