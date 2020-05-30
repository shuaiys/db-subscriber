package com.galen.subscriber.server.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.galen.subscriber.server.configuration.CanalConnectorConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
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
public class CanalClient implements DisposableBean {

    @Resource
    private CanalConnectorConfiguration connectorConfiguration;

    public static CanalConnector connector;

    // 订阅表
    public static String lastSubscriber;

    @Bean
    public CanalConnector canalConnector() {
        connector = connectorConfiguration.getConnectorConfig();
        // 开启连接
        connector.connect();
        // 订阅
        connector.subscribe(connectorConfiguration.getSubscribe());

        lastSubscriber = connectorConfiguration.getSubscribe();
        // 回滚到上次订阅的位置
        connector.rollback();
        log.info("canal 客户端启动成功！");
        return connector;
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
}
