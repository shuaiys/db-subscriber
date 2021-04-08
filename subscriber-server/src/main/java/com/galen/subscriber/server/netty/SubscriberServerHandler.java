package com.galen.subscriber.server.netty;

import com.galen.subscriber.core.proto.SubscriberInfoProto;
import com.galen.subscriber.server.canal.CanalClient;
import com.galen.subscriber.server.common.SubscribeTableCenter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server.netty
 * @description TODO
 * @date 2020-05-21 22:11
 */
@Slf4j
public class SubscriberServerHandler extends SimpleChannelInboundHandler<SubscriberInfoProto.SubscriberBody> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端[{}]连接成功。", ctx.channel().remoteAddress());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        disConnect(ctx);
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SubscriberInfoProto.SubscriberBody body) throws Exception {
        if (body.hasAck()) {
            this.handleAck(ctx, body.getAck().getAck());
        }
        if (body.hasRb()) {
            this.handleRegister(ctx, body.getRb());
        }
        ctx.fireChannelRead(body);
    }

    /**
     * 1、注册订阅表到订阅中心 {@link SubscribeTableCenter}
     * 2、刷新canal的订阅表
     * @param ctx
     * @param rb
     */
    private void handleRegister(ChannelHandlerContext ctx, SubscriberInfoProto.RegisterBody rb) {
        Map<String, String> registerTableMap = rb.getRegisterTableMap();

        if (MapUtils.isNotEmpty(registerTableMap)) {
            // 注册到订阅中心
            SubscribeTableCenter.register(ctx.channel(), registerTableMap, rb.getAppId());
            // 更新canal server订阅表
            CanalClient.freshCanalSubscribe();
        }
    }

    /**
     * 处理ack信息
     * @param ctx
     * @param ack
     */
    private void handleAck(ChannelHandlerContext ctx, SubscriberInfoProto.ACK ack) {

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
        ctx.fireChannelReadComplete();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        disConnect(ctx);
        ctx.fireExceptionCaught(cause);
    }

    /**
     * 断开连接，清除缓存
     * @param ctx
     */
    private static void disConnect(ChannelHandlerContext ctx){
        // 移除注册信息
        String appId = SubscribeTableCenter.geAppId(ctx.channel());
        SubscribeTableCenter.unRegister(ctx.channel());
        // 更新canal server订阅表
        CanalClient.freshCanalSubscribe();
        log.info("客户端[{}]断开连接，appId:{}。", ctx.channel().remoteAddress(), appId);
    }
}
