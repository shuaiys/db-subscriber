package com.galen.subscriber.server.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.server.task
 * @description TODO
 * @date 2020-05-28 19:52
 */
@Component
public class RetryDataSyncTask {

    /**
     * 每天2点定时清除过期的exchange
     *
     * <p>当消息存放超过x天未接收到ACK确认，将会被视为无效数据定时清除</p>
     */
    @Scheduled(cron = "0 0 2 1/1 * ?")
    public void clearExpireExchange() {

    }

    /**
     * 每3分钟扫描一次未提交ACK的消息，并重新发送
     */
    @Scheduled(fixedDelay = 30 * 1000L)
    public void retrySync() {

    }

}
