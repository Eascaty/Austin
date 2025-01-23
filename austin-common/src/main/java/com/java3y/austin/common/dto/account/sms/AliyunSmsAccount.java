package com.java3y.austin.common.dto.account.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 阿里云短信参数
 * <p>
 * 账号参数示例：
 * {
 * "endpoint": "dysmsapi.aliyuncs.com",
 * "accessKeyId": "LTAI5xxxxxxxxxxxxT5",
 * "accessKeySecret": "8rfxxxxxxx8TnbXfA",
 * "templateId": "SMS_12345678",
 * "signName": "Java3y公众号",
 * "supplierId": 20,
 * "supplierName": "阿里云",
 * "scriptName": "AliyunSmsScript"
 * }
 *
 * 阿里云短信服务相关文档：https://help.aliyun.com/document_detail/101414.html
 *
 * @author 3y
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliyunSmsAccount extends SmsAccount {

    /**
     * API 相关
     */
    private String endpoint;

    /**
     * 账号相关
     */
    private String accessKeyId;
    private String accessKeySecret;

    /**
     * 短信发送相关
     */
    private String templateCode;
    private String signName;

    /**
     * 重写 equals 方法
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AliyunSmsAccount that = (AliyunSmsAccount) o;
        return endpoint.equals(that.endpoint) &&
                accessKeyId.equals(that.accessKeyId) &&
                accessKeySecret.equals(that.accessKeySecret) &&
                templateCode.equals(that.templateCode) &&
                signName.equals(that.signName);
    }

    /**
     * 重写 hashCode 方法
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), endpoint, accessKeyId, accessKeySecret, templateCode, signName);
    }
}