package com.maxplus1.ms_monitor;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.registry.RegistryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xiaolong.qiu on 2017/7/7.
 */
@RestController
public class TestRest {
    @Reference
    private RegistryService registry;

    @GetMapping("test")
    public String test(){
        System.out.println(registry);
        return "test";
    }
}
