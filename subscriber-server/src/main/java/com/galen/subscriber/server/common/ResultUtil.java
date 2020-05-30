package com.galen.subscriber.server.common;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.common
 * @description 简单封装结果
 * @date 2020-03-05 22:06
 */
public class ResultUtil {

    public static Result setOk() {
        return setOk(null);
    }

    public static Result setOk(Object data) {
        return new Result<>(200, "成功", data, true);
    }

    public static Result setError(String msg) {
        return new Result<>(300, msg, "", false);
    }
}
