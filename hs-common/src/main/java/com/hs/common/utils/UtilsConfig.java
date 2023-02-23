package com.hs.common.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {

    @Bean
    SnowflakeIdGenerator getIdGenerator(){
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(0L,0L);
        return  snowflakeIdGenerator;
    }
}
