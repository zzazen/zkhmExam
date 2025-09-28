package com.zkhm.exam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: zkhmExam
 * @description:
 * @author: Mr.Zhang
 * @create: 2025-09-25 10:23
 **/


@SpringBootApplication(scanBasePackages = "com.zkhm.exam.ai")
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
