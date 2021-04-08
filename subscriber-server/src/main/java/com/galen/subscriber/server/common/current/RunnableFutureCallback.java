package com.galen.subscriber.server.common.current;

/**
 * @author shuaiys
 * @date 2021/4/7 5:49 下午
 */
public interface RunnableFutureCallback {

    /**
     * 正常回调
     *
     */
    void success();

    /**
     * 异常处理
     *
     * @param throwable 异常
     */
    void fail(Throwable throwable);
}
