package com.galen.subscriber.client.netty;

import com.galen.subscriber.client.current.NamedThreadFactory;
import com.galen.subscriber.core.Objects;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private final static AtomicBoolean STARTING = new AtomicBoolean(false);

    private final static Object LOCK = new Object();

    private final static ThreadPoolExecutor CLIENT_EXECUTOR;

    static {
        CLIENT_EXECUTOR = new ThreadPoolExecutor(1, 1, 100L,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new NamedThreadFactory("subscriber client launcher"));
        // 拒绝策略，由当前线程执行任务
        CLIENT_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 开启线程
     *
     * @param config
     */
    public void connect(SubscriberConfig config) {
        checkConfig(config);
        CLIENT_EXECUTOR.execute(() -> {
            // 连接服务端
            try {
                this.connect(config.getPort(), config.getIp());
            } catch (InterruptedException e) {
                // ignore
            }
        });
    }

    private static void checkConfig(SubscriberConfig config) {
        Objects.isNotNull(config).or(StringUtils.isBlank(config.getIp())).or(null == config.getPort())
                .ifMatch(() -> {
                    throw new IllegalArgumentException("subscriber.server 未设置或设置不正确");
                });
    }

    public void connect(int port, String host) throws InterruptedException {
        synchronized (LOCK) {
            if (STARTING.get()) {
                log.warn("客户端已经启动。");
                return;
            }
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group).channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(this.initializer);
                ChannelFuture f = b.connect(host, port).sync();
                log.info("Subscriber Client启动成功");
                STARTING.compareAndSet(false, true);
                f.channel().closeFuture().sync();
            } finally {
                STARTING.compareAndSet(true, false);
                group.shutdownGracefully();
                // 客户端断开自动重连
                if (ClientConstant.running) {
                    CLIENT_EXECUTOR.execute(() -> {
                        try {
                            // 5s重连
                            TimeUnit.SECONDS.sleep(5);
                            log.info("客户端重连...");
                            this.connect(port, host);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    });
                }
            }
        }

    }

    @Override
    public void destroy() throws Exception {
        ClientConstant.changeRunningState(false);
        // 清空缓存
        ClientConstant.closeCtx();
        while (true) {
            try {
                // 关闭线程池
                CLIENT_EXECUTOR.shutdown();
                if (!CLIENT_EXECUTOR.awaitTermination(1, TimeUnit.SECONDS)) {
                    continue;
                }
                break;
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}
