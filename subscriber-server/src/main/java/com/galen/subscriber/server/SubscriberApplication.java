package com.galen.subscriber.server;

import com.galen.subscriber.server.netty.SubscriberNettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server
 * @description Subscriber Server启动类
 * @date 2020-05-18 23:01
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.galen.subscriber"})
@EnableScheduling
@EnableAsync
public class SubscriberApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubscriberApplication.class, args);
        try {
            // 启动netty server
            SubscriberNettyServer.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
