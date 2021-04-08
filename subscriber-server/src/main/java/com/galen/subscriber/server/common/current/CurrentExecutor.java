package com.galen.subscriber.server.common.current;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

/**
 * @author shuaiys
 * @date 2021/4/7 5:50 下午
 */
public class CurrentExecutor {

    public static void execVoid(List<RunnableFutureTask> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }

        CountDownLatch cdl = new CountDownLatch(tasks.size());
        for (RunnableFutureTask task : tasks) {
            CompletableFuture.runAsync(task.getRunnable())
                    .whenCompleteAsync(new BiConsumer<Void, Throwable>() {
                        @Override
                        public void accept(Void unused, Throwable throwable) {
                            if (task.getCallback() == null) {
                                return;
                            }
                            if (throwable == null) {
                                task.getCallback().success();
                            } else {
                                task.getCallback().fail(throwable);
                            }
                            cdl.countDown();
                        }
                    });
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            //ignore
        }
    }
}
