package com.galen.subscriber.core.body;

import com.galen.subscriber.core.body.BaseBody;
import lombok.Data;

import java.util.Map;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core
 * @description TODO
 * @date 2020-05-20 21:25
 */
@Data
public class SubscribeInfoRegisterBody extends BaseBody {
    private static final long serialVersionUID = 2205384140279371890L;

    private Map<String, String> registerTable;

}
