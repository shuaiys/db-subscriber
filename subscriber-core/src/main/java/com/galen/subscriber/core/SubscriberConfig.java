package com.galen.subscriber.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core
 * @description 客户端配置类
 * @date 2020-05-20 22:08
 */
@Data
@Component
@ConfigurationProperties(prefix = "subscriber.server")
public class SubscriberConfig implements Serializable {

    private static final long serialVersionUID = 165823883547758212L;

    private String ip;

    private Integer port;

    private String appId;
}
