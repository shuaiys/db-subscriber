package com.galen.subscriber.core.body;

import lombok.Data;

import java.io.Serializable;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core
 * @description TODO
 * @date 2020-05-20 22:11
 */
@Data
public class BaseBody implements Serializable {

    private static final long serialVersionUID = 1325307421527361175L;
    private Integer type;

}
