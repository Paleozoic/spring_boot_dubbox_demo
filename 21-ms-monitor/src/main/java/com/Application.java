package com;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(includeFilters = {@ComponentScan.Filter(value = Service.class)})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
