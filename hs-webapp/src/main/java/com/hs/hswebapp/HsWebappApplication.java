package com.hs.hswebapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@ComponentScan("com.hs.*")
@MapperScan("com.hs.common.**")
@SpringBootApplication
@Slf4j
public class HsWebappApplication {


    public static void main(String[] args) {
        SpringApplication.run(HsWebappApplication.class, args);
    }

}
