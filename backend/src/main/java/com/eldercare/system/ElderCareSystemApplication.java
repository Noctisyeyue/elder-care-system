package com.eldercare.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 颐养中心后台管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.eldercare.system.mapper")
public class ElderCareSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElderCareSystemApplication.class, args);
    }
}
