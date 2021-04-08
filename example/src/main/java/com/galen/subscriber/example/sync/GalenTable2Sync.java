package com.galen.subscriber.example.sync;

import com.galen.subscriber.client.DataSync;
import com.galen.subscriber.client.annotation.Subscriber;
import com.galen.subscriber.core.Ack;
import com.galen.subscriber.core.Exchange;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.example.sync
 * @description 测试demo
 * @date 2020-05-27 22:37
 */
@Subscriber(db = "galen_dev", table = "product_detail", name = "galen2", contextId = "galenTB")
@Slf4j
public class GalenTable2Sync implements DataSync {
    @Override
    public Ack doSync(Exchange exchange) {
        log.debug(exchange.getTableName() + 2);
        return Ack.SUCCESS();
    }
}
