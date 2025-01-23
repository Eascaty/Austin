package com.java3y.austin.handler.script.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.google.common.base.Throwables;
import com.java3y.austin.common.dto.account.sms.AliyunSmsAccount;
import com.java3y.austin.common.enums.SmsStatus;
import com.java3y.austin.handler.domain.sms.SmsParam;
import com.java3y.austin.handler.script.SmsScript;
import com.java3y.austin.support.domain.SmsRecord;
import com.java3y.austin.support.utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("AliyunSmsScript")
public class AliyunSmsScript implements SmsScript {

    @Autowired
    private AccountUtils accountUtils;

    @Override
    public List<SmsRecord> send(SmsParam smsParam) {
        try {
            AliyunSmsAccount aliyunSmsAccount = Objects.nonNull(smsParam.getSendAccountId()) ?
                    accountUtils.getAccountById(smsParam.getSendAccountId(), AliyunSmsAccount.class) :
                    accountUtils.getSmsAccountByScriptName(smsParam.getScriptName(), AliyunSmsAccount.class);

            Client client = init(aliyunSmsAccount);
            SendSmsRequest request = assembleSendReq(smsParam, aliyunSmsAccount);
            SendSmsResponse response = client.sendSms(request);
            return assembleSendSmsRecord(smsParam, response, aliyunSmsAccount);
        } catch (Exception e) {
            log.error("AliyunSmsScript#send fail:{}, params:{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(smsParam));
            return new ArrayList<>();
        }
    }

    @Override
    public List<SmsRecord> pull(Integer accountId) {
        log.warn("Aliyun does not support pulling SMS status directly. Implement webhook for delivery status.");
        return new ArrayList<>();
    }

    private Client init(AliyunSmsAccount account) throws Exception {
        Config config = new Config()
                .setAccessKeyId(account.getAccessKeyId())
                .setAccessKeySecret(account.getAccessKeySecret());
        config.endpoint = account.getEndpoint();
        return new Client(config);
    }

    private SendSmsRequest assembleSendReq(SmsParam smsParam, AliyunSmsAccount account) {
        SendSmsRequest request = new SendSmsRequest();

        // 设置手机号（多个手机号用逗号分隔）
        request.setPhoneNumbers(String.join(",", smsParam.getPhones()));

        // 设置签名
        request.setSignName(account.getSignName());

        // 设置模板代码
        request.setTemplateCode(account.getTemplateCode());

        // 将短信内容转为 JSON 格式的字符串
        String templateParamJson = String.format("{\"code\":\"%s\"}", smsParam.getContent());
        request.setTemplateParam(templateParamJson); // 确保是 JSON 格式的字符串

        return request;
    }

    private List<SmsRecord> assembleSendSmsRecord(SmsParam smsParam, SendSmsResponse response, AliyunSmsAccount account) {
        List<SmsRecord> smsRecordList = new ArrayList<>();

        // 确保 phones 不为空
        Set<String> phones = smsParam.getPhones();
        if (phones == null || phones.isEmpty()) {
            log.error("AliyunSmsScript#assembleSendSmsRecord: phone set is empty");
            return smsRecordList;
        }

        // 获取第一个手机号
        String phone = phones.iterator().next();

        if (response.getBody() != null && "OK".equals(response.getBody().getCode())) {
            String requestId = response.getBody().getRequestId() != null ? response.getBody().getRequestId() : "N/A";
            String message = response.getBody().getMessage() != null ? response.getBody().getMessage() : "No message";

            SmsRecord smsRecord = SmsRecord.builder()
                    .sendDate(Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)))
                    .messageTemplateId(smsParam.getMessageTemplateId())
                    .phone(Long.valueOf(phone))
                    .supplierId(account.getSupplierId())
                    .supplierName(account.getSupplierName())
                    .msgContent(smsParam.getContent())
                    .seriesId(requestId)
                    .chargingNum(1)
                    .status(SmsStatus.SEND_SUCCESS.getCode())
                    .reportContent(message)
                    .created((int) (System.currentTimeMillis() / 1000))
                    .updated((int) (System.currentTimeMillis() / 1000))
                    .build();

            smsRecordList.add(smsRecord);
        } else {
            String errorCode = response.getBody() != null ? response.getBody().getCode() : "No Code";
            String errorMessage = response.getBody() != null ? response.getBody().getMessage() : "No Message";
            log.error("AliyunSmsScript#assembleSendSmsRecord fail: code={}, message={}, smsParam={}", errorCode, errorMessage, JSON.toJSONString(smsParam));
        }

        return smsRecordList;
    }}