package com.galen.subscriber.client;

import com.galen.subscriber.client.netty.ClientConstant;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.client
 * @description 容器关闭，断开连接
 * @date 2020-05-26 22:58
 */
@Component
public class ApplicationStopListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            ClientConstant.isRun = false;
            while (true) {
                try {
                    // 清空缓存
                    ClientConstant.closeCtx();
                    // 关闭线程池
                    ClientConstant.executor.shutdown();
                    if (ClientConstant.executor.awaitTermination(1, TimeUnit.SECONDS)) break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
