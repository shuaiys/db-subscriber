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
public class Ack {

    /**
     * 1-成功，2-延时重试，3-放弃
     */
    private Integer status;
    private Exchange exchange;

    public Ack(Integer status, Exchange exchange) {
        this.status = status;
        this.exchange = exchange;
    }

    public static Ack SUCCESS(){
        return new Ack(1, null);
    }

    public static Ack RETRY(Exchange exchange) {
        return new Ack(2, exchange);
    }

    public static Ack DISCARD() {
        return new Ack(3, null);
    }

}
