package com.java3y.austin.cron.handler;

import com.dtp.core.thread.DtpExecutor;
import com.java3y.austin.cron.config.CronAsyncThreadPoolConfig;
import com.java3y.austin.cron.entity.AustinTask;
import com.java3y.austin.cron.mapper.AustinTaskMapper;
import com.java3y.austin.cron.service.TaskHandler;
import com.java3y.austin.support.utils.ThreadPoolUtils;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    private AustinTaskMapper taskMapper;

    @Autowired
    private ThreadPoolUtils threadPoolUtils;

    private final DtpExecutor dtpExecutor = CronAsyncThreadPoolConfig.getXxlCronExecutor();

    /**
     * 处理后台的 austin 定时任务消息
     */
    @XxlJob("austinJob")
    public void execute() {
        log.info("定时任务开始，查询未处理任务...");

        // 注册线程池到动态线程池工具
        threadPoolUtils.register(dtpExecutor);

        // 查询未处理任务
        List<AustinTask> tasks = taskMapper.findUnprocessedTasks();
        if (tasks.isEmpty()) {
            log.info("无未处理任务");
            return;
        }

        // 并行处理任务
        tasks.forEach(this::processTaskAsync);
    }



    /**
     * 异步处理单个任务
     *
     * @param task 未处理的任务
     */
    private void processTaskAsync(AustinTask task) {
        CompletableFuture.runAsync(() -> {
            try {
                log.info("开始处理任务，任务 ID: {}", task.getId());
                taskHandler.sendMessage(task); // 调用 TaskHandler 处理任务
                updateTaskStatus(task, 1);     // 更新任务状态为已处理
                log.info("任务处理成功，任务 ID: {}", task.getId());
            } catch (Exception e) {
                log.error("任务处理失败，任务 ID: {}", task.getId(), e);
            }
        }, dtpExecutor);
    }

    /**
     * 更新任务状态
     *
     * @param task 任务对象
     * @param status 新的状态值
     */
    private void updateTaskStatus(AustinTask task, int status) {
        task.setFlag(status);
        taskMapper.updateById(task);
    }
}