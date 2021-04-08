package com.galen.subscriber.core.util;

import com.galen.subscriber.core.Exchange;
import com.galen.subscriber.core.MysqlTypeConverter;
import com.galen.subscriber.core.proto.SubscriberInfoProto;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core.util
 * @description proto body转换器
 * @date 2020-05-25 23:02
 */
public class BodyConverter {
    /**
     * protobuf Obj转换成 {@link Exchange}
     * @param exchange
     * @return
     */
    public static Exchange proto2Exchange(SubscriberInfoProto.Exchange exchange) {
        Exchange e = new Exchange();
        ProtocolStringList updateColumnsList = exchange.getUpdateColumnsList();
        Set<String> collect = new HashSet<>(updateColumnsList);
        e.setDatabase(exchange.getDatabase())
                .setEventType(exchange.getEventType())
                .setTableName(exchange.getTableName())
                .setExecuteTime(exchange.getExecuteTime())
                .setUpdateColumns(collect)
                .setBeforeColumns(unPackFromAny(exchange.getBeforeColumnsMap()))
                .setAfterColumns(unPackFromAny(exchange.getAfterColumnsMap()));

        return e;
    }

    /**
     * {@link Exchange} 转换成 protobuf Obj
     * @param exchange
     * @return
     */
    public static SubscriberInfoProto.Exchange exchange2Proto(Exchange exchange) {
        SubscriberInfoProto.Exchange.Builder builder = SubscriberInfoProto.Exchange.newBuilder();
        builder.setEventType(exchange.getEventType()).setExecuteTime(exchange.getExecuteTime());
        builder.setDatabase(exchange.getDatabase()).setTableName(exchange.getTableName());
        builder.putAllAfterColumns(packAnyFromObj(exchange.getAfterColumns()));
        builder.putAllBeforeColumns(packAnyFromObj(exchange.getBeforeColumns()));
        builder.addAllUpdateColumns(exchange.getUpdateColumns());
        return builder.build();
    }

    /**
     * 将Object 转换成Any
     * @param map
     * @return
     */
    public static Map<String, Any> packAnyFromObj(Map<String, Object> map) {
        Map<String, Any> anyMap = new HashMap<>(map.size());
        if (MapUtils.isNotEmpty(map)) {
            map.forEach((k, v) -> {
                Any any = Any.newBuilder().setTypeUrl(v.getClass().getCanonicalName()).setValue(ByteString.copyFromUtf8(String.valueOf(v))).build();
                anyMap.put(k, any);
            });
        }

        return anyMap;
    }

    /**
     * 将Any转换成Object
     * @param anyMap
     * @return
     */
    public static Map<String, Object> unPackFromAny(Map<String, Any> anyMap) {
        Map<String, Object> ret = new HashMap<>(anyMap.size());
        if (MapUtils.isNotEmpty(anyMap)) {
            anyMap.forEach((k, v) -> {
                String typeUrl = v.getTypeUrl();
                Class<?> aClass = String.class;
                try {
                    aClass = Class.forName(typeUrl);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                String value = v.getValue().toStringUtf8();
                // 转换成具体的类型，时间类型使用LocalDateTime
                Object val = MysqlTypeConverter.castValue(value, aClass);
                ret.put(k, val);
            });
        }
        return ret;
    }

}
