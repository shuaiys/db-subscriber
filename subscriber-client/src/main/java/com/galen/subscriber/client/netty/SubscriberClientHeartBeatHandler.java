package com.galen.subscriber.client.netty;

import com.galen.subscriber.core.proto.SubscriberInfoProto;
import com.galen.subscriber.core.util.BodyFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client.netty
 * @description 心跳
 * @date 2020-05-25 23:36
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class SubscriberClientHeartBeatHandler extends SimpleChannelInboundHandler<SubscriberInfoProto.SubscriberBody> implements Runnable {

    private volatile ScheduledFuture<?> heartBeat;

    private volatile Channel channel;

    private static final String PING = "PING";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        // 激活成功，客户端主动发送心跳
        this.heartBeat = ctx.executor().scheduleAtFixedRate(this, 0, 5000, TimeUnit.MILLISECONDS);

        ctx.fireChannelActive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SubscriberInfoProto.SubscriberBody body) throws Exception {
        String msg = body.getHb().getMsg();
        log.debug(msg);
    }

    /**
     * 定时发送心跳
     */
    @Override
    public void run() {
        SubscriberInfoProto.SubscriberBody heartBeatBody = BodyFactory.buildHeartBeatBody(PING);
        this.channel.writeAndFlush(heartBeatBody);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
        ctx.fireChannelReadComplete();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常退出心跳
        if (this.heartBeat != null) {
            this.heartBeat.cancel(true);
            this.heartBeat = null;
        }
        ctx.close();
        log.debug("心跳断开.");
    }
}
