package com.galen.subscriber.server.common;

import io.netty.channel.Channel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server.common
 * @description 订阅表数据中心
 *
 * 用于保存注册的订阅信息，长连接channel，通知的目标bean信息等
 *
 * @date 2020-05-26 23:47
 */
public class SubscribeTableCenter {

    /**
     * 存放channel，key：channelId，value：{@link Channel}
     */
    private static final ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>();

    /**
     * key1：订阅的表，格式：db.table。 value：channelIds
     */
    private static final ConcurrentHashMap<String, Set<String>> tables = new ConcurrentHashMap<>();

    /**
     * key：db.table##channelId，value：beanAlias
     */
    private static final ConcurrentHashMap<String, Set<String>> channelBeans = new ConcurrentHashMap<>();

    /**
     * appId与Channel绑定, key: channelId, value: appId
     */
    private static final ConcurrentHashMap<String, String> appChannels = new ConcurrentHashMap<>();

    //private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Set<String>>> tableMaps = new ConcurrentHashMap<>();

    private static final String RELATION = "##";

    /**
     * 注册订阅信息
     * <p>客户端与服务端建立长连接之后，立即同步给服务端注册信息</p>
     *
     * @param channel
     * @param table
     */
    public static void register(Channel channel, Map<String, String> table, String appId) {
        // 加入channels
        saveChannel(channel);

        // 加入订阅表
        saveSubscribeTable(channel, table);

        // 绑定appId
        bandAppId(channel, appId);
    }

    private static void bandAppId(Channel channel, String appId) {
        appChannels.put(getChannelId(channel), appId);
    }

    /**
     * 移除订阅信息
     * <p>长连接断开时，移除注册信息</p>
     *
     * @param channel
     */
    public static void unRegister(Channel channel) {
        String channelId = getChannelId(channel);
        // 移除channel
        removeChannel(channelId);

        // 移除订阅
        removeSubscribeTable(channelId);

        // 移除appId绑定关系
        removeAppChannel(channelId);
    }

    private static void removeAppChannel(String channelId) {
        appChannels.remove(channelId);
    }

    /**
     * 根据appId获取Channel
     *
     * @param appId
     * @return
     */
    @Deprecated
    public static Channel getChannelByAppId(String appId) {
        String channelId = appChannels.search(1L, (k, v) -> {
            if (v.equals(appId)) {
                return k;
            }
            return null;
        });
        return Optional.ofNullable(channelId).map(channels::get).orElse(null);
    }

    /**
     * 根据订阅表获取所有订阅的channel
     *
     * @param subTable
     * @return
     */
    public static Set<Channel> listChannelBySub(String subTable) {
        Set<Channel> chs = new HashSet<>();
        if (MapUtils.isNotEmpty(tables)) {
            Set<String> strings = tables.get(subTable);
            strings.forEach(s -> chs.add(channels.get(s)));
        }
        return chs;
    }

    /**
     * 根据channel和订阅表获取beanAlias
     *
     * @param channel
     * @param subTable
     * @return
     */
    public static Set<String> listBeanAliasByChannel(Channel channel, String subTable) {
        String channelId = getChannelId(channel);
        return channelBeans.get(getChannelBeanKey(subTable, channelId));
    }

    private static String getChannelBeanKey(String subTable, String channelId) {
        return subTable + RELATION + channelId;
    }

    /**
     * 获取所有的订阅表
     *
     * @return
     */
    public static Set<String> listAllSubscribeTables() {
        return tables.keySet();
    }

    /**
     * 根据channel获取appId
     * @param channel
     * @return
     */
    public static String geAppId(Channel channel) {
        return appChannels.get(getChannelId(channel));
    }

    /**
     * 获取所有的订阅表
     *
     * @return
     */
    public static String getAllSubscribeTables() {
        Set<String> tbs = listAllSubscribeTables();
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(tbs)) {
            tbs.forEach(s -> builder.append(s).append(","));
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     * 移除订阅
     *
     * @param channelId
     */
    private static void removeSubscribeTable(String channelId) {

        tables.forEach((k, v) -> {
            v.remove(channelId);
            if (v.isEmpty()) {
                tables.remove(k);
            }
        });

        if (MapUtils.isNotEmpty(channelBeans)) {
            channelBeans.forEach((k, v) -> {
                if (containsChannelId(channelId, k)) {
                    channelBeans.remove(k);
                }
            });
        }

    }


    private static boolean containsChannelId(String channelId, String key) {
        return key.split(RELATION)[1].equals(channelId);
    }

    /**
     * 移出channel
     *
     * @param channelId
     */
    private static void removeChannel(String channelId) {
        channels.remove(channelId);
    }

    /**
     * 新增并保存订阅信息
     *
     * @param channel
     * @param table
     */
    private static void saveSubscribeTable(Channel channel, Map<String, String> table) {
        if (MapUtils.isNotEmpty(table)) {
            String channelId = getChannelId(channel);
            table.forEach((k, v) -> {
                tables.computeIfAbsent(v, kk -> new HashSet<>()).add(channelId);
                bindChannelBean(channelId, k, v);
            });
        }
    }

    /**
     * 绑定channel 和 beanAlias
     *  @param channelId
     * @param beanAlias
     * @param subTable
     */
    private static void bindChannelBean(String channelId, String beanAlias, String subTable) {
        // 已绑定channel和bean
        String key = getChannelBeanKey(subTable, channelId);
        channelBeans.computeIfAbsent(key, k -> new HashSet<>()).add(beanAlias);
    }

    private static void saveChannel(Channel channel) {
        channels.put(getChannelId(channel), channel);
    }

    private static String getChannelId(Channel channel) {
        return channel.id().asShortText();
    }

}
