package com.galen.subscriber.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core
 * @description TODO
 * @date 2020-05-20 23:15
 */
@Getter
@AllArgsConstructor
public enum BodyTypeEnum {

    Ack(0, "ack"),
    Register(1, "注册订阅信息"),
    Subscribe(2, "订阅内容"),
    HeartBeat(3, "心跳"),

    ;

    public Integer type;

    public String remark;

}
