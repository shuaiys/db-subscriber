package com.galen.subscriber.server.server;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.galen.subscriber.server.canal.CanalClient;
import com.galen.subscriber.server.configuration.CanalBeanPostProcessor;
import com.galen.subscriber.server.event.CanalEvent;
import com.galen.subscriber.server.filter.CanalFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
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
public class SubscriberCanalServer implements ApplicationRunner {

    @Resource
    private ApplicationEventPublisher publisher;

    private final static List<CanalFilter> filters = new ArrayList<>();

    public static void init() {
        filters.addAll(CanalBeanPostProcessor.FILTERS);
        // 按照ordered接口排序
        filters.sort(OrderComparator.INSTANCE);
    }

    /**
     * 单次从canal server获取的数量
     */
    private static final Integer MSG_SIZE = 100;

    @Scheduled(fixedDelay = 1000)
    public void run() {
        Message message = CanalClient.connector.getWithoutAck(MSG_SIZE);
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
                                log.error("事件消费失败", e);
                            }
                        });
            }
            CanalClient.connector.ack(batchId);
        } catch (Exception e) {
            CanalClient.connector.rollback(batchId);
            log.error("发生异常，canal batchId 回滚， batchId={}", batchId, e);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
    }
}