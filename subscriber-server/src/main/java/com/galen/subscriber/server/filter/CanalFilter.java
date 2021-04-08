package com.galen.subscriber.server.filter;


import com.galen.subscriber.server.common.Result;
import com.galen.subscriber.server.filter.chain.CanalFilterChain;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.filter
 * @description filter接口
 * @date 2020-03-05 21:46
 */
public interface CanalFilter {

    /**
     * canal过滤方法
     *
     * @param exchange  canal更新数据
     * @param chain     chain
     * @return
     */
    Result<?> filter(CanalExchange exchange, CanalFilterChain chain);
}
