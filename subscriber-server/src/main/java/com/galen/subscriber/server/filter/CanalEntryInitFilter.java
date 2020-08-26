package com.galen.subscriber.server.filter;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.galen.subscriber.core.ChangeDataEntity;
import com.galen.subscriber.core.MysqlTypeConverter;
import com.galen.subscriber.server.common.Result;
import com.galen.subscriber.server.common.ResultUtil;
import com.galen.subscriber.server.filter.chain.CanalFilterChain;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.galen.subscriber.server.filter.FilterOrderConstant.INIT;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.filter
 * @description 实现 {@link PriorityOrdered} 接口,将会优先于{@link Ordered}接口被执行。初始化entry信息。
 * @date 2020-03-05 21:36
 */
@Component
@Slf4j
public class CanalEntryInitFilter implements CanalFilter, PriorityOrdered {
    @Override
    public int getOrder() {
        // 优先级最高，最先执行
        return FilterOrderConstant.PERSIST;
    }

    @Override
    public Result filter(CanalExchange exchange, CanalFilterChain chain) {
        Entry entry = exchange.getEntry();
        if (null == entry) {
            return ResultUtil.setError("entry 为空");
        }
        // 将库表信息设置到exchange，传递到链路
        String database = entry.getHeader().getSchemaName();
        String table = entry.getHeader().getTableName();
        long executeTime = entry.getHeader().getExecuteTime();
        if (StringUtils.isBlank(database) || StringUtils.isBlank(table)) {
            log.error("初始化失败，database or table is blank");
            return ResultUtil.setError("初始化失败");
        }
        exchange.setDatabase(database);
        exchange.setTableName(table);
        exchange.setExecuteTime(executeTime);
        RowChange change;
        try {
            change = RowChange.parseFrom(entry.getStoreValue());
            exchange.setEventType(change.getEventType());
            setColumns(change, exchange);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            log.error("获取RowChange失败！");
        }
        if (exchange.getData().isEmpty()) {
            log.debug("Entry为空，已丢弃");
            return ResultUtil.setError("初始化失败");
        }
        log.debug("Entry初始化完成, db:{}, table:{}", database, table);
        return chain.filter(exchange);
    }

    private void setColumns(RowChange change, CanalExchange exchange) {
        int eventType = exchange.getEventType().getNumber();
        change.getRowDatasList().forEach(r -> {

            ChangeDataEntity entity = new ChangeDataEntity();
            if (EventType.DELETE.getNumber() == eventType) {
                // 删除之前的数据
                entity.setBeforeColumns(parseColumnsToMap(r.getBeforeColumnsList()));
            } else if (EventType.INSERT.getNumber() == eventType) {
                // 插入之后的数据
                entity.setAfterColumns(parseColumnsToMap(r.getAfterColumnsList()));
            } else {
                // 修改之前和之后的数据
                entity.setBeforeColumns(parseColumnsToMap(r.getBeforeColumnsList()));
                entity.setAfterColumns(parseColumnsToMap(r.getAfterColumnsList()));
                // 存储修改过的字段名
                entity.setUpdateColumns(updateColumns(r.getAfterColumnsList()));
            }
            exchange.getData().add(entity);
        });
    }

    private Map<String, Object> parseColumnsToMap(List<CanalEntry.Column> columns) {
        Map<String, Object> jsonMap = new HashMap<>();
        columns.stream().filter(column -> column != null).forEach(column -> jsonMap.put(column.getName(), MysqlTypeConverter.convert(column.getMysqlType(), column.getValue())));
        return jsonMap;
    }

    private Set<String> updateColumns(List<CanalEntry.Column> columns) {
        Set<String> updates = columns.stream()
                .filter(column -> column != null && column.getUpdated())
                .map(column -> column.getName())
                .collect(Collectors.toSet());
        return updates;
    }
}
