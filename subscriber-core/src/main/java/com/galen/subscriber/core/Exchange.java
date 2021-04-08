package com.galen.subscriber.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core
 * @description 传递的消息体
 * @date 2020-05-18 22:34
 */
@Data
@Accessors(chain = true)
public class Exchange implements Serializable {
    private static final long serialVersionUID = 4535320077292222744L;

    /**
     * 数据库名
     */
    private String database;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 1-INSERT、2-UPDATE、3-DELETE
     */
    private int eventType;

    /**
     * 更新之前的字段
     */
    private Map<String, Object> beforeColumns = new HashMap<>();

    /**
     * 更新之后的字段
     */
    private Map<String, Object> afterColumns = new HashMap<>();

    /**
     * 更新的字段
     */
    private Set<String> updateColumns = new HashSet<>();

    /**
     * 操作执行的时间戳
     */
    private long executeTime;

}
