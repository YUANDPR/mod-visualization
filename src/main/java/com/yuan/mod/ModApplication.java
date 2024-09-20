package com.yuan.mod;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.yuan.mod.mapper")
public class ModApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModApplication.class, args);
    }
}
