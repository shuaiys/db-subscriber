package com.galen.subscriber.client;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description
 * 监听 {@link SubscriberFactoryBean}的实例化，执行afterPropertiesSet()方法时，发送事件
 * 获取到需要注册的库表，以及bean的别名等等。
 * @date 2020-05-19 22:10
 */
@Component
public class SubscribeListener implements ApplicationListener<SubscribeEvent> {

    private static final String connectString = ".";

    public static final Map<String, String> SUBSCRIBE_MAP = new HashMap<>();

    @Override
    public void onApplicationEvent(SubscribeEvent event) {
        String alias = getAlias(event.getContextId(), event.getName());
        SUBSCRIBE_MAP.put(alias, event.getDb() + connectString + event.getTable());
    }

    private String getAlias(String contextId, String name) {
        return name + contextId + "SubscriberClient";
    }
}
