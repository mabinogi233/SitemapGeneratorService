package com.example.sitemap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SitemapApplication {

    public static void main(String[] args) {
        SpringApplication.run(SitemapApplication.class, args);
    }

}
