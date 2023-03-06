package com.hs.admin.boot;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import tk.mybatis.spring.annotation.MapperScan;

@ComponentScan("com.hs.admin.*")
@MapperScan("com.hs.common.**")
@ImportResource(locations = "classpath:dubbo-*.xml")
@SpringBootApplication
@Slf4j
public class HsAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(HsAdminApplication.class, args);


    }

}
