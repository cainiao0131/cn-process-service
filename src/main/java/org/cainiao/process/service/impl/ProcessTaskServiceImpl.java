package org.cainiao.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dao.service.FormVersionMapperService;
import org.cainiao.process.dto.form.FormItem;
import org.cainiao.process.dto.form.FormItemConfig;
import org.cainiao.process.dto.request.ReassignTaskRequest;
import org.cainiao.process.dto.response.ProcessActivityResponse;
import org.cainiao.process.dto.response.ProcessTaskResponse;
import org.cainiao.process.dto.response.VariableInfo;
import org.cainiao.process.entity.FormVersion;
import org.cainiao.process.service.ProcessTaskService;
import org.cainiao.process.service.processengine.ProcessEngineService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cainiao.process.util.JsonUtil.jsonToList;
import static org.cainiao.process.util.TimeUtil.SIMPLE_DATE_FORMAT;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
@RequiredArgsConstructor
public class ProcessTaskServiceImpl implements ProcessTaskService {

    private final FormVersionMapperService formVersionMapperService;

    private final ProcessEngineService processEngineService;

    private final TaskService taskService;
    private final HistoryService historyService;

    @Override
    public void completeTask(String taskId, Map<String, Object> localVariables,
                             Map<String, Object> processVariables, String userName) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String assignee = task.getAssignee();
        if (!StringUtils.hasText(assignee)) {
            throw new BusinessException("任务没有委托人!");
        }
        if (!assignee.equals(userName)) {
            throw new BusinessException("只能完成自己的任务!");
        }
        if (localVariables != null && !localVariables.isEmpty()) {
            taskService.setVariablesLocal(taskId, localVariables);
        }
        if (processVariables != null && !processVariables.isEmpty()) {
            taskService.complete(taskId, userName, processVariables);
        } else {
            taskService.complete(taskId, userName);
        }
    }

    @Override
    public IPage<ProcessActivityResponse> taskActivities(String processInstanceId, String elementId,
                                                         long current, int size) {
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(processInstanceId).taskDefinitionKey(elementId);
        long count = historicTaskInstanceQuery.count();
        List<HistoricTaskInstance> historicTaskInstances = historicTaskInstanceQuery
            .orderByHistoricTaskInstanceStartTime().desc().listPage((int) ((current - 1) * size), size);
        List<ProcessActivityResponse> taskHistoryRecords = new ArrayList<>();
        for (HistoricTaskInstance taskInstance : historicTaskInstances) {
            String taskInstanceId = taskInstance.getId();
            taskHistoryRecords.add(ProcessActivityResponse.builder()
                .assignee(taskInstance.getAssignee())
                .activityInstanceId(taskInstanceId)
                .createTime(taskInstance.getCreateTime())
                .endTime(taskInstance.getEndTime())
                .endReason(taskInstance.getDeleteReason())
                .variables(getVariables(historyService
                    .createHistoricVariableInstanceQuery().taskId(taskInstanceId).list(), taskInstance.getFormKey()))
                .state(taskInstance.getState())
                .build());
        }
        IPage<ProcessActivityResponse> page = new Page<>(current, size);
        page.setRecords(taskHistoryRecords);
        page.setTotal(count);
        return page;
    }

    @Override
    public IPage<ProcessActivityResponse> processInstanceActivities(String processInstanceId, long current, int size) {
        Set<String> activityTypes = new HashSet<>();
        activityTypes.add("startEvent");
        activityTypes.add("userTask");
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService
            .createHistoricActivityInstanceQuery().activityTypes(activityTypes).processInstanceId(processInstanceId);

        long count = historicActivityInstanceQuery.count();

        List<HistoricActivityInstance> activities = historicActivityInstanceQuery
            .orderByHistoricActivityInstanceStartTime()
            .desc()
            .orderByHistoricActivityInstanceEndTime()
            .desc()
            .listPage((int) ((current - 1) * size), size);
        List<ProcessActivityResponse> activityRecords = new ArrayList<>();
        if (!activities.isEmpty()) {
            List<HistoricVariableInstance> variableInstances = historyService
                .createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
            HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            ProcessActivityResponse firstProcessActivity = null;
            for (HistoricActivityInstance activityInstance : activities) {
                String activityType = activityInstance.getActivityType();
                String deleteReason = activityInstance.getDeleteReason();
                if ("userTask".equalsIgnoreCase(activityType) && "MI_END".equalsIgnoreCase(deleteReason)) {
                    /*
                     * "MI_END" 表示当前记录是多人任务，因为任务结束而被删除的活动实例
                     * 即这个活动的用户并没有完成这个任务，因此排除掉
                     */
                    continue;
                }
                String activityId = activityInstance.getActivityId();
                String activityInstanceId = activityInstance.getId();
                String activityName = activityInstance.getActivityName();
                ProcessActivityResponse workflowActivity = ProcessActivityResponse.builder()
                    .activityInstanceId(activityInstanceId)
                    .activityId(activityId).activityName(activityName).activityType(activityType)
                    .createTime(activityInstance.getStartTime()).endTime(activityInstance.getEndTime())
                    .endReason(deleteReason).assignee(activityInstance.getAssignee()).build();
                if ("startEvent".equalsIgnoreCase(activityType)) {
                    workflowActivity.setVariables(getVariables(variableInstances,
                        processEngineService.getProcessFormKey(processDefinitionId, activityInstance.getActivityId())
                    ));
                } else if ("userTask".equalsIgnoreCase(activityType)) {
                    workflowActivity.setVariables(getVariables(
                        historyService.createHistoricVariableInstanceQuery()
                            .taskId(activityInstance.getTaskId()).list(),
                        processEngineService.getProcessFormKey(processDefinitionId, activityInstance.getActivityId())));
                }
                if ("startEvent".equalsIgnoreCase(activityType) && firstProcessActivity == null) {
                    // 开始事件放到最后去，避免开始事件与第一个用户任务的 start time 相同时的排序错误
                    firstProcessActivity = workflowActivity;
                } else {
                    activityRecords.add(workflowActivity);
                }
            }
            if (firstProcessActivity != null) {
                activityRecords.add(firstProcessActivity);
            }
        }
        IPage<ProcessActivityResponse> page = new Page<>(current, size);
        page.setRecords(activityRecords);
        page.setTotal(count);
        return page;
    }

    @Override
    public ProcessActivityResponse startEventActivity(String processInstanceId, String elementId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        // TODO 不应该传递开始事件 ID，测试一下开始事件的 elementId 是否等于 historicProcessInstance.getStartActivityId();
        return ProcessActivityResponse.builder().assignee(historicProcessInstance.getStartUserId())
            .createTime(historicProcessInstance.getStartTime())
            .variables(getVariables(historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstanceId).list(),
                processEngineService.getProcessFormKeyByProcessInstanceId(processInstanceId, elementId))).build();
    }

    private List<VariableInfo> getVariables(List<HistoricVariableInstance> variableInstances, String processFormKey) {
        if (!StringUtils.hasText(processFormKey)) {
            return Collections.emptyList();
        }
        if (variableInstances.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, String> labelMap = getLabelMap(jsonToList(formVersionMapperService
            .fetchByProcessFormKey(processFormKey).getFormItems(), FormItem.class));
        return variableInstances.stream().map(historicVariableInstance -> {
            String variableName = historicVariableInstance.getVariableName();
            String label = labelMap.get(variableName);
            return VariableInfo.builder()
                .label(StringUtils.hasText(label) ? label : variableName)
                .value(historicVariableInstance.getValue())
                .build();
        }).collect(Collectors.toList());
    }

    private @NonNull Map<String, String> getLabelMap(List<FormItem> formItems) {
        if (formItems == null || formItems.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        formItems.forEach(formItem -> {
            FormItemConfig config = formItem.getConfig();
            map.put(config.getName(), config.getLabel());
            map.putAll(getLabelMap(formItem.getChildren()));
        });
        return map;
    }

    @Override
    public void reassignOwnTask(String taskId, ReassignTaskRequest reassignTaskRequest, String userName) {
        String toUserName = reassignTaskRequest.getToUserName();
        if (userName.equals(toUserName)) {
            return;
        }
        String assignee = taskService.createTaskQuery().taskId(taskId).singleResult().getAssignee();
        if (!StringUtils.hasText(assignee) || !assignee.equals(userName)) {
            throw new BusinessException("只能对分配给自己的任务进行改派!");
        }
        taskService.setAssignee(taskId, toUserName);
    }

    @Override
    public IPage<ProcessTaskResponse> tasks(String userName, String processInstanceId,
                                            long current, int size, String searchKey) {
        TaskQuery taskQuery = taskService.createTaskQuery().taskAssignee(userName);
        if (StringUtils.hasText(processInstanceId)) {
            taskQuery.processInstanceId(processInstanceId);
        }
        if (StringUtils.hasText(searchKey)) {
            searchKey = String.format("%%%s%%", searchKey);
            taskQuery = taskQuery.or().taskNameLike(searchKey).taskDescriptionLike(searchKey).endOr();
        }
        long count = taskQuery.count();
        List<Task> tasks = taskQuery.orderByTaskCreateTime().desc().listPage((int) (current - 1) * size, size);
        List<ProcessTaskResponse> taskList = tasks.stream()
            .map(task -> ProcessTaskResponse.from(task, SIMPLE_DATE_FORMAT)).toList();
        IPage<ProcessTaskResponse> page = new Page<>(current, size);
        page.setRecords(taskList);
        page.setTotal(count);
        return page;
    }

    @Override
    public ProcessTaskResponse task(String taskId) {
        return taskDetail(taskService.createTaskQuery().taskId(taskId).singleResult());
    }

    private ProcessTaskResponse taskDetail(Task task) {
        ProcessTaskResponse processTaskResponse = ProcessTaskResponse.from(task, SIMPLE_DATE_FORMAT);

        // 流程变量，用于展示任务上下文
        processTaskResponse.setProcessInstanceDetail(processEngineService.processInstance(task.getProcessInstanceId()));
        processTaskResponse.setElementId(processEngineService
            .getFlowElement(task.getProcessDefinitionId(), task.getTaskDefinitionKey()).getId());

        // 任务表单
        String processFormKey = processTaskResponse.getFormKey();
        if (StringUtils.hasText(processFormKey)) {
            FormVersion formVersion = formVersionMapperService.fetchByProcessFormKey(processFormKey);
            processTaskResponse.setFormConfig(formVersion.getFormConfig());
            processTaskResponse.setFormItems(formVersion.getFormItems());
        }
        return processTaskResponse;
    }
}
