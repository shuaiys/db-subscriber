package com.galen.subscriber.server.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.galen.subscriber.server.event.CanalEvent;
import com.galen.subscriber.server.filter.CanalExchange;
import com.galen.subscriber.server.filter.chain.CanalFilterChain;
import com.galen.subscriber.server.filter.chain.DefaultCanalFilterChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.listener
 * @description canal event 事件监听器
 * @date 2020-03-05 22:29
 */
@Component
@Slf4j
public class CanalListener implements ApplicationListener<CanalEvent> {

    /**
     * 订阅监听方法，不需要保证顺序时可开启异步
     * @param event
     */
    @Override
//    @Async
    public void onApplicationEvent(CanalEvent event) {
        CanalEntry.Entry entry = event.getEntry();
        // 创建责任链
        CanalExchange exchange = new CanalExchange();
        exchange.setEntry(entry);
        CanalFilterChain filterChain = new DefaultCanalFilterChain(event.getFilters());
        filterChain.filter(exchange);
    }

}
