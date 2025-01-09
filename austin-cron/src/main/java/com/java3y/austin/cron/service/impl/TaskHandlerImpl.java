package com.java3y.austin.cron.service.impl;

import cn.hutool.core.text.csv.CsvRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java3y.austin.cron.service.TaskHandler;
import com.java3y.austin.support.dao.MessageTemplateDao;
import com.java3y.austin.support.pending.AbstractLazyPending;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

        ;


/**
 * @author 3y
 * @date 2022/2/9
 */
@Service
@Slf4j
public class TaskHandlerImpl implements TaskHandler {
    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private ApplicationContext context;


    @Override
    public void sendMessage(String url, Integer messageTemplateId, String receiver) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", "JohnDoe");
        Map<String, Object> extra = new HashMap<>();
        extra.put("priority", "high");

        String payload = createRequestPayload(messageTemplateId, receiver,variables, extra);

    }

    private String createRequestPayload(Integer messageTemplateId, String receiver, Map<String, Object> variables, Map<String, Object> extra) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 构造请求体的顶层数据结构
            Map<String, Object> payload = new HashMap<>();
            payload.put("code", "send");
            payload.put("messageTemplateId", messageTemplateId);

            // 构造 messageParam 对象
            Map<String, Object> messageParam = new HashMap<>();
            messageParam.put("bizId", null);
            messageParam.put("receiver", receiver);
            messageParam.put("variables", variables); // 支持动态变量
            messageParam.put("extra", extra);         // 支持附加信息

            payload.put("messageParam", messageParam);
            payload.put("recallMessageIds", null);

            // 转换为 JSON 字符串
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create request payload", e);
        }
    }
    /**
     * 文件遍历结束时
     * 1. 暂停单线程池消费(最后会回收线程池资源)
     * 2. 更改消息模板的状态(暂未实现)
     *
     * @param row
     * @param countCsvRow
     * @param crowdBatchTaskPending
     * @param messageTemplateId
     */
    private void onComplete(CsvRow row, long countCsvRow, AbstractLazyPending crowdBatchTaskPending, Long messageTemplateId) {
        if (row.getOriginalLineNumber() == countCsvRow) {
            crowdBatchTaskPending.setStop(true);
            log.info("messageTemplate:[{}] read csv file complete!", messageTemplateId);
        }
    }
}
