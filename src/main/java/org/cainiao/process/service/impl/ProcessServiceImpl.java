package org.cainiao.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dao.service.FormMapperService;
import org.cainiao.process.dao.service.FormVersionMapperService;
import org.cainiao.process.dao.service.ProcessDefinitionMetadataMapperService;
import org.cainiao.process.dto.response.ProcessInstanceDetail;
import org.cainiao.process.dto.response.ProcessInstanceResponse;
import org.cainiao.process.dto.response.ProcessStartEventResponse;
import org.cainiao.process.entity.FormVersion;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.cainiao.process.service.ProcessService;
import org.cainiao.process.service.processengine.ProcessEngineService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.image.ProcessDiagramGenerator;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.cainiao.process.util.ProcessUtil.validateForm;
import static org.cainiao.process.util.TimeUtil.SIMPLE_DATE_FORMAT;

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

    private final ProcessEngineService processEngineService;

    private final ProcessEngine processEngine;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final FormService formService;

    @Override
    public void setProcessDefinitionMetadata(Long systemId, ProcessDefinitionMetadata processDefinitionMetadata) {
        // TODO
    }

    @Override
    public void deleteProcessDefinition(String processDefinitionKey) {
        // TODO
    }

    @Override
    public IPage<ProcessDefinitionMetadata> processDefinitions(long systemId,
                                                               long current, int size, String searchKey) {
        return processDefinitionMetadataMapperService.searchPageBySystemId(systemId, current, size, searchKey);
    }

    @Override
    public ProcessDefinitionMetadata processDefinition(long systemId, String processDefinitionKey) {
        return processDefinitionMetadataMapperService.processDefinition(systemId, processDefinitionKey);
    }

    @Override
    public IPage<ProcessInstanceResponse> processInstances(long systemId, String processDefinitionKey,
                                                           Boolean finished, long current, int size) {
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
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        IPage<ProcessInstanceResponse> page = new Page<>(current, size);
        page.setRecords(historicProcessInstanceQuery.orderByProcessInstanceStartTime().desc()
            .listPage((int) ((current - 1) * size), size)
            .stream().map(historicProcessInstance -> {
                ProcessInstanceResponse processInstanceResponse = ProcessInstanceResponse
                    .from(historicProcessInstance, SIMPLE_DATE_FORMAT);
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
        ProcessDefinition processDefinition = processEngineService
            .getLatestVersionProcessDefinition(String.valueOf(systemId), processDefinitionKey);
        String processDefinitionId = processDefinition.getId();
        // 流程开始事件是否需要填写表单
        if (processDefinition.hasStartFormKey()) {
            String processFormKey = formService.getStartFormKey(processDefinitionId);
            FormVersion formVersion = formVersionMapperService.fetchByProcessFormKey(processFormKey);
            return ProcessStartEventResponse.builder()
                .formName(formMapperService.fetchByKey(formVersion.getFormKey()).getName())
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
        return processEngineService.processInstance(processInstanceId);
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

        BpmnModel bpmnModel = processEngineService.getBpmnModel(processInstance.getProcessDefinitionId());
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
