package com.galen.subscriber.client;

import com.galen.subscriber.client.proxy.CglibProxyFactory;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description 使用 {@link com.galen.subscriber.client.annotation.Subscriber}  注解标注的类将会被代理
 * @date 2020-05-18 23:25
 */
@Data
public class SubscriberFactoryBean implements FactoryBean<DataSync>, InitializingBean, ApplicationEventPublisherAware {

    private String db;

    private String table;

    private String name;

    private String contextId;

    private Class<DataSync> type;

    private ApplicationEventPublisher publisher;

    @Override
    public DataSync getObject() throws Exception {
        return getTarget();
    }

    private DataSync getTarget() {
        //this.applicationContext.getBean()
        // JDK 动态代理
//        DataSync dataSync = type.newInstance();
//        DataSync proxy = JDKProxyFactory.getProxy(dataSync);
        // cglib 代理生成目标增强类
        DataSync proxy = CglibProxyFactory.getProxy(type);
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        publisher.publishEvent(new SubscribeEvent("", db, table, name, contextId));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
