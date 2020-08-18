package com.galen.subscriber.server.configuration;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.configuration
 * @description canal连接配置,暂未支持多节点集群
 * @date 2020-03-08 22:51
 */
@Component
@Data
@ConfigurationProperties(prefix = "canal.server")
public class CanalConnectorConfiguration{
    private String ip;
    private Integer port;
    private String destination = "example";
    private String username = "";
    private String password = "";
    private String subscribe;

    public CanalConnector getConnectorConfig() {
        CanalConnector connector;
        if (StringUtils.isBlank(subscribe)) {
            subscribe = ".*\\..*"; // 订阅所有，格式 {database}.{table}
        }
        // 创建单节点类型连接配置信息
        connector = CanalConnectors.newSingleConnector(new InetSocketAddress(ip,
                port), destination, username, password);

        // 多节点创建方式
//        List<InetSocketAddress> ips = new ArrayList<>();
//        ips.add(new InetSocketAddress(ip, port));
//        connector = CanalConnectors.newClusterConnector(ips, destination, username, password);

        return connector;
    }

}
