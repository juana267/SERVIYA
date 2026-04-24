package com.serviya.msservicerequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsServiceRequestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsServiceRequestApplication.class, args);
    }
}
