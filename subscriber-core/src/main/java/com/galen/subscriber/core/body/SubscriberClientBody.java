package com.galen.subscriber.core.body;

import com.galen.subscriber.core.Exchange;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core
 * @description TODO
 * @date 2020-05-20 21:07
 */
@Data
@Accessors(chain = true)
public class SubscriberClientBody extends BaseBody implements Serializable {

    private static final long serialVersionUID = 6425794545867313535L;
    private String beanAlias;

    private Exchange exchange;

    private Integer id;
}
