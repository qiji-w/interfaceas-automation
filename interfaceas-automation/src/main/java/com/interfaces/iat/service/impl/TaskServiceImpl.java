package com.interfaces.iat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interfaces.iat.dto.common.ResponseData;
import com.interfaces.iat.dto.input.task.TaskCreateInputDto;
import com.interfaces.iat.dto.input.task.TaskUpdateInputDto;
import com.interfaces.iat.dto.output.job.JobOutputDto;
import com.interfaces.iat.dto.output.module.ModuleOutputDto;
import com.interfaces.iat.dto.output.task.TaskDetailOutputDto;
import com.interfaces.iat.dto.output.task.TaskOutputDto;
import com.interfaces.iat.dto.output.task.TaskQueryOutputDto;
import com.interfaces.iat.dto.output.testcase.TestCaseOutputDto;
import com.interfaces.iat.dto.output.testsuit.TestSuitDetailOutputDto;
import com.interfaces.iat.mapper.TaskMapper;
import com.interfaces.iat.util.SessionUtil;
import com.interfaces.iat.dto.input.task.ChangeIsJobOrNotInputDto;
import com.interfaces.iat.dto.input.task.TaskRunInputDto;
import com.interfaces.iat.entity.EnvironmentEntity;
import com.interfaces.iat.entity.InterfaceEntity;
import com.interfaces.iat.entity.JobEntity;
import com.interfaces.iat.entity.ModuleEntity;
import com.interfaces.iat.entity.ProjectEntity;
import com.interfaces.iat.entity.TaskModuleEntity;
import com.interfaces.iat.entity.TestCaseEntity;
import com.interfaces.iat.entity.TestRecordEntity;
import com.interfaces.iat.entity.TestSuitEntity;
import com.interfaces.iat.entity.TaskEntity;
import com.interfaces.iat.entity.UserEntity;
import com.interfaces.iat.entity.enumeration.JobStatus;
import com.interfaces.iat.service.EnvironmentService;
import com.interfaces.iat.service.InterfaceService;
import com.interfaces.iat.service.JobService;
import com.interfaces.iat.service.ModuleService;
import com.interfaces.iat.service.ProjectService;
import com.interfaces.iat.service.TaskModuleService;
import com.interfaces.iat.service.TaskService;
import com.interfaces.iat.service.TestCaseService;
import com.interfaces.iat.service.TestRecordService;
import com.interfaces.iat.service.TestReportService;
import com.interfaces.iat.service.TestSuitService;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskEntity> implements TaskService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    SessionUtil sessionUtil;
    @Autowired
    ProjectService projectService;
    @Autowired
    EnvironmentService environmentService;
    @Autowired
    ModuleService moduleService;
    @Autowired
    InterfaceService interfaceService;
    @Autowired
    TestCaseService testCaseService;
    @Autowired
    TestRecordService testRecordService;
    @Autowired
    TestSuitService testSuitService;

    @Autowired
    TaskModuleService taskModuleService;
    @Autowired
    TestReportService reportService;
    @Autowired
    TaskTestService taskTestService;
    @Autowired
    JobService jobService;

    @Override
    public ResponseData<List<TaskQueryOutputDto>> query(Integer pageIndex, Integer pageSize, String name, Integer projectId) {
        ResponseData<List<TaskQueryOutputDto>> responseData;

        try {
            QueryWrapper<TaskEntity> queryWrapper = new QueryWrapper<>();
            if(name!=null){
                queryWrapper.like("name", name);
            }
            if(projectId != null) {
                queryWrapper.eq("project_id", projectId);
            }
            queryWrapper.eq("is_delete",false);
            queryWrapper.orderByDesc("id");
            IPage<TaskEntity> queryPage = new Page<>(pageIndex, pageSize);
            queryPage = this.page(queryPage,queryWrapper);
            List<TaskEntity> taskEntities = queryPage.getRecords();

            //获取所有关联模块信息
            QueryWrapper<TaskModuleEntity> taskModuleEntityQueryWrapper = new QueryWrapper<>();
            List<Integer> taskIds = taskEntities.stream().map(s->s.getId()).collect(Collectors.toList());
            taskModuleEntityQueryWrapper.in("task_id", taskIds);
            List <TaskModuleEntity> taskModuleEntities = taskModuleService.list(taskModuleEntityQueryWrapper);
            List<Integer> modudleIds = taskModuleEntities.stream().map(s->s.getModuleId()).collect(Collectors.toList());
            //获取所有关联模块详情
            QueryWrapper<ModuleEntity> moduleEntityQueryWrapper = new QueryWrapper<>();
            moduleEntityQueryWrapper.in("id",modudleIds);
            moduleEntityQueryWrapper.eq("is_delete",false);
            List<ModuleEntity> moduleEntities = moduleService.list(moduleEntityQueryWrapper);
            //获取所有关联定时任务详情
            QueryWrapper<JobEntity> jobEntityQueryWrapper = new QueryWrapper <>();
            jobEntityQueryWrapper.in("task_id",taskIds);
            jobEntityQueryWrapper.eq("is_delete",false);
            List<JobEntity> jobEntities = jobService.list(jobEntityQueryWrapper);

            List<TaskQueryOutputDto> outputDtos = taskEntities.stream().map(s->modelMapper.map(s, TaskQueryOutputDto.class)).collect(Collectors.toList());
            outputDtos.stream().forEach(s->{
                //获取当前任务关联模块
                List<TaskModuleEntity> currentTaskModuleEntities = taskModuleEntities.stream().filter(t->t.getTaskId() == s.getId()).collect(Collectors.toList());
                //获取当前关联模块id列表
                List<Integer> currentModuleIds = currentTaskModuleEntities.stream().map(t->t.getModuleId()).collect(Collectors.toList());
                //根据当前关联模块id列表获取模块详情
                List<ModuleEntity> currentModuleEntities = moduleEntities.stream().filter(t->currentModuleIds.contains(t.getId())).collect(Collectors.toList());
                //设置到当前任务Dto对象
                List<ModuleOutputDto> currentModuleOutputDtos = currentModuleEntities.stream().map(t->modelMapper.map(t,ModuleOutputDto.class)).collect(Collectors.toList());
                s.setModules(currentModuleOutputDtos);

                //获取当前任务关联定时任务
                List<JobEntity> currentJobEntities = jobEntities.stream().sorted(Comparator.comparing(JobEntity::getId).reversed()).filter(t->t.getTaskId() == s.getId()).collect(Collectors.toList());
                //设置到当前任务Dto对象
                List<JobOutputDto> currentJobOutputDtos = currentJobEntities.stream().map(t->modelMapper.map(t, JobOutputDto.class)).collect(Collectors.toList());
                s.setJobs(currentJobOutputDtos);
            });

            responseData = ResponseData.success(outputDtos);
            responseData.setTotal(queryPage.getTotal());
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }


    @Override
    public ResponseData<List<TaskOutputDto>> queryByProjectId(Integer projectId) {
        ResponseData<List<TaskOutputDto>> responseData;

        try {
            QueryWrapper<TaskEntity> queryWrapper = new QueryWrapper<>();
            if(projectId != null) {
                queryWrapper.eq("project_id", projectId);
            }
            queryWrapper.eq("is_delete",false);
            queryWrapper.orderByDesc("id");
            List <TaskEntity> entities = this.list(queryWrapper);
            List <TaskOutputDto> outputDtos = entities.stream().map(s -> modelMapper.map(s, TaskOutputDto.class)).collect(Collectors.toList());

            responseData = ResponseData.success(outputDtos);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<List<TaskDetailOutputDto>> queryDetailByProjectId(Integer projectId) {
        ResponseData<List<TaskDetailOutputDto>> responseData;

        if(projectId == null){
            responseData = new ResponseData <>();
            responseData.setCode(1);
            responseData.setMessage("项目ID不能为空");

            return responseData;
        }

        try {
            QueryWrapper<TaskEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("project_id", projectId);
            queryWrapper.eq("is_delete",false); //只取没有删除的
            queryWrapper.eq("is_archive",false); //只取没有归档的
            List <TaskEntity> entities = this.list(queryWrapper);
            List <TaskDetailOutputDto> outputDtos = entities.stream().map(s -> modelMapper.map(s, TaskDetailOutputDto.class)).collect(Collectors.toList());

            //获取任务所有的测试套件
            QueryWrapper<TestSuitEntity> testSuitEntityQueryWrapper = new QueryWrapper <>();
            testSuitEntityQueryWrapper.eq("project_id", projectId);
            testSuitEntityQueryWrapper.eq("is_delete",false); //只取没有删除的
            List <TestSuitEntity> testSuitEntities = testSuitService.list(testSuitEntityQueryWrapper);

            //获取项目所有的测试用例
            QueryWrapper<TestCaseEntity> testCaseEntityQueryWrapper = new QueryWrapper <>();
            testCaseEntityQueryWrapper.eq("project_id", projectId);
            testCaseEntityQueryWrapper.eq("is_delete",false); //只取没有删除的
            List <TestCaseEntity> testCaseEntities = testCaseService.list(testCaseEntityQueryWrapper);

            //将测试套件、测试用例归类到任务下去
            outputDtos.stream().forEach(taskDetailOutputDto->{
                List<TestSuitEntity> currentTestSuitEntities = testSuitEntities.stream().filter(testSuitEntity->testSuitEntity.getTaskId() == taskDetailOutputDto.getId()).collect(Collectors.toList());
                List<TestSuitDetailOutputDto> testSuitDetailOutputDtos = currentTestSuitEntities.stream().map(testSuitEntity->modelMapper.map(testSuitEntity,TestSuitDetailOutputDto.class)).collect(Collectors.toList());
                testSuitDetailOutputDtos.stream().forEach(testSuitDetailOutputDto->{
                    List<TestCaseEntity> currentTestCaseEntities = testCaseEntities.stream().filter(testCaseEntity->testCaseEntity.getTestSuitId() == testSuitDetailOutputDto.getId()).collect(Collectors.toList());
                    List<TestCaseOutputDto> testCaseDetailOutputDtos = currentTestCaseEntities.stream().map(testCaseEntity->modelMapper.map(testCaseEntity,TestCaseOutputDto.class)).collect(Collectors.toList());

                    testSuitDetailOutputDto.setTestCases(testCaseDetailOutputDtos);
                });

                taskDetailOutputDto.setTestSuits(testSuitDetailOutputDtos);
            });

            responseData = ResponseData.success(outputDtos);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData <TaskOutputDto> getById(Integer id) {
        ResponseData<TaskOutputDto> responseData;
        try {
            TaskEntity entity = super.getById(id);

            //获取关联模块id
            QueryWrapper<TaskModuleEntity> taskModuleEntityQueryWrapper = new QueryWrapper<>();
            taskModuleEntityQueryWrapper.eq("task_id",id);
            List<TaskModuleEntity> taskEntities = taskModuleService.list(taskModuleEntityQueryWrapper);
            List<Integer> moduleIds = taskEntities.stream().map(s->s.getModuleId()).collect(Collectors.toList());

            TaskOutputDto outputDto = modelMapper.map(entity,TaskOutputDto.class);
            outputDto.setModuleIds(moduleIds);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<TaskOutputDto> create(TaskCreateInputDto inputDto) {
        ResponseData<TaskOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //当前项目模块名称是否存在
            QueryWrapper<TaskEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", inputDto.getName());
            queryWrapper.eq("is_delete",false);
            queryWrapper.eq("project_id",inputDto.getProjectId());
            TaskEntity taskEntity = this.getOne(queryWrapper,false);
            if (taskEntity!=null) {
                checkMsgs.add("任务名称已经存在");
            }
            //所属项目是否存在
            QueryWrapper<ProjectEntity> projectQueryWrapper = new QueryWrapper<>();
            projectQueryWrapper.eq("id", inputDto.getProjectId());
            projectQueryWrapper.eq("is_delete", false);
            Integer existCount = projectService.count(projectQueryWrapper);
            if(existCount<=0){
                checkMsgs.add("所属项目不存在");
            }
            //关联模块
            if(inputDto.getModuleIds()!=null && inputDto.getModuleIds().size()>0){
                List<Integer> moduleIds = inputDto.getModuleIds();
                QueryWrapper<ModuleEntity> moduleQueryWrapper = new QueryWrapper<>();
                moduleQueryWrapper.in("id", moduleIds);
                moduleQueryWrapper.eq("is_delete", false);
                List<ModuleEntity> existsModules = moduleService.list(moduleQueryWrapper);
                //如果查询出来的模块信息与不包含当前传入的模块信息，则表示，传入的部分id不存在
                List<Integer> nonExistsIds = moduleIds.stream().filter(s->existsModules.stream().anyMatch(t->t.getId() == s) == false).collect(Collectors.toList());
                if(nonExistsIds.size()>0){
                    checkMsgs.add("关联模块["+  nonExistsIds.stream().map(s->s.toString()).collect(Collectors.joining(",")) +"]不存在");
                }
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            TaskEntity entity = modelMapper.map(inputDto,TaskEntity.class);
            entity.setIsDelete(false);
            entity.setIsArchive(false);
            this.save(entity);
            //保存关联模块
            if(inputDto.getModuleIds()!=null && inputDto.getModuleIds().size()>0){
                for(Integer moduleId: inputDto.getModuleIds()){
                    TaskModuleEntity taskModuleEntity = new TaskModuleEntity();
                    taskModuleEntity.setTaskId(entity.getId());
                    taskModuleEntity.setModuleId(moduleId);

                    taskModuleService.save(taskModuleEntity);
                }
            }
            TaskOutputDto outputDto = modelMapper.map(entity,TaskOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<TaskOutputDto> update(TaskUpdateInputDto inputDto) {
        ResponseData<TaskOutputDto> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //模块名称是否存在
            QueryWrapper<TaskEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", inputDto.getName());
            queryWrapper.eq("is_delete",false);
            queryWrapper.eq("project_id",inputDto.getProjectId());
            queryWrapper.ne("id",inputDto.getId());
            TaskEntity taskEntity = this.getOne(queryWrapper,false);
            if (taskEntity!=null) {
                checkMsgs.add("任务名称已经存在");
            }
            //所属项目是否存在
            QueryWrapper<ProjectEntity> projectQueryWrapper = new QueryWrapper<>();
            projectQueryWrapper.eq("id", inputDto.getProjectId());
            projectQueryWrapper.eq("is_delete", false);
            Integer existCount = projectService.count(projectQueryWrapper);
            if(existCount<=0){
                checkMsgs.add("所属项目不存在");
            }
            //验证关联模块
            List<TaskModuleEntity> existsTaskModuleEntities = null;
            if(inputDto.getModuleIds()!=null && inputDto.getModuleIds().size()>0){
                List<Integer> moduleIds = inputDto.getModuleIds();
                QueryWrapper<ModuleEntity> moduleQueryWrapper = new QueryWrapper<>();
                moduleQueryWrapper.in("id", moduleIds);
                moduleQueryWrapper.eq("is_delete", false);
                List<ModuleEntity> existsModules = moduleService.list(moduleQueryWrapper);
                //如果查询出来的模块信息与不包含当前传入的模块信息，则表示，传入的部分id不存在
                List<Integer> nonExistsIds = moduleIds.stream().filter(s->existsModules.stream().anyMatch(t->t.getId() == s) == false).collect(Collectors.toList());
                if(nonExistsIds.size()>0){
                    checkMsgs.add("关联模块["+  nonExistsIds.stream().map(s->s.toString()).collect(Collectors.joining(",")) +"]不存在");
                }
                //验证关联模块相比旧数据，是否有删除
                QueryWrapper<TaskModuleEntity> taskModuleEntityQueryWrapper = new QueryWrapper<>();
                taskModuleEntityQueryWrapper.eq("task_id",inputDto.getId());
                existsTaskModuleEntities = taskModuleService.list(taskModuleEntityQueryWrapper);
                if(existsTaskModuleEntities!=null && existsTaskModuleEntities.size()>0){
                    if(existsTaskModuleEntities.stream().filter(s->inputDto.getModuleIds().contains(s.getModuleId()) == false).count()>0){
                        checkMsgs.add("修改任务时，只能新增模块，不能删除；因为任务已关联模块可能已经在测试套件中添加接口和测试用例");
                    }
                }
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            TaskEntity entity = modelMapper.map(inputDto,TaskEntity.class);
            entity.setIsDelete(false);
            this.updateById(entity);
            //对于新增的模块，添加到任务模块关联数据
            if(inputDto.getModuleIds()!=null && inputDto.getModuleIds().size()>0){
                //获取新增的模块id
                List<TaskModuleEntity> finalExistsTaskModuleEntities = existsTaskModuleEntities;
                List<Integer> newMoudelIds = inputDto.getModuleIds().stream().filter(s-> finalExistsTaskModuleEntities.stream().filter(t->t.getModuleId() == s).count()<=0).collect(Collectors.toList());
                List<TaskModuleEntity> newTaskModuleEntities = new ArrayList<>();
                for(Integer moduleId: newMoudelIds){
                    TaskModuleEntity taskModuleEntity = new TaskModuleEntity();
                    taskModuleEntity.setTaskId(entity.getId());
                    taskModuleEntity.setModuleId(moduleId);

                    newTaskModuleEntities.add(taskModuleEntity);
                }
                if(newTaskModuleEntities.size()>0){
                    taskModuleService.saveBatch(newTaskModuleEntities);
                }
            }
            TaskOutputDto outputDto = modelMapper.map(entity,TaskOutputDto.class);

            responseData = ResponseData.success(outputDto);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<Boolean> delete(Integer id) {
        ResponseData<Boolean> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //项目名称是否存在
            QueryWrapper<TaskEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",id);
            queryWrapper.eq("is_delete",false);
            TaskEntity taskEntity = this.getOne(queryWrapper,false);
            if (taskEntity!=null) {
                //如果任务下有测试记录，则不能删除
                QueryWrapper<TestRecordEntity> testRecordEntityQueryWrapper = new QueryWrapper<>();
                if(id != null) {
                    testRecordEntityQueryWrapper.eq("task_id", id);
                }
                testRecordEntityQueryWrapper.eq("is_delete",false);
                List <TestRecordEntity> entities = testRecordService.list(testRecordEntityQueryWrapper);
                if(entities!=null && entities.size()>0){
                    checkMsgs.add("任务已有运行记录，不能删除");
                }
            }else{
                checkMsgs.add("任务不存在");
            }

            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            taskEntity.setIsDelete(true);
            Boolean result = this.updateById(taskEntity);

            responseData = ResponseData.success(result);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    @Override
    public ResponseData<List<ModuleOutputDto>> getModulesByTaskId(Integer taskId) {
        ResponseData<List<ModuleOutputDto>> responseData;

        try {
            QueryWrapper<TaskModuleEntity> taskModuleEntityQueryWrapper = new QueryWrapper<>();
            if(taskId != null) {
                taskModuleEntityQueryWrapper.eq("task_id", taskId);
            }
            List <TaskModuleEntity> taskModuleEntities = taskModuleService.list(taskModuleEntityQueryWrapper);
            List<Integer> modudleIds = taskModuleEntities.stream().map(s->s.getModuleId()).collect(Collectors.toList());

            QueryWrapper<ModuleEntity> moduleEntityQueryWrapper = new QueryWrapper<>();
            moduleEntityQueryWrapper.in("id",modudleIds);
            moduleEntityQueryWrapper.eq("is_delete",false);
            List<ModuleEntity> moduleEntities = moduleService.list(moduleEntityQueryWrapper);

            List <ModuleOutputDto> outputDtos = moduleEntities.stream().map(s -> modelMapper.map(s, ModuleOutputDto.class)).collect(Collectors.toList());

            responseData = ResponseData.success(outputDtos);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }

    /**
     * 运行任务
     * @param taskRunInputDto
     * @return
     */
    public ResponseData<Boolean> run(TaskRunInputDto taskRunInputDto){
        ResponseData<Boolean> responseData;

        //数据验证&获取基础数据
        List<String> checkMsgs = new ArrayList <>();
        EnvironmentEntity environmentEntity = null;
        List<InterfaceEntity> interfaceEntities = null;
        List<TestSuitEntity> testSuitEntities = null;
        List<TestCaseEntity> testCaseEntities = null;
        try{
            //获取任务
            TaskEntity taskEntity = this.getById((Serializable)taskRunInputDto.getTaskId());
            if(taskEntity!=null) {
                //获取项目环境
                environmentEntity = environmentService.getById((Serializable) taskRunInputDto.getEnvironmentId());
                if(environmentEntity == null){
                    checkMsgs.add("项目环境不存在");
                }
                //根据测试任务获取关联模块
                QueryWrapper<TaskModuleEntity> queryWrapperOfModules = new QueryWrapper<>();
                queryWrapperOfModules.eq("task_id", taskRunInputDto.getTaskId());
                List<TaskModuleEntity> taskModuleEntities = taskModuleService.list(queryWrapperOfModules);
                //根据模块获取所有接口
                if (taskModuleEntities != null && taskModuleEntities.size() > 0) {
                    QueryWrapper<InterfaceEntity> queryWrapperOfInterfaces = new QueryWrapper<>();
                    queryWrapperOfInterfaces.in("module_id", taskModuleEntities.stream().map(s -> s.getModuleId()).collect(Collectors.toList()));
                    queryWrapperOfInterfaces.eq("is_delete", false);
                    interfaceEntities = interfaceService.list(queryWrapperOfInterfaces);
                    if (interfaceEntities == null || interfaceEntities.size() <= 0) {
                        checkMsgs.add(String.format("任务[%s]关联模块下没有接口", taskEntity.getName()));
                    }
                } else {
                    checkMsgs.add(String.format("任务[%s]没有关联模块", taskEntity.getName()));
                }

                //根据测试任务获取所有套件
                QueryWrapper<TestSuitEntity> testSuitEntityQueryWrapper = new QueryWrapper<>();
                testSuitEntityQueryWrapper.eq("task_id", taskRunInputDto.getTaskId());
                testSuitEntityQueryWrapper.eq("is_delete", false);
                testSuitEntities = testSuitService.list(testSuitEntityQueryWrapper);
                if (testSuitEntities != null && testSuitEntities.size() > 0) {
                    //根据测试套件获取所有测试用例列表
                    QueryWrapper<TestCaseEntity> queryWrapperOfTestCases = new QueryWrapper<>();
                    queryWrapperOfTestCases.in("task_id", taskRunInputDto.getTaskId());
                    queryWrapperOfTestCases.in("test_suit_id", testSuitEntities.stream().map(s -> s.getId()).collect(Collectors.toList()));
                    queryWrapperOfTestCases.eq("is_delete", false);
                    testCaseEntities = testCaseService.list(queryWrapperOfTestCases);
                    if (testCaseEntities == null || testCaseEntities.size() <= 0) {
                        checkMsgs.add(String.format("任务[%s]下没有测试用例", taskEntity.getName()));
                    }
                } else {
                    checkMsgs.add(String.format("任务[%s]下没有测试套件", taskEntity.getName()));
                }
            }else{
                checkMsgs.add("任务不存在");
            }
        }catch (Exception ex){
            checkMsgs.add("获取任务基础数据异常");
        }
        if(checkMsgs.size()>0){
            responseData = new ResponseData <>();
            responseData.setCode(1);
            responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

            return responseData;
        }

        //运行测试任务前，添加一条测试记录，状态为执行中
        TestRecordEntity testRecordEntity =  modelMapper.map(taskRunInputDto,TestRecordEntity.class);
        testRecordEntity.setStatus(1);
        testRecordEntity.setIsDelete(false);
        //会话信息
        Date current = new Date();
        UserEntity userEntity = sessionUtil.getCurrentUser().getUserEntity();
        testRecordEntity.setCreateById(userEntity.getId());
        testRecordEntity.setCreateByName(userEntity.getName());
        testRecordEntity.setCreateTime(current);
        testRecordEntity.setUpdateById(userEntity.getId());
        testRecordEntity.setUpdateByName(userEntity.getName());
        testRecordEntity.setUpdateTime(current);
        testRecordService.save(testRecordEntity);

        //运行测试任务
        List <InterfaceEntity> finalInterfaceEntities = interfaceEntities;
        List <TestCaseEntity> finalTestCaseEntities = testCaseEntities;
        EnvironmentEntity finalEnvironmentEntity = environmentEntity;
        SessionUtil.CurrentUser currentUser =sessionUtil.getCurrentUser();
        new Thread(new Runnable() {
            @SneakyThrows
            @Override public void run() {
                taskTestService.test(finalEnvironmentEntity, finalInterfaceEntities, finalTestCaseEntities,testRecordEntity,currentUser);
            }
        }).start();
        responseData = ResponseData.success();
        return responseData;
    }

    @Override
    public ResponseData<Boolean> changeIsJobOrNot(ChangeIsJobOrNotInputDto inputDto) {
        ResponseData<Boolean> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //任务是否存在
            QueryWrapper<TaskEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",inputDto.getTaskId());
            queryWrapper.eq("is_delete",false);
            TaskEntity taskEntity = this.getOne(queryWrapper,false);
            if (taskEntity==null){
                checkMsgs.add("任务不存在");
            }
            //如果要将测试任务标识为非定时执行，验证任务关联的所有Job是否都已经停止或暂停
            if(inputDto.getIsJob() == false) {
                QueryWrapper<JobEntity> jobEntityQueryWrapper = new QueryWrapper<>();
                jobEntityQueryWrapper.eq("task_id", inputDto.getTaskId());
                jobEntityQueryWrapper.eq("status", JobStatus.Started.ordinal());
                jobEntityQueryWrapper.eq("is_delete", false);
                List<JobEntity> jobEntities = jobService.list(jobEntityQueryWrapper);
                if (jobEntities != null && jobEntities.size() > 0) {
                    checkMsgs.add("测试任务下存在定时执行，请先停止");
                }
            }

            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }

            taskEntity.setIsJob(inputDto.getIsJob());
            Boolean result = this.updateById(taskEntity);

            responseData = ResponseData.success(result);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }


    @Override
    public ResponseData<Boolean> archive(Integer id) {
        ResponseData<Boolean> responseData;

        try {
            List<String> checkMsgs = new ArrayList <>();
            //项目名称是否存在
            QueryWrapper<TaskEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",id);
            queryWrapper.eq("is_delete",false);
            TaskEntity taskEntity = this.getOne(queryWrapper,false);
            if (taskEntity==null) {
                checkMsgs.add("任务不存在");
            }
            //如果要将测试任务归档，验证任务关联的所有Job是否都已经停止或暂停
            QueryWrapper<JobEntity> jobEntityQueryWrapper = new QueryWrapper<>();
            jobEntityQueryWrapper.eq("task_id", id);
            jobEntityQueryWrapper.eq("status", JobStatus.Started.ordinal());
            jobEntityQueryWrapper.eq("is_delete", false);
            List<JobEntity> jobEntities = jobService.list(jobEntityQueryWrapper);
            if (jobEntities != null && jobEntities.size() > 0) {
                checkMsgs.add("测试任务下存在定时执行，请先停止");
            }
            if(checkMsgs.size()>0){
                responseData = new ResponseData <>();
                responseData.setCode(1);
                responseData.setMessage(checkMsgs.stream().collect(Collectors.joining(",")));

                return responseData;
            }
            taskEntity.setIsArchive(true);
            Boolean result = this.updateById(taskEntity);

            responseData = ResponseData.success(result);
        }catch (Exception ex){
            log.error("操作异常：",ex);
            responseData = ResponseData.failure("操作异常："+ex.toString());
        }

        return responseData;
    }
}
