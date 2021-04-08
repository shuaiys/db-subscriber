package com.galen.subscriber.client.netty;

import com.galen.subscriber.client.current.NamedThreadFactory;
import com.galen.subscriber.core.Objects;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client.netty
 * @description 客户端常量类
 * @date 2020-05-20 22:17
 */
@Slf4j
public final class ClientConstant {

    /**
     * channel上下文
     */
    public static ChannelHandlerContext ctx;

    public final static Integer CORE_POOL_SIZE = 5;
    public final static Integer MAXIMUM_POOL_SIZE = 10;
    public final static Long KEEP_ALIVE_TIME = 100L;
    private final static Integer CAPACITY = 500;
    public static ThreadPoolExecutor executor;
    private final static Object LOCK = new Object();

    /**
     * 初始化一个线程池
     */
    static {
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(CAPACITY), new NamedThreadFactory("subscribe-pool"));
    }

    /**
     * 存放客户端订阅表
     */
    public static Map<String, String> subscribeMap;

    /**
     * 客户端注册的appId
     */
    public static String appId;

    /**
     * 用来标注客户端是否与服务端连接通
     */
    public static volatile boolean running = false;

    private ClientConstant() {
    }

    /**
     * 关闭netty channel上下文
     */
    public static void closeCtx() {
        ChannelHandlerContext ctx = ClientConstant.ctx;
        Objects.isNotNull(ctx).and(ctx.channel().isActive()).ifMatch(() -> {
            ctx.fireExceptionCaught(new ClosedChannelException());
            ctx.close();
            log.info("Subscriber Client成功断开");
        });
    }

    public static void changeRunningState(boolean state) {
        if (state != running) {
            synchronized (LOCK) {
                running = state;
            }
        }
    }
}
