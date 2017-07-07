package com.maxplus1.ms_hello.rest;

import com.alibaba.dubbo.config.annotation.Service;

/**
 * Created by xiaolong.qiu on 2017/5/12.
 */
@Service(version = "2.0.0")
public class HelloRestImplVersion2 implements HelloRest {

    public String hello() {
        return "hello2";
    }

    public String hello(String version) {
        return "hello " + version;
    }

}
