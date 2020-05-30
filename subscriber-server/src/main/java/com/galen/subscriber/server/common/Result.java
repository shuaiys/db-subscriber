package com.galen.subscriber.server.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.common
 * @description 可以自定义返回结果
 * @date 2020-03-05 22:49
 */
@Data
@AllArgsConstructor
public class Result<T> {

    private int code;

    private String msg;

    private T data;

    private boolean success;

}
