package com.galen.subscriber.core;

import com.galen.subscriber.core.function.BooleanFunction;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author shuaiys
 * @date 2021/4/8 2:14 下午
 */
public class Objects {

    /**
     * 布尔条件标识
     */
    private final boolean flag;

    /**
     * 初始化true类型Objects对象
     */
    private final static Objects TRUE = new Objects(true);

    /**
     * 初始化false类型Objects对象
     */
    private final static Objects FALSE = new Objects(false);

    public Objects(boolean flag) {
        this.flag = flag;
    }

    /**
     * 校验参数是否为空，抛出系统错误
     *
     * @param parameter
     * @param remark
     * @return
     */
    public static <T> T notNull(@Nullable T parameter, String remark) {
        return notNull(parameter, () -> remark);
    }

    /**
     * 判断是否为空对象
     *
     * @param parameter 参数
     * @return
     */
    public static Objects isNotNull(Object parameter) {
        if (null == parameter) {
            return FALSE;
        }
        return TRUE;
    }

    /**
     * 增加了判断集合是否为空
     *
     * @param parameter
     * @return
     */
    public static Objects isNotEmpty(Object parameter) {
        if (checkNull(parameter)) {
            return TRUE;
        }
        return FALSE;
    }

    /**
     * 校验参数是否为空，抛出系统错误
     * 函数式
     *
     * @param parameter
     * @param remark
     * @param <T>
     * @return
     */
    public static <T> T notNull(@Nullable T parameter, Supplier<String> remark) {
        if (!checkNull(parameter)) {
            throw new IllegalArgumentException(remark.get());
        }
        return parameter;
    }

    /**
     * 校验参数是否为空，抛出系统错误
     *
     * @param parameter
     * @param <T>
     * @return
     */
    public static <T> T notNull(@Nullable T parameter) {
        if (!checkNull(parameter)) {
            throw new IllegalArgumentException("入参不能为空");
        }
        return parameter;
    }

    private static <T> boolean checkNull(@Nullable T parameter) {
        if (null == parameter) {
            return false;
        }
        if (parameter instanceof String && StringUtils.isBlank((String) parameter)) {
            return false;
        }
        if (parameter instanceof Collection<?> && ((Collection) parameter).isEmpty()) {
            return false;
        }
        if (parameter instanceof Map<?, ?> && ((Map) parameter).isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * equals比较
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean equals(Object a, Object b) {
        return java.util.Objects.equals(a, b);
    }

    /**
     * and 条件
     *
     * @param condition
     * @return
     */
    public Objects and(boolean condition) {
        if (this.flag && condition) {
            return TRUE;
        }
        return FALSE;
    }

    /**
     * and 条件
     *
     * @param function
     * @return
     */
    public Objects and(BooleanFunction function) {
        if (this.flag && function.apply()) {
            return TRUE;
        }
        return FALSE;
    }

    /**
     * or条件
     *
     * @param condition
     * @return
     */
    public Objects or(boolean condition) {
        if (this.flag || condition) {
            return TRUE;
        }
        return FALSE;
    }

    /**
     * 多个条件and
     *
     * @param conditions
     * @return
     */
    public static Objects allMatch(boolean ... conditions) {
        boolean f = true;
        for (boolean condition : conditions) {
            f = f && condition;
        }
        if (f) {
            return TRUE;
        }
        return FALSE;
    }

    /**
     * 多条件or
     *
     * @param conditions
     * @return
     */
    public static Objects anyMatch(boolean ... conditions) {
        boolean f = false;
        for (boolean condition : conditions) {
            f = f || condition;
        }
        if (f) {
            return TRUE;
        }
        return FALSE;
    }

    /**
     * 判断非空，符合则执行function
     *
     * @param t         第一个参数
     * @param function  当第一个参数满足非空时，执行此函数
     * @param <T>
     * @return          满足条件时，返回true的Objects对象，否则返回false的Objects对象
     */
    public static <T> Objects ifNotNull(T t, Runnable function) {
        if (null != t) {
            function.run();
            return TRUE;
        }
        return FALSE;
    }

    /**
     * 当Objects的flag为false时执行
     *
     * @param function  flag为false时，执行此函数
     */
    public void orElse(Runnable function) {
        if (!this.flag) {
            function.run();
        }
    }

    /**
     * 如果条件为false，则抛异常
     *
     * @param exceptionSupplier
     * @param <X>
     * @throws X
     */
    public <X extends Throwable> void orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (!this.flag) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 匹配函数，返回Objects对象
     * 成功匹配flag = true
     * 未匹配flag = false
     *
     * @param function  布尔类型返回值的函数
     * @return  满足条件时，返回true的Objects对象，否则返回false的Objects对象
     */
    public static Objects match(BooleanFunction function) {
        boolean ret = function.apply();
        if (TRUE.get() && ret) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    /**
     * 相当于else if
     *
     * @param function
     * @return
     */
    public  Objects orMatch(BooleanFunction function) {
        boolean ret = function.apply();
        if (TRUE.get() && ret) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    /**
     * 匹配并生成Objects对象
     *
     * @param condition
     * @return
     */
    public static Objects match(boolean condition) {
        if (TRUE.get() && condition) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    /**
     * 当flag为true时，执行函数
     *
     * @param function  无返回值类型函数
     * @return
     */
    public Objects ifMatch(Runnable function) {
        if (this.flag) {
            function.run();
            return this;
        }
        return FALSE;
    }

    /**
     * 如果匹配则执行逻辑
     *
     * @param condition 布尔条件
     * @param function  无参函数
     * @return
     */
    public static Objects ifMatch(boolean condition, Runnable function) {
        if (condition) {
            function.run();
            return TRUE;
        }
        return FALSE;
    }

    /**
     * 获取当前的状态值
     *
     * @return
     */
    public boolean get() {
        return this.flag;
    }
}
