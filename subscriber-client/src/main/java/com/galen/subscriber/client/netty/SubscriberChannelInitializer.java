package com.galen.subscriber.client.netty;

import com.galen.subscriber.core.proto.SubscriberInfoProto;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client.netty
 * @description 客户端Initializer
 * @date 2020-05-20 22:33
 */
@Component
public class SubscriberChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private SubscriberClientHandler clientHandler;

    @Resource
    private SubscriberClientHeartBeatHandler heartBeatHandler;

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        // 使用protobuf编解码
        sc.pipeline().addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(SubscriberInfoProto.SubscriberBody.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                // 50s内没有应答，断开连接
                .addLast(new ReadTimeoutHandler(50))
                // 自定义handler
                .addLast(clientHandler)
                .addLast(heartBeatHandler);
    }
}
