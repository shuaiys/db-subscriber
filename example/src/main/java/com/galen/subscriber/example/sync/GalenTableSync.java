package com.galen.subscriber.example.sync;

import com.alibaba.fastjson.JSON;
import com.galen.subscriber.client.DataSync;
import com.galen.subscriber.client.annotation.Subscriber;
import com.galen.subscriber.core.ACK;
import com.galen.subscriber.core.Exchange;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description 测试demo
 * @date 2020-05-18 23:04
 */
@Subscriber(db = "minifranchise_dev", table = "n_product_detail", name = "galen1", contextId = "galenTB")
@Slf4j
public class GalenTableSync implements DataSync {
    @Override
    public ACK doSync(Exchange exchange) {
        System.err.println(exchange.getTableName() + 1);
        System.err.println(JSON.toJSONString(exchange));
        return ACK.SUCCESS();
    }
}
