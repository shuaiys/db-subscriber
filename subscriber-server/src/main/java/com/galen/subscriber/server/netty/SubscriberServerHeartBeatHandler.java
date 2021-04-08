package com.galen.subscriber.server.netty;

import com.galen.subscriber.core.proto.SubscriberInfoProto;
import com.galen.subscriber.core.util.BodyFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server.netty
 * @description 心跳服务
 * @date 2020-05-25 22:21
 */
@Slf4j
public class SubscriberServerHeartBeatHandler extends SimpleChannelInboundHandler<SubscriberInfoProto.SubscriberBody> {

    private final static String PONG = "PONG";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SubscriberInfoProto.SubscriberBody body) throws Exception {
        if (body.hasHb()) {
            this.handlerHeartBeat(ctx, body.getHb());
        }
    }

    private void handlerHeartBeat(ChannelHandlerContext ctx, SubscriberInfoProto.HeartBate hb) {
        // 接收到心跳，发送回应
        SubscriberInfoProto.SubscriberBody heartBeatBody = BodyFactory.buildHeartBeatBody(PONG);
        ctx.writeAndFlush(heartBeatBody);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}
