package com.galen.subscriber.client.proxy;

import com.galen.subscriber.client.DataSync;
import com.galen.subscriber.core.Ack;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description JDK动态代理处理器
 * @date 2020-05-18 23:50
 */
@Slf4j
public class DataSyncProxyHandle implements InvocationHandler {

    private DataSync target;

    public DataSyncProxyHandle(DataSync target){
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("接收到同步请求");
        Object invoke = method.invoke(target, args);
        Ack result = (Ack) invoke;
        // 处理ack
        this.doACK(result);
        return invoke;
    }

    private void doACK(Ack ack) {

    }
}
