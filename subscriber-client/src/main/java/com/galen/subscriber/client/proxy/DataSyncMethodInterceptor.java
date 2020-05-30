package com.galen.subscriber.client.proxy;

import com.galen.subscriber.core.ACK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description Cglib
 * @date 2020-05-19 23:21
 */
@Slf4j
public class DataSyncMethodInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        log.info("接收到同步请求");
        Object result = methodProxy.invokeSuper(o, objects);
        doSync(result);
        return result;
    }

    /**
     * 向服务端发送ACK消息
     * @param result
     */
    private void doSync(Object result) {
        if (result instanceof ACK) {
            ACK ack = (ACK) result;
        }
    }
}
