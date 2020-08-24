package com.galen.subscriber.server.server;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.galen.subscriber.server.configuration.CanalBeanPostProcessor;
import com.galen.subscriber.server.event.CanalEvent;
import com.galen.subscriber.server.filter.CanalFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.OrderComparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server
 * @description SubscriberCanalServer
 * @date 2020-03-08 22:53
 */
@Component
@Slf4j
public class SubscriberCanalServer implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private CanalConnector connector;

    @Resource
    private ApplicationEventPublisher publisher;

    @Resource
    private CanalBeanPostProcessor postProcessor;

    private final static List<CanalFilter> filters = new ArrayList<>();

    public void init() {
        filters.addAll(postProcessor.filters);
        // 按照ordered接口排序
        filters.sort(OrderComparator.INSTANCE);
    }

    // 单次从canal server获取的数量
    private static Integer MSG_SIZE = 100;

    @Scheduled(fixedDelay = 1000)
    public void run() {
        Message message = connector.getWithoutAck(MSG_SIZE);
        long batchId = message.getId();
        try {
            List<CanalEntry.Entry> entries = message.getEntries();
            // 空集合直接跳过
            if (batchId != -1L && entries.size() > 0) {
                entries.parallelStream()
                        .filter(entry -> entry.getEntryType().getNumber() == CanalEntry.EntryType.ROWDATA.getNumber())
                        .forEachOrdered(entry -> {
                            try {
                                publisher.publishEvent(new CanalEvent(entry, filters));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
            }
            connector.ack(batchId);
        } catch (Exception e) {
            e.printStackTrace();
            connector.rollback(batchId);
            log.error("发生异常，canal batchId 回滚， batchId={}", batchId);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // spring 容器加载完成之后执行filters的初始化
        if (event.getApplicationContext().getParent() == null) {
            init();
        }
    }
}