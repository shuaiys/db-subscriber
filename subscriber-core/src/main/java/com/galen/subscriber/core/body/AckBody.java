package com.galen.subscriber.core.body;

import com.galen.subscriber.core.Ack;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core
 * @description TODO
 * @date 2020-05-20 22:30
 */
@Data
@AllArgsConstructor
public class AckBody extends BaseBody{
    private static final long serialVersionUID = -2796372188242127576L;

    private Ack ack;

}