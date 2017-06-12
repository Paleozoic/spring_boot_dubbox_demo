package com.maxplus1.ms_world.rest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.maxplus1.ms_hello.rest.HelloRest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xiaolong.qiu on 2017/5/12.
 */
@RestController
public class WorldRest {

    @Reference(version = "1.0.0")
    private HelloRest helloRest;

    @Reference(version = "2.0.0")
    private HelloRest helloRest2;

    @GetMapping("world")
    public String world() {
        return helloRest.hello() + " world!";
    }

    @GetMapping("world2")
    public String world2() {
        return helloRest2.hello() + " world!";
    }

}
