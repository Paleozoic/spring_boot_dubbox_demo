package com.alibaba.dubbo.rpc.cluster.router;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.cluster.Router;
import com.alibaba.dubbo.rpc.cluster.RouterFactory;

/**
 * Created by qxloo on 2017/6/18.
 */
public class KeyRouterFactory implements RouterFactory {
    @Override
    public Router getRouter(URL url) {
        return new KeyRouter(url); //其实可以写成单例
    }
}
