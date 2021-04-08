package com.galen.subscriber.client.annotation;

import com.galen.subscriber.core.EventTypeEnum;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client.annotation
 * @description 订阅注解，被标注的类会被代理之后加入IOC容器
 * @date 2020-05-18 21:37
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Subscriber {

    /**
     * 数据库名称
     *
     * @return
     */
    String db();

    /**
     * 表名称
     *
     * @return
     */
    String table();

    /**
     * bean的别名
     *
     * @return
     */
    String name();

    String contextId() default "";

    /**
     * 订阅的字段，默认所有字段
     *
     * @return
     */
    String[] cloumns() default {};

    EventTypeEnum[] eventType() default EventTypeEnum.ALL;

    boolean primary() default true;

    String qualifier() default "";
}
