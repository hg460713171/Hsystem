package com.hs.hswebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@ComponentScan("com.hs")
@MapperScan("com.hs.common.**")
@SpringBootApplication
public class HsWebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(HsWebappApplication.class, args);
    }

}
