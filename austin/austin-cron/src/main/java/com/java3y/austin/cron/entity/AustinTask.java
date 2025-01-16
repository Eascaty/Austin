package com.java3y.austin.cron.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * austin_task 表，存储任务数据，包括消息模板、接收者、任务处理状态等信息
 * </p>
 *
 * @author author
 * @since 2025-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("austin_task")
@ApiModel(value="AustinTask对象", description="austin_task 表，存储任务数据，包括消息模板、接收者、任务处理状态等信息")
public class AustinTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，唯一标识每一条任务记录")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "消息模板 ID，表示使用的消息模板的 ID")
    private Integer messageTemplateId;

    @ApiModelProperty(value = "接收者，存储消息的接收者邮箱或其他标识（例如手机号）")
    private String receiver;

    @ApiModelProperty(value = "动态变量，存储与消息相关的变量，可以使用 JSON 格式存储任意自定义的键值对")
    private String variables;

    @ApiModelProperty(value = "附加信息，存储与任务相关的其他附加信息，也采用 JSON 格式存储")
    private String extra;

    @ApiModelProperty(value = "状态标志位，0 表示任务未处理，1 表示任务已处理（可以根据任务执行状态设置）")
    private Integer flag;

    @ApiModelProperty(value = "创建时间，记录任务创建的时间，默认为当前时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间，记录任务最后更新时间，默认为当前时间，并且在每次更新时自动更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "备用字段 1，用于存储额外的信息或未来扩展")
    private String backup1;

    @ApiModelProperty(value = "备用字段 2，用于存储额外的信息或未来扩展")
    private String backup2;

    @ApiModelProperty(value = "备用字段 3，用于存储额外的信息或未来扩展")
    private String backup3;

    @ApiModelProperty(value = "备用字段 4，用于存储额外的信息或未来扩展")
    private String backup4;

    @ApiModelProperty(value = "备用字段 5，用于存储额外的信息或未来扩展")
    private String backup5;


}
