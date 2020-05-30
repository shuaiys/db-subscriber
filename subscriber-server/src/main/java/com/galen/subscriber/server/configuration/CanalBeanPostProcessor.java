package com.galen.subscriber.server.configuration;

import com.galen.subscriber.server.filter.CanalFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.configuration
 * @description 在bean初始化之后，将CanalFilter加入filter集合
 * @date 2020-03-05 22:13
 */
@Component
@Slf4j
public class CanalBeanPostProcessor implements BeanPostProcessor {

    // filter集合
    public final static List<CanalFilter> filters = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CanalFilter) {
            // canal filter将会被加入filters
            filters.add((CanalFilter) bean);
            log.info("canal filter [{}] added to chain", beanName);
        }
        return bean;
    }

}
