package com.galen.subscriber.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core
 * @description 数据库事件的类型
 * @date 2020-05-28 10:19
 */
@Getter
@AllArgsConstructor
public enum EventTypeEnum {

    ALL(0, "所有"),
    INSERT(1, "新增"),
    UPDATE(2, "更新"),
    DELETED(3, "删除"),

    ;

    /**
     * 事件类型
     */
    public Integer type;

    /**
     * 时间描述
     */
    public String remark;

}
