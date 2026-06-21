package com.atlas.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Entry point for the Hotel Service (catalog owner). */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class HotelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelServiceApplication.class, args);
    }
}
