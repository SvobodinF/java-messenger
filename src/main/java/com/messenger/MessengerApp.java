package com.messenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.messenger")
@EnableJpaRepositories(basePackages = "com.messenger.backend.repository")
@EntityScan(basePackages = "com.messenger.backend.domain")
public class MessengerApp {

    public static void main(String[] args) {
        SpringApplication.run(MessengerApp.class, args);
    }
}
