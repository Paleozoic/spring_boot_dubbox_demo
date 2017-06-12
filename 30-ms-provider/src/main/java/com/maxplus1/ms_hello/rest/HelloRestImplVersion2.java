package com.maxplus1.ms_hello.rest;

import com.alibaba.dubbo.config.annotation.Service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * Created by xiaolong.qiu on 2017/5/12.
 */
@Service(version = "2.0.0")
public class HelloRestImplVersion2 implements HelloRest {

    public String hello() {
        return "hello2";
    }

}
