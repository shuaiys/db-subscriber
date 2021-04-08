package com.galen.subscriber.server.configuration;

import com.alibaba.otter.canal.common.utils.NamedThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author shuaiys
 * @version 1.0
 * @package co.lvita.subscriber.configuration
 * @description 初始化一个线程池
 * @date 2020-03-04 21:58
 */
public final class ThreadPoolConfiguration {

    private final static Integer CORE_POOL_SIZE = 5;
    private final static Integer MAXIMUM_POOL_SIZE = 10;
    private final static Long KEEP_ALIVE_TIME = 100L;
    private final static Integer CAPACITY = 500;
    public static ThreadPoolExecutor executor;
    static {
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(CAPACITY), new NamedThreadFactory("subscribe-pool"));
    }

    private ThreadPoolConfiguration() {
    }
}
