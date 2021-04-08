package com.galen.subscriber.server.common.current;

import lombok.Data;

/**
 * @author shuaiys
 * @date 2021/4/7 5:48 下午
 */
@Data
public class RunnableFutureTask {

    private Runnable runnable;

    private RunnableFutureCallback callback;
}
