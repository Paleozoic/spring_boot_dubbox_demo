package com.alibaba.dubbo.rpc.cluster.router;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Router;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by qxloo on 2017/6/18.
 */
@Slf4j
public class KeyRouter implements Router{

    private final static ConcurrentHashMap<String,String> keyVersionCache = new ConcurrentHashMap<String,String>();

    private URL url;

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        Object[] arguments = invocation.getArguments();
        if(arguments == null || arguments.length == 0){
            log.error("[ERROR===>>>]KeyRouter needs at least one argument");
            return null;
        }
        Object key = arguments[0];
        List<Invoker<T>> result = new ArrayList<Invoker<T>>();
        //过滤Invoker
        for (Invoker<T> invoker : invokers) {
            String version = invoker.getUrl().getParameter(Constants.VERSION_KEY);
            if (version.equals(keyVersionCache.get(key))) {
                result.add(invoker);
            }
        }
        return result;
    }

    @Override
    public int compareTo(Router o) {
        return 0;
    }


    public KeyRouter(URL url) {
        this.url = url;
    }

    /**
     * 提供一些关于缓存的操作
     */
    public static void clearCache(){
        keyVersionCache.clear();
    }

    public static void add(Map<String,String> map ){
        keyVersionCache.putAll(map);
    }

    public static void remove(String key){
        keyVersionCache.remove(key);
    }

}
