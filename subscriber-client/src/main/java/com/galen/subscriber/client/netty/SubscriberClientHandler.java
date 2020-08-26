package com.galen.subscriber.client.netty;

import com.galen.subscriber.client.DataSync;
import com.galen.subscriber.core.ACK;
import com.galen.subscriber.core.BodyTypeEnum;
import com.galen.subscriber.core.body.ACKBody;
import com.galen.subscriber.core.proto.SubscriberInfoProto;
import com.galen.subscriber.core.util.BodyConverter;
import com.galen.subscriber.core.util.BodyFactory;
import com.google.protobuf.ProtocolStringList;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client.netty
 * @description 客户端handler
 * @date 2020-05-20 22:42
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class SubscriberClientHandler extends SimpleChannelInboundHandler<SubscriberInfoProto.SubscriberBody> implements ApplicationContextAware {

    /**
     * 引入spring的上下文，用于调用{@link DataSync}类型bean的同步方法
     */
    private ApplicationContext applicationContext;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 缓存上下文
        ClientConstant.ctx = ctx;
        // 发送注册表
        SubscriberInfoProto.SubscriberBody subscriberBody = BodyFactory.buildRegisterBody(ClientConstant.subscribeMap, ClientConstant.appId);
        ctx.writeAndFlush(subscriberBody);
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SubscriberInfoProto.SubscriberBody body) throws Exception {
        // 处理订阅消息
        if (body.getTypeValue() == BodyTypeEnum.Subscribe.type) handleSubscribe(ctx, body.getSb());
        ctx.fireChannelRead(body);
    }

    /**
     * 处理订阅消息
     * @param ctx
     * @param sb
     */
    private void handleSubscribe(ChannelHandlerContext ctx, SubscriberInfoProto.SubscribeBody sb) {
        ProtocolStringList beanAliasList = sb.getBeanAliasList();
        // 使用parallelStream将消息分发到具体的bean
        beanAliasList.stream().forEach(s -> ClientConstant.executor.execute(() -> {
            Object bean = this.applicationContext.getBean(s);
            if (bean != null) {
                DataSync ds = (DataSync) bean;
                ds.doSync(BodyConverter.proto2Exchange(sb.getExchange()));
                // 无需发送ack，交给代理对象
                // ctx.channel().writeAndFlush(new ACKBody(ack));
            } else {
                log.error("未找到bean：{}", s);
            }
        }));

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 发生异常，清空上下文缓存，等待重连
        ClientConstant.ctx = null;
        ctx.fireExceptionCaught(cause);
    }
}
