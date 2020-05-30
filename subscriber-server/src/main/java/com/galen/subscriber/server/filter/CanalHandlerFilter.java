package com.galen.subscriber.server.filter;

import com.galen.subscriber.core.ChangeDataEntity;
import com.galen.subscriber.core.proto.SubscriberInfoProto;
import com.galen.subscriber.core.util.BodyConverter;
import com.galen.subscriber.core.util.BodyFactory;
import com.galen.subscriber.server.common.Result;
import com.galen.subscriber.server.common.SubscribeTableCenter;
import com.galen.subscriber.server.configuration.ThreadPoolConfiguration;
import com.galen.subscriber.server.filter.chain.CanalFilterChain;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server.filter
 * @description 发送订阅消息到client端
 * @date 2020-05-26 21:51
 */
@Component
@Slf4j
public class CanalHandlerFilter implements CanalFilter, Ordered {

    private static final String RELATION = "##";


    @Override
    public Result filter(CanalExchange exchange, CanalFilterChain chain) {
        String subscribe = getSubscribe(exchange);
        log.info("接收到订阅信息：{}", subscribe);
        Set<Channel> channels = SubscribeTableCenter.listChannelBySub(subscribe);
        List<SubscriberInfoProto.Exchange> exs = new ArrayList<>();
        exchange.getData().forEach(d -> {
            SubscriberInfoProto.Exchange e = buildProtoExchange(exchange, d);
            exs.add(e);
        });
        sendSubscribeBody(channels, exs, subscribe);

        return chain.filter(exchange);
    }

    /**
     * 获取msgID，db.table_id
     * @param exchange
     * @param d
     * @return
     */
    private static String getSubscribeMsgId(CanalExchange exchange, ChangeDataEntity d) {
        String subscribe = getSubscribe(exchange);
        if (!d.getAfterColumns().isEmpty()) {
            return subscribe + RELATION + d.getAfterColumns().get("id");
        } else {
            return subscribe + RELATION + d.getBeforeColumns().get("id");
        }
    }

    /**
     * 发送消息给订阅者
     * @param channels
     * @param exs
     * @param subscribe
     */
    private void sendSubscribeBody(Set<Channel> channels, List<SubscriberInfoProto.Exchange> exs, String subscribe) {
        if (!channels.isEmpty()) {
            ThreadPoolExecutor executor = ThreadPoolConfiguration.executor;
            channels.forEach(channel -> {
                Set<String> beans = SubscribeTableCenter.listBeanAliasByChannel(channel, subscribe);
                // 开启多个线程执行
                executor.execute( () -> exs.forEach(exchange -> {
                    SubscriberInfoProto.SubscriberBody subscriberBody = BodyFactory.buildSubscribeBody(beans, exchange);
                    channel.writeAndFlush(subscriberBody);
                }));
            });
        }
    }

    private static String getSubscribe(CanalExchange exchange) {
        String database = exchange.getDatabase();
        String tableName = exchange.getTableName();
        String subscribe = database + "." + tableName;
        return subscribe;
    }

    private static SubscriberInfoProto.Exchange buildProtoExchange(CanalExchange exchange, ChangeDataEntity d) {
        SubscriberInfoProto.Exchange.Builder builder = SubscriberInfoProto.Exchange.newBuilder();
        builder.setTableName(exchange.getTableName())
                .setDatabase(exchange.getDatabase())
                .setExecuteTime(exchange.getExecuteTime())
                .setEventType(exchange.getEventType().getNumber())
                .putAllBeforeColumns(BodyConverter.packAnyFromObj(d.getBeforeColumns()))
                .putAllAfterColumns(BodyConverter.packAnyFromObj(d.getAfterColumns()))
                .addAllUpdateColumns(d.getUpdateColumns())
                .setId(getSubscribeMsgId(exchange, d));
        return builder.build();
    }

    @Override
    public int getOrder() {
        return FilterOrderConstant.HANDLER;
    }
}
