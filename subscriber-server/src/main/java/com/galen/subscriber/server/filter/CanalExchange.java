package com.galen.subscriber.server.filter;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.galen.subscriber.core.ChangeDataEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.filter
 * @description 责任链传递的对象
 * @date 2020-03-05 21:51
 */
@Data
public class CanalExchange implements Serializable {
    private static final long serialVersionUID = -6231537501536712582L;

    private CanalEntry.Entry entry;

    private String database;

    private String tableName;

    private CanalEntry.EventType eventType;

    private List<ChangeDataEntity> data = new ArrayList<>();

    /**
     * 保留字段
     */
    private Map<String, Object> params;

    private long executeTime;
}
