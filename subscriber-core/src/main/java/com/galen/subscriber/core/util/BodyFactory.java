package com.galen.subscriber.core.util;

import com.galen.subscriber.core.proto.SubscriberInfoProto;
import com.google.protobuf.ProtocolStringList;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core.util
 * @description proto body工厂
 * @date 2020-05-25 23:33
 */
public class BodyFactory {

    /**
     * 构建订阅表注册消息
     * @param registerTable
     * @param appId
     * @return
     */
    public static SubscriberInfoProto.SubscriberBody buildRegisterBody(Map<String, String> registerTable, String appId) {
        SubscriberInfoProto.SubscriberBody.Builder builder = SubscriberInfoProto.SubscriberBody.newBuilder()
                .setType(SubscriberInfoProto.SubscriberBody.DataType.Register)
                .setClassType(SubscriberInfoProto.RegisterBody.class.getCanonicalName());
        return builder.setRb(SubscriberInfoProto.RegisterBody.newBuilder().setAppId(appId).putAllRegisterTable(registerTable).build()).build();
    }

    /**
     * 构建心跳消息
     * @param msg
     * @return
     */
    public static SubscriberInfoProto.SubscriberBody buildHeartBeatBody(String msg) {
        SubscriberInfoProto.SubscriberBody.Builder builder = SubscriberInfoProto.SubscriberBody.newBuilder()
                .setType(SubscriberInfoProto.SubscriberBody.DataType.HeartBate)
                .setClassType(SubscriberInfoProto.HeartBate.class.getCanonicalName());
        return builder.setHb(SubscriberInfoProto.HeartBate.newBuilder().setMsg(msg).build()).build();
    }

    /**
     * 构建发布订阅消息
     * @param beansAlias
     * @param exchange
     * @return
     */
    public static SubscriberInfoProto.SubscriberBody buildSubscribeBody(Set<String> beansAlias, SubscriberInfoProto.Exchange exchange) {
        SubscriberInfoProto.SubscriberBody.Builder builder = SubscriberInfoProto.SubscriberBody.newBuilder()
                .setType(SubscriberInfoProto.SubscriberBody.DataType.Subscribe)
                .setClassType(SubscriberInfoProto.SubscribeBody.class.getCanonicalName());
        return builder.setSb(SubscriberInfoProto.SubscribeBody.newBuilder().setExchange(exchange).addAllBeanAlias(beansAlias).build()).build();
    }

}
