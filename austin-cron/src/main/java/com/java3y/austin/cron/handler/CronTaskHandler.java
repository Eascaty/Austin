package com.java3y.austin.cron.handler;

import com.dtp.core.thread.DtpExecutor;
import com.java3y.austin.cron.config.CronAsyncThreadPoolConfig;
import com.java3y.austin.cron.service.TaskHandler;
import com.java3y.austin.support.utils.ThreadPoolUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 后台提交的定时任务处理类
 *
 * @author 3y
 */
@Service
@Slf4j
public class CronTaskHandler {

    @Autowired
    private TaskHandler taskHandler;

    @Autowired
    private ThreadPoolUtils threadPoolUtils;
    private final DtpExecutor dtpExecutor = CronAsyncThreadPoolConfig.getXxlCronExecutor();

    /**
     * 处理后台的 Austin 定时任务消息
     *
     * @param url 请求地址
     * @param messageTemplateId 消息模板 ID
     * @param receiver 接收者
     */
    @XxlJob("austinJob")
    public void execute(String url, Integer messageTemplateId, String receiver) {
        log.info("定时任务开始，准备向 Austin 接口发送消息");

        try {
            // 将任务提交到自定义线程池中执行
            dtpExecutor.execute(() -> {
                try {
                    taskHandler.sendMessage(url, messageTemplateId, receiver);
                    log.info("消息发送成功");
                } catch (Exception e) {
                    log.error("消息发送失败", e);
                }
            });
        } catch (Exception e) {
            log.error("任务提交线程池失败", e);
        }
    }
}