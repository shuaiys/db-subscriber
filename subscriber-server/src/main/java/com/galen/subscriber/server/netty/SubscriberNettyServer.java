package com.galen.subscriber.server.netty;

import com.alibaba.otter.canal.common.utils.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server.netty
 * @description Subscriber Netty Server
 * @date 2020-05-20 22:29
 */
@Slf4j
@Component
public class SubscriberNettyServer implements ApplicationRunner, DisposableBean {

    private final static int PORT = 8888;

    /**
     * 主线程池组
     */
    private static final EventLoopGroup BOSS_GROUP;
    /**
     * 工作线程池组
     */
    private static final EventLoopGroup WORKER_GROUP;

    /**
     * socket启动线程池，1个线程
     */
    private static final Executor EXECUTOR;

    static {
        BOSS_GROUP = new NioEventLoopGroup();
        WORKER_GROUP = new NioEventLoopGroup();
        EXECUTOR = new ThreadPoolExecutor(1, 1, 100L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new NamedThreadFactory("subscriber-server-Launcher"));
    }

    public static void run() {
        try {
            run(PORT);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public static void run(int port) throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.group(BOSS_GROUP, WORKER_GROUP)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new SubscriberServerChannelInitializer());
        ChannelFuture f = b.bind(port).sync();
        log.info("netty Server启动成功");
        f.channel().closeFuture().sync();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        EXECUTOR.execute(() -> run());
    }

    @Override
    public void destroy() throws Exception {
        BOSS_GROUP.shutdownGracefully();
        WORKER_GROUP.shutdownGracefully();
        log.info("socket thread group shutdown...");
    }
}
