package com.java3y.austin.cron.service.impl;

import com.java3y.austin.cron.entity.AustinTask;
import com.java3y.austin.cron.mapper.AustinTaskMapper;
import com.java3y.austin.cron.service.IAustinTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * austin_task 表，存储任务数据，包括消息模板、接收者、任务处理状态等信息 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-01-09
 */
@Service
public class AustinTaskServiceImpl extends ServiceImpl<AustinTaskMapper, AustinTask> implements IAustinTaskService {

}
