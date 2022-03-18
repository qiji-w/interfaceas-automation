package com.interfaces.iat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interfaces.iat.service.TaskModuleService;
import com.interfaces.iat.entity.TaskModuleEntity;
import com.interfaces.iat.mapper.TaskModuleMapper;
import org.springframework.stereotype.Service;


@Service
public class TaskModuleServiceImpl extends ServiceImpl<TaskModuleMapper, TaskModuleEntity> implements TaskModuleService {
}
