package com.traineta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrainEtaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainEtaApplication.class, args);
    }
}
