package com.galen.subscriber.server.event;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.galen.subscriber.server.filter.CanalFilter;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.event
 * @description canal event事件
 * @date 2020-03-08 21:54
 */
@Data
public class CanalEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1413300830691435902L;

    private List<CanalFilter> filters;

    public CanalEvent(CanalEntry.Entry source) {
        super(source);
        this.filters = new ArrayList<>();
    }

    public CanalEvent(CanalEntry.Entry source, List<CanalFilter> filters) {
        super(source);
        this.filters = filters;
    }

    public CanalEntry.Entry getEntry(){
        return (CanalEntry.Entry) source;
    }
}