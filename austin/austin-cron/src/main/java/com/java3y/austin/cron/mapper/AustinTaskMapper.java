package com.java3y.austin.cron.mapper;

import com.java3y.austin.cron.entity.AustinTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * austin_task 表，存储任务数据，包括消息模板、接收者、任务处理状态等信息 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2025-01-09
 */

@Mapper
public interface AustinTaskMapper extends BaseMapper<AustinTask> {

    // 查询未处理的任务
    @Select("SELECT * FROM austin_task WHERE flag = 0 limit 1000")
    List<AustinTask> findUnprocessedTasks();

    // 更新任务状态
    @Update("UPDATE austin_task SET flag = 1 WHERE id = #{id}")
    int updateTaskFlag(@Param("id") Long id);
}
