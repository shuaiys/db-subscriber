package com.galen.subscriber.server.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.galen.subscriber.server.common.SubscribeTableCenter;
import com.galen.subscriber.server.configuration.CanalConnectorConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.canal
 * @description 提供默认的canal连接
 * @date 2020-03-04 20:35
 */
@Component
@Slf4j
public class CanalClient implements InitializingBean, DisposableBean {

    @Resource
    private CanalConnectorConfiguration connectorConfiguration;

    public static CanalConnector connector;

    /**
     * 最近一次的订阅表
     */
    public static String lastSubscribeTable;


    @Override
    public void afterPropertiesSet() throws Exception {
        connector = this.connectorConfiguration.getConnectorConfig();
        // 开启连接
        connector.connect();
        // 订阅
        connector.subscribe(this.connectorConfiguration.getSubscribe());

        lastSubscribeTable = this.connectorConfiguration.getSubscribe();
        // 回滚到上次订阅的位置
        connector.rollback();
        log.info("canal 客户端启动成功！");
    }

    /**
     * 销毁bean时断开连接
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        if (null != connector) {
            connector.disconnect();
            log.info("canal client 成功断开。");
        }
    }

    /**
     * 刷新订阅表
     */
    public static void freshCanalSubscribe() {
        String tables = SubscribeTableCenter.getAllSubscribeTables();
        // 判断订阅表是否发生变化，变化则刷新订阅
        if (!tables.equals(lastSubscribeTable)) {
            connector.subscribe(tables);
            log.info("订阅表更新为：{}", tables);
        }
    }

}
