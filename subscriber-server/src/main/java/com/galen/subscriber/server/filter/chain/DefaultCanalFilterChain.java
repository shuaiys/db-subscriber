package com.galen.subscriber.server.filter.chain;

import com.galen.subscriber.server.common.Result;
import com.galen.subscriber.server.common.ResultUtil;
import com.galen.subscriber.server.filter.CanalExchange;
import com.galen.subscriber.server.filter.CanalFilter;
import lombok.Data;

import java.util.List;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.filter.chain
 * @description 默认的chain实现类
 * @date 2020-03-05 21:00
 */
@Data
public class DefaultCanalFilterChain implements CanalFilterChain {

    private final int size;

    private final List<CanalFilter> filters;

    public DefaultCanalFilterChain(int size, DefaultCanalFilterChain chain) {
        this.size = size;
        this.filters = chain.getFilters();
    }

    public DefaultCanalFilterChain(List<CanalFilter> filters) {
        this.size = 0;
        this.filters = filters;
    }

    @Override
    public Result<?> filter(CanalExchange exchange) {
        if (this.size < this.filters.size()) {
            CanalFilter canalFilter = this.filters.get(this.size);
            DefaultCanalFilterChain filterChain = new DefaultCanalFilterChain(this.size + 1, this);
            return canalFilter.filter(exchange, filterChain);
        }

        return ResultUtil.setOk();
    }
}
