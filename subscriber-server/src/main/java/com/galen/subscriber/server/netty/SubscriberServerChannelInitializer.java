package com.galen.subscriber.server.netty;

import com.galen.subscriber.core.proto.SubscriberInfoProto;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server.netty
 * @description SubscriberServerChannelInitializer
 * @date 2020-05-21 22:02
 */
public class SubscriberServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        sc.pipeline().addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(SubscriberInfoProto.SubscriberBody.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                // 50s内没有读操作，断开连接
                .addLast(new ReadTimeoutHandler(10))
                .addLast(new SubscriberServerHandler())
                .addLast(new SubscriberServerHeartBeatHandler());
    }
}
