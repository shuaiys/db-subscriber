package com.galen.subscriber.example.sync;

import com.galen.subscriber.client.DataSync;
import com.galen.subscriber.client.annotation.Subscriber;
import com.galen.subscriber.core.Ack;
import com.galen.subscriber.core.Exchange;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description 测试demo
 * @date 2020-05-18 23:04
 */
@Subscriber(db = "galen_dev", table = "product_detail", name = "galen1", contextId = "galenTB")
@Slf4j
public class GalenTableSync implements DataSync {
    @Override
    public Ack doSync(Exchange exchange) {
        log.debug(exchange.getTableName() + 1);
        return Ack.SUCCESS();
    }
}
