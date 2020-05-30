package com.galen.subscriber.server.filter;

import com.galen.subscriber.server.common.Result;
import com.galen.subscriber.server.filter.chain.CanalFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import static com.galen.subscriber.server.filter.FilterOrderConstant.PERSIST;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server.filter
 * @description 将订阅的Exchange持久化，等待ACK确认。
 *
 * 同一张表的同一条数据发生一次以上变化，则重写。（覆盖未经过ACK确认的记录）
 *
 * @date 2020-05-28 10:32
 */
@Component
public class AckPersistHandlerFilter implements CanalFilter, Ordered {
    @Override
    public Result filter(CanalExchange exchange, CanalFilterChain chain) {


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrderConstant.PERSIST;
    }
}
