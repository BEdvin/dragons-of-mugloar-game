package com.bigbank.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class GameApplication {

    public static void main(final String[] args) {
        SpringApplication.run(GameApplication.class, args);
    }

}
