package com.galen.subscriber.client.proxy;

import com.galen.subscriber.client.DataSync;

import java.lang.reflect.Proxy;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description JDK动态代理工厂
 * @date 2020-05-18 23:57
 */
public class JDKProxyFactory {

    public static DataSync getProxy(DataSync target) {
        DataSyncProxyHandle handle = new DataSyncProxyHandle(target);
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new IllegalStateException("需要实现DataSync接口");
        }
        DataSync proxy = (DataSync) Proxy.newProxyInstance(target.getClass().getClassLoader(), interfaces, handle);
        return proxy;
    }
}
