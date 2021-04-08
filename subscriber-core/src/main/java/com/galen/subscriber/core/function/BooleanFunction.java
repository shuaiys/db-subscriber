package com.galen.subscriber.core.function;

import com.galen.subscriber.core.Objects;

/**
 * @author shuaiys
 * @date 2021/4/8 2:13 下午
 */
@FunctionalInterface
public interface BooleanFunction {

    /**
     * 无入参，返回布尔值
     *
     * @return
     */
    boolean apply();

    /**
     * 继续执行后续方法
     *
     * @param function
     * @return
     */
    default BooleanFunction andThen(BooleanFunction function) {
        Objects.notNull(function);
        return () -> function.apply();
    }

}
