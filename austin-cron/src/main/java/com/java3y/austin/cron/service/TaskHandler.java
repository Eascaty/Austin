package com.java3y.austin.cron.service;

/**
 * @author 3y
 * @date 2022/2/9
 * 具体处理定时任务逻辑的Handler
 */
public interface TaskHandler {

    /**
     * 处理具体的逻辑
     *
     * @param  url, Integer messageTemplateId, String receiver
     */
    void sendMessage(String url, Integer messageTemplateId, String receiver);

}
