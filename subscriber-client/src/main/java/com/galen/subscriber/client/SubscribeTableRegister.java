package com.galen.subscriber.client;

import com.galen.subscriber.client.netty.ClientConstant;
import com.galen.subscriber.client.netty.SubscriberNettyClient;
import com.galen.subscriber.core.body.SubscribeInfoRegisterBody;
import com.galen.subscriber.core.SubscriberConfig;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description TODO
 * @date 2020-05-19 23:41
 */
@Component
@Slf4j
public class SubscribeTableRegister implements ApplicationListener<ContextRefreshedEvent>, EnvironmentAware {

    @Resource
    private SubscriberConfig subscriberConfig;

    @Resource
    private SubscriberNettyClient client;

    private Environment environment;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // spring IOC容器初始化完成之后，连接Subscriber Server
        if (event.getApplicationContext().getId().startsWith("db-subscriber")) {
            ClientConstant.isRun = true;
            registerSubscriberInfo();
        }
    }

    private void registerSubscriberInfo() {
        // 获取注册表
        Map<String, String> subscribeMap = SubscribeListener.SUBSCRIBE_MAP;
        if (null == subscribeMap || subscribeMap.isEmpty()) {
            log.warn("注册表为空，将不会向订阅中心注册");
            return;
        }
        ClientConstant.subscribeMap = subscribeMap;
        String appId;
        if (StringUtils.isBlank(subscriberConfig.getAppId())) {
            appId = environment.getProperty("spring.application.name");
        } else {
            appId = subscriberConfig.getAppId();
        }
        ClientConstant.appId = appId;
        connectServer();
    }

    /**
     * 已经废弃该方法
     * @return
     */
    private Channel getChannel() {
        Channel channel = ClientConstant.ctx.channel();
        if (channel == null) {
            // 5s内重复获取
            for (int i = 0; i < 10; i++) {
                channel = ClientConstant.ctx.channel();
                if (null != channel) {
                    return channel;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            throw new SecurityException("未获取到subscriber channel");
        }
        return channel;
    }

    @Async
    public void connectServer() {
        client.connect(subscriberConfig);
    }

    private Set<String> getSubscribeDBInfo(Map<String, String> subscribeMap) {
        Set<String> subscribe = new HashSet<>();
        subscribeMap.keySet().forEach(s -> subscribe.add(subscribeMap.get(s)));
        return subscribe;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
