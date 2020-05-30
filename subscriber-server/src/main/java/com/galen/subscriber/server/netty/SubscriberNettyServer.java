package com.galen.subscriber.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server.netty
 * @description Subscriber Netty Server
 * @date 2020-05-20 22:29
 */
@Slf4j
public class SubscriberNettyServer {

    private final static int PORT = 8888;

    public static void run() throws InterruptedException {
        run(PORT);
    }

    public static void run(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new SubscriberServerChannelInitializer());
            ChannelFuture f = b.bind(port).sync();
            log.info("netty Server启动成功");
            f.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
