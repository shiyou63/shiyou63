package com.bkxt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 */
@SpringBootApplication
@MapperScan("com.bkxt.mapper")
@EnableScheduling
public class BlogApplication {

    public static void main(String[] args) {

        SpringApplication.run(BlogApplication.class,args);
    }
}