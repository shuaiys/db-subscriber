package com.galen.subscriber.server.filter.chain;


import com.galen.subscriber.server.common.Result;
import com.galen.subscriber.server.filter.CanalExchange;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.filter.chain
 * @description chain
 * @date 2020-03-05 21:46
 */
public interface CanalFilterChain {

    Result filter(CanalExchange exchange);
}
