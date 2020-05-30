package com.galen.subscriber.example;

import com.galen.subscriber.client.EnableSubscriber;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.example
 * @description 测试启动类
 * @date 2020-05-26 22:28
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.galen.subscriber"})
@EnableSubscriber(basePackages = {"com.galen.subscriber.example"})
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
