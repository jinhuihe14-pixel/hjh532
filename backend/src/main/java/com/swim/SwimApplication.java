package com.swim;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.swim.mapper")
@EnableScheduling
@EnableAsync
public class SwimApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwimApplication.class, args);
        System.out.println("游泳馆管理系统启动成功！");
    }
}
