package com.java3y.austin.cron.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hutool.core.text.csv.CsvRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java3y.austin.cron.entity.AustinTask;
import com.java3y.austin.cron.service.TaskHandler;
import com.java3y.austin.support.dao.MessageTemplateDao;
import com.java3y.austin.support.pending.AbstractLazyPending;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import  java.util.List;




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

    private final RestTemplate restTemplate = new RestTemplate(); // 发送 HTTP 请求的 RestTemplate

    /**
     * 发送消息到指定接口
     *
     * @param task 任务对象，包含消息模板 ID、接收者、变量和附加信息
     * @return 接口响应内容
     */
    public String sendMessage(AustinTask task) {
        String url = "http://localhost:8080/send"; // 接口的请求地址

        // 构造请求体
        String payload = createRequestPayload(task);
        if (payload == null) {
            log.error("构造请求体失败，任务 ID: {}", task.getId());
            return "请求体构造失败";
        }

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // 指定请求体的类型为 JSON
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON)); // 指定接受的响应类型为 JSON

        // 构造 HttpEntity
        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        // 发送 HTTP 请求
        try {
            log.info("发送请求，任务 ID: {}, 请求体: {}", task.getId(), payload);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            log.info("任务 ID: {}，接口响应: {}", task.getId(), response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("任务 ID: {}，发送请求失败", task.getId(), e);
            return "请求失败";
        }
    }



    private String createRequestPayload(AustinTask task) {
        if (task == null) {
            log.error("任务对象为空");
            return null;
        }

        // 构造请求体的对象结构
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("code", "send");
            payload.put("messageTemplateId", task.getMessageTemplateId());

            Map<String, Object> messageParam = new HashMap<>();
            messageParam.put("bizId", null);
            messageParam.put("receiver", task.getReceiver());

            // 处理 variables 字段
            String variables = task.getVariables();
            if (variables != null && variables.trim().startsWith("{")) {
                // 如果 variables 是 JSON 格式字符串，解析为对象
                messageParam.put("variables", new ObjectMapper().readValue(variables, Map.class));
            } else {
                // 如果不是 JSON 格式，设置为默认空 JSON 对象
                messageParam.put("variables", new HashMap<>());
            }

            messageParam.put("extra", task.getExtra());

            payload.put("messageParam", messageParam);
            payload.put("recallMessageIds", null);

            // 使用 ObjectMapper 将对象转换为 JSON 字符串
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(payload);

        } catch (Exception e) {
            log.error("构造请求体失败，任务 ID: {}", task.getId(), e);
            return null;
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
