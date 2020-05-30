package com.galen.subscriber.client;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description 开启订阅功能，向容器中导入{@link SubscriberRegistrar}
 * @date 2020-05-18 21:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(SubscriberRegistrar.class)
public @interface EnableSubscriber {

    /**
     * 将会扫描组件的包名
     * @return
     */
    String[] basePackages() default {};

}
