package org.cainiao.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dao.service.FormMapperService;
import org.cainiao.process.dao.service.FormVersionMapperService;
import org.cainiao.process.dao.service.ProcessDefinitionMetadataMapperService;
import org.cainiao.process.dto.form.FormItem;
import org.cainiao.process.dto.form.FormItemConfig;
import org.cainiao.process.dto.response.*;
import org.cainiao.process.entity.FormVersion;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.cainiao.process.service.ProcessService;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.cainiao.process.util.JsonUtil.jsonToList;
import static org.cainiao.process.util.ProcessUtil.validateForm;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private final ProcessDefinitionMetadataMapperService processDefinitionMetadataMapperService;
    private final FormVersionMapperService formVersionMapperService;
    private final FormMapperService formMapperService;

    private final ProcessEngine processEngine;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final FormService formService;

    /**
     * @Override public FormVersion fetchByFlowFormKey(String flowFormKey) {
     * String[] startFormInfo = flowFormKey.split(":");
     * return null;
     * }
     */

    @Override
    public IPage<ProcessDefinitionMetadata> processDefinitions(long systemId,
                                                               long current, long size, String searchKey) {
        return processDefinitionMetadataMapperService.searchPageBySystemId(systemId, current, size, searchKey);
    }

    @Override
    public ProcessDefinitionMetadata processDefinition(long systemId, String processDefinitionKey) {
        return processDefinitionMetadataMapperService.processDefinition(systemId, processDefinitionKey);
    }

    @Override
    public IPage<ProcessInstanceResponse> processInstances(long systemId, String processDefinitionKey,
                                                           Boolean finished, long current, long size) {
        if (!processDefinitionMetadataMapperService.exists(systemId, processDefinitionKey)) {
            throw new BusinessException("未找到流程定义");
        }

        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService
            .createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey);
        if (finished != null) {
            if (finished) {
                historicProcessInstanceQuery.finished();
            } else {
                historicProcessInstanceQuery.unfinished();
            }
        }

        int count = (int) historicProcessInstanceQuery.count();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        IPage<ProcessInstanceResponse> page = new Page<>(current, size);
        page.setRecords(historicProcessInstanceQuery.orderByProcessInstanceStartTime().desc()
            .listPage((int) ((current - 1) * size), (int) size)
            .stream().map(historicProcessInstance -> {
                ProcessInstanceResponse processInstanceResponse = ProcessInstanceResponse
                    .from(historicProcessInstance, simpleDateFormat);
                if (!processInstanceResponse.isEnded()) {
                    // 未结束的流程，查询一下：哪些节点正在执行、是否处于暂停状态
                    List<Execution> executions = runtimeService.createExecutionQuery()
                        .processInstanceId(historicProcessInstance.getId())
                        .list();
                    if (executions != null && !executions.isEmpty()) {
                        processInstanceResponse.setActivityIds(executions.stream()
                            .map(Execution::getActivityId).filter(Objects::nonNull).toList());
                    }
                    processInstanceResponse.setSuspended(processInstanceQuery
                        .processInstanceId(historicProcessInstance.getId()).singleResult().isSuspended());
                }
                return processInstanceResponse;
            }).toList());
        page.setTotal(count);
        return page;
    }

    @Override
    public ProcessStartEventResponse startProcess(long systemId, String userName,
                                                  String processDefinitionKey, Map<String, Object> variables) {
        if (!processDefinitionMetadataMapperService.exists(systemId, processDefinitionKey)) {
            throw new BusinessException("未找到流程定义");
        }

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionTenantId(String.valueOf(systemId))
            .processDefinitionKey(processDefinitionKey)
            .latestVersion()
            .singleResult();
        String processDefinitionId = processDefinition.getId();

        // 流程开始事件是否需要填写表单
        if (processDefinition.hasStartFormKey()) {
            String formKey = formService.getStartFormKey(processDefinitionId);
            FormVersion formVersion = formVersionMapperService.fetchByProcessFormKey(formKey);
            return ProcessStartEventResponse.builder()
                .formName(formMapperService.fetchByKey(formKey).getName())
                .processDefinitionId(processDefinitionId)
                .needForm(true)
                .formConfig(formVersion.getFormConfig())
                .formItems(formVersion.getFormItems())
                .build();
        }

        return ProcessStartEventResponse.builder()
            .processInstanceId(startFlowByDefinitionId(userName, processDefinitionId, variables).getProcessInstanceId())
            .processDefinitionId(processDefinitionId).build();
    }

    @Override
    public ProcessInstance startFlowByFormAndDefinitionId(String userName, String processDefinitionId,
                                                          @Nullable Map<String, Object> variables) {
        // 校验表单，即 formItems 中的必填项在 variables 中是否都有正确类型的值
        validateForm(formVersionMapperService
            .fetchByProcessFormKey(formService.getStartFormKey(processDefinitionId)), variables);
        return startFlowByDefinitionId(userName, processDefinitionId, variables);
    }

    private ProcessInstance startFlowByDefinitionId(String userName, String processDefinitionId,
                                                    @Nullable Map<String, Object> variables) {
        try {
            // 设置发起流程的用户 ID
            Authentication.setAuthenticatedUserId(userName);
            // 开始流程
            return runtimeService
                .startProcessInstanceById(processDefinitionId, variables == null ? new HashMap<>() : variables);
        } finally {
            Authentication.setAuthenticatedUserId(null);
        }
    }

    @Override
    public ProcessInstanceDetail processInstance(String processInstanceId) {
        ProcessInstance processInstance = runtimeService
            .createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 正在执行的节点 ID
        Set<String> activeActivityIds = getActiveActivityIds(processInstanceId);
        /*
         * 一个活动，可能被重复执行过多次，这会导致一个节点查出多个 HistoricActivityInstance 记录
         * 这里是绘图用的，需要去重，取最新的那个的状态，并且要排除那些正在执行的节点
         */
        Set<String> finishedActivityIds = new HashSet<>();
        historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId).finished().orderByHistoricActivityInstanceEndTime().desc().list()
            .forEach(historicActivityInstance -> {
                String activityId = historicActivityInstance.getActivityId();
                if (!activeActivityIds.contains(activityId)) {
                    finishedActivityIds.add(activityId);
                }
            });
        return ProcessInstanceDetail.builder()
            .processInstanceId(processInstanceId)
            .xml(new String(new BpmnXMLConverter().convertToXML(repositoryService
                .getBpmnModel(processInstance.getProcessDefinitionId())), StandardCharsets.UTF_8))
            .finishedActivityIds(new ArrayList<>(finishedActivityIds))
            .activeActivityIds(new ArrayList<>(activeActivityIds))
            .build();
    }

    /**
     * 获取流程实例中正在执行的节点 ID
     *
     * @param processInstanceId 流程实例 ID
     * @return 流程实例中正在执行的节点 ID 列表
     */
    private Set<String> getActiveActivityIds(String processInstanceId) {
        Set<String> activityIds = new HashSet<>();
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        if (executions != null && !executions.isEmpty()) {
            activityIds.addAll(executions.stream().map(Execution::getActivityId).filter(Objects::nonNull).toList());
        }
        return activityIds;
    }

    @Override
    public IPage<ProcessActivity> processInstanceActivities(String processInstanceId, long current, long size) {
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
            .listPage((int) ((current - 1) * size), (int) size);
        List<ProcessActivity> activityRecords = new ArrayList<>();
        if (!activities.isEmpty()) {
            List<HistoricVariableInstance> variableInstances = historyService
                .createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
            HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            ProcessActivity firstProcessActivity = null;
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
                ProcessActivity workflowActivity = ProcessActivity.builder().activityInstanceId(activityInstanceId)
                    .activityId(activityId).activityName(activityName).activityType(activityType)
                    .createTime(activityInstance.getStartTime()).endTime(activityInstance.getEndTime())
                    .endReason(deleteReason).assignee(activityInstance.getAssignee()).build();
                if ("startEvent".equalsIgnoreCase(activityType)) {
                    workflowActivity.setVariables(getVariables(variableInstances,
                        getProcessFormKey(processDefinitionId, activityInstance.getActivityId())
                    ));
                } else if ("userTask".equalsIgnoreCase(activityType)) {
                    workflowActivity.setVariables(getVariables(
                        historyService.createHistoricVariableInstanceQuery()
                            .taskId(activityInstance.getTaskId()).list(),
                        getProcessFormKey(processDefinitionId, activityInstance.getActivityId())));
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
        IPage<ProcessActivity> page = new Page<>(current, size);
        page.setRecords(activityRecords);
        page.setTotal(count);
        return page;
    }

    private String getProcessFormKey(String processDefinitionId, String elementId) {
        FlowElement flowElement = repositoryService.getBpmnModel(processDefinitionId).getFlowElement(elementId);
        if (flowElement instanceof UserTask userTask) {
            return userTask.getFormKey();
        }
        if (flowElement instanceof StartEvent startEvent) {
            return startEvent.getFormKey();
        }
        return null;
    }

    private String getProcessFormKeyByProcessInstanceId(String processInstanceId, String elementId) {
        return getProcessFormKey(runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult().getProcessDefinitionId(), elementId);
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

    public List<FormVariable> taskVariables(String processInstanceId, String elementId) {
        String processFormKey = getProcessFormKeyByProcessInstanceId(processInstanceId, elementId);
        if (!StringUtils.hasText(processFormKey)) {
            return Collections.emptyList();
        }
        FormVersion formVersion = formVersionMapperService.fetchByProcessFormKey(processFormKey);
        List<FormItem> formItems = jsonToList(formVersion.getFormItems(), FormItem.class);
        List<FormVariable> taskVariables = new ArrayList<>();
        formItems.forEach(formItem -> {
            FormItemConfig config = formItem.getConfig();
            Object value = runtimeService.getVariable(processInstanceId, config.getName());
            taskVariables.add(FormVariable.builder()
                .type(formItem.getType())
                .name(config.getLabel())
                .value(value == null ? "" : value.toString())
                .build());
        });
        return taskVariables;
    }

    @Override
    public ResponseEntity<Resource> processDiagram(String processInstanceId) throws IOException {
        ProcessInstance processInstance = runtimeService
            .createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        List<String> activityIds = new ArrayList<>();
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        if (executions != null && !executions.isEmpty()) {
            activityIds.addAll(executions.stream().map(Execution::getActivityId).filter(Objects::nonNull).toList());
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        try (InputStream inputStream = diagramGenerator
            .generateDiagram(bpmnModel, "png", activityIds, new ArrayList<>(),
                processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(),
                processEngineConfiguration.getAnnotationFontName(),
                processEngineConfiguration.getClassLoader(), 2.0, true)) {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
        }
    }
}
