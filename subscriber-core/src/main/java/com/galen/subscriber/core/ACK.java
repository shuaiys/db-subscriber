package com.galen.subscriber.core;

import lombok.Data;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core
 * @description TODO
 * @date 2020-05-18 22:36
 */
@Data
public class ACK {

    private Integer status;  // 1-成功，2-延时重试，3-放弃
    private Exchange exchange;

    public ACK(Integer status, Exchange exchange) {
        this.status = status;
        this.exchange = exchange;
    }

    public static ACK SUCCESS(){
        return new ACK(1, null);
    }

    public static ACK RETRY(Exchange exchange) {
        return new ACK(2, exchange);
    }

    public static ACK DISCARD() {
        return new ACK(3, null);
    }

}
