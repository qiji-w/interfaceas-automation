package com.interfaces.iat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.task.TaskCreateInputDto;
import com.interfaces.iat.dto.input.task.TaskRunInputDto;
import com.interfaces.iat.dto.input.task.TaskUpdateInputDto;
import com.interfaces.iat.dto.output.module.ModuleOutputDto;
import com.interfaces.iat.dto.output.task.TaskDetailOutputDto;
import com.interfaces.iat.dto.output.task.TaskOutputDto;
import com.interfaces.iat.dto.output.task.TaskQueryOutputDto;
import com.interfaces.iat.dto.input.task.ChangeIsJobOrNotInputDto;
import com.interfaces.iat.entity.TaskEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 测试任务服务接口
 */
public interface TaskService extends IService<TaskEntity> {
    /**
     * 分页查询列表
     * @param pageIndex
     * @param pageSize
     * @param projectId
     * @return
     */
    ResponseData<List<TaskQueryOutputDto>> query(Integer pageIndex, Integer pageSize, String name, Integer projectId);

    /**
     * 查询全部
     * @return
     */
    ResponseData<List<TaskOutputDto>> queryByProjectId(Integer projectId);

    /**
     * 查询全部，包含详情
     * @return
     */
    ResponseData<List<TaskDetailOutputDto>> queryDetailByProjectId(Integer projectId);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    ResponseData<TaskOutputDto> getById(Integer id);

    /**
     * 创建
     * @param inputDto
     * @return
     */
    ResponseData<TaskOutputDto> create(TaskCreateInputDto inputDto);

    /**
     * 更新
     * @param inputDto
     * @return
     */
    ResponseData<TaskOutputDto> update(TaskUpdateInputDto inputDto);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    ResponseData<Boolean> delete(Integer id);

    /**
     * 根据任务ID获取关联模块
     * @param taskId
     * @return
     */
    ResponseData<List<ModuleOutputDto>> getModulesByTaskId(Integer taskId);

    /**
     * 运行任务
     * @param taskRunInputDto
     * @return
     */
    ResponseData<Boolean> run(TaskRunInputDto taskRunInputDto);

    /**
     * 变化任务是否定时执行
     * @param inputDto
     * @return
     */
    ResponseData<Boolean> changeIsJobOrNot(ChangeIsJobOrNotInputDto inputDto);

    /**
     * 归档任务
     * @param id
     * @return
     */
    ResponseData<Boolean> archive(Integer id);
}
