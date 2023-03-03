package com.hs.admin.boot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@ComponentScan("com.hs.admin.*")
@MapperScan("com.hs.common.**")
@SpringBootApplication
@Slf4j
public class HsAdminApplication {


    public static void main(String[] args) {
        SpringApplication.run(HsAdminApplication.class, args);
    }

}
