package com.galen.subscriber.client;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description 订阅类实例化时会发送订阅数据
 * @date 2020-05-19 21:57
 */
@Data
public class SubscribeEvent extends ApplicationEvent {

    private String db;

    private String table;

    private String name;

    private String contextId;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public SubscribeEvent(Object source) {
        super(source);
    }

    public SubscribeEvent(Object source, String db, String table, String name, String contextId) {
        super(source);
        this.db = db;
        this.table = table;
        this.name = name;
        this.contextId = contextId;
    }
}
