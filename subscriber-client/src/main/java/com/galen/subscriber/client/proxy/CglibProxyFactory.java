package com.galen.subscriber.client.proxy;

import com.galen.subscriber.client.DataSync;
import org.springframework.cglib.proxy.Enhancer;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description Cglib代理工厂
 * @date 2020-05-19 23:21
 */
public class CglibProxyFactory {

    public static DataSync getProxy(Class<DataSync> target){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target);
        enhancer.setCallback(new DataSyncMethodInterceptor());
        DataSync proxy = (DataSync) enhancer.create();
        return proxy;
    }
}
