package com.maxplus1.ms_world.rest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.cluster.router.KeyRouter;
import com.maxplus1.ms_hello.rest.HelloRest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaolong.qiu on 2017/5/12.
 */
@RestController
public class WorldRest {

    //模拟操作：从db取出配置。初始化key-version对
    static {
        Map<String,String > map = new HashMap<>();
        map.put("v1","1.0.0");
        map.put("v2","2.0.0");
        KeyRouter.add(map);
    }

    @Reference(version = "1.0.0")
    private HelloRest helloRest;

    @Reference(version = "2.0.0")
    private HelloRest helloRest2;

    @Reference(version = "*",router = "keyRouter")
    private HelloRest helloRest3;

    @GetMapping("world")
    public String world() {
        return helloRest.hello() + " world!";
    }

    @GetMapping("world2")
    public String world2() {
        return helloRest2.hello() + " world!";
    }

    @GetMapping("world3")
    public String world3() {
        return helloRest3.hello() + " world!";
    }

    @GetMapping("world3/{version}")
    public String world3(@PathVariable("version") String version) {
        return helloRest3.hello(version) + " world!";
    }

}
