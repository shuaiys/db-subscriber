package com.galen.subscriber.client.netty;

import com.galen.subscriber.core.SubscriberConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client.netty
 * @description netty客户端
 * @date 2020-05-20 22:17
 */
@Slf4j
@Component
public class SubscriberNettyClient implements DisposableBean {

    @Resource
    private SubscriberChannelInitializer initializer;

    private volatile boolean start = false;

    private static Object lock = new Object();

    public void connect(SubscriberConfig config) {
        checkConfig(config);
        ThreadPoolExecutor executor = ClientConstant.executor;
        executor.execute(() -> {
            // 连接服务端
            try {
                this.connect(config.getPort(), config.getIp());
            } catch (InterruptedException e) {
                // ignore
            }
        });
    }

    private void checkConfig(SubscriberConfig config) {
        if (null == config || StringUtils.isBlank(config.getIp()) || null == config.getPort()) {
            throw new IllegalArgumentException("subscriber.server 未设置或设置不正确");
        }
    }

    public void connect(int port, String host) throws InterruptedException {
        synchronized (this.lock) {
            if (this.start) {
                log.warn("客户端已经启动。");
                return;
            }
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group).channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(initializer);
                ChannelFuture f = b.connect(host, port).sync();
                log.info("Subscriber Client启动成功");
                this.start = true;
                f.channel().closeFuture().sync();
            } finally {
                this.start = false;
                group.shutdownGracefully();
                // 客户端断开自动重连
                if (ClientConstant.isRun) {
                    ClientConstant.executor.execute(() -> {
                        try {
                            // 5s重连
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                        try {
                            log.info("客户端重连...");
                            connect(port, host);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }

    }

    @Override
    public void destroy() throws Exception {
        // 关闭管道
        ClientConstant.closeCtx();
    }
}
