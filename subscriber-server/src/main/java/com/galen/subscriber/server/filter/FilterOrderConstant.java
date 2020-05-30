package com.galen.subscriber.server.filter;

import org.springframework.core.PriorityOrdered;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server.filter
 * @description TODO
 * @date 2020-05-28 20:46
 */
public class FilterOrderConstant {

    public static final int INIT = PriorityOrdered.HIGHEST_PRECEDENCE;

    public static final int HANDLER = 0;

    public static final int PERSIST = 10;
}
