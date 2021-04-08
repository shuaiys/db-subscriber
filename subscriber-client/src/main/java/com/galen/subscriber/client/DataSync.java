package com.galen.subscriber.client;

import com.galen.subscriber.core.Ack;
import com.galen.subscriber.core.Exchange;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description 数据同步顶级接口
 * @date 2020-05-18 21:32
 */
public interface DataSync {

    /**
     * 同步接口
     *
     * @param exchange  更新数据
     * @return
     */
    Ack doSync(Exchange exchange);
}
