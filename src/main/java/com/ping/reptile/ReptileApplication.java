package com.ping.reptile;

import com.ping.reptile.cpws.service.CpwsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReptileApplication {

    @Autowired
    private CpwsService cpwsService;
    public static void main(String[] args) {
        SpringApplication.run(ReptileApplication.class, args);

    }

}
