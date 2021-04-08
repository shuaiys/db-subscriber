package com.galen.subscriber.client;

import com.galen.subscriber.client.netty.ClientConstant;
import com.galen.subscriber.client.netty.SubscriberNettyClient;
import com.galen.subscriber.core.SubscriberConfig;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description TODO
 * @date 2020-05-19 23:41
 */
@Component
@Slf4j
public class SubscribeTableRegister implements ApplicationRunner, EnvironmentAware {

    @Resource
    private SubscriberConfig subscriberConfig;

    @Resource
    private SubscriberNettyClient client;

    private Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClientConstant.changeRunningState(true);
        this.registerSubscriberInfo();
    }

    private void registerSubscriberInfo() {
        // 获取注册表
        Map<String, String> subscribeMap = SubscribeListener.SUBSCRIBE_MAP;
        if (MapUtils.isEmpty(subscribeMap)) {
            log.warn("注册表为空，将不会向订阅中心注册");
            return;
        }
        ClientConstant.subscribeMap = subscribeMap;
        ClientConstant.appId = Optional.ofNullable(this.subscriberConfig).map(SubscriberConfig::getAppId)
                .orElse(this.environment.getProperty("spring.application.name"));
        this.connectServer();
    }

    /**
     * 已经废弃该方法
     * @return
     */
    @Deprecated
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
        this.client.connect(this.subscriberConfig);
    }

    private static Set<String> getSubscribeDBInfo(Map<String, String> subscribeMap) {
        return Objects.requireNonNull(subscribeMap).keySet().stream().map(subscribeMap::get).collect(Collectors.toSet());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
