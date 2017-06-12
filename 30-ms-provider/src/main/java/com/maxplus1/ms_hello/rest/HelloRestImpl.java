package com.maxplus1.ms_hello.rest;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xiaolong.qiu on 2017/5/12.
 */
@Service(version = "1.0.0")
@RestController("test")
public class HelloRestImpl implements HelloRest {

    @GetMapping("hello")
    public String hello() {
        return "hello";
    }

}
