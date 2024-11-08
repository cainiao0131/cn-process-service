package org.cainiao.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dao.service.FormVersionMapperService;
import org.cainiao.process.dao.service.ProcessDefinitionMetadataMapperService;
import org.cainiao.process.dto.response.ProcessInstanceResponse;
import org.cainiao.process.dto.response.ProcessStartEventResponse;
import org.cainiao.process.entity.Form;
import org.cainiao.process.entity.FormVersion;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.cainiao.process.service.ProcessDefinitionMetadataService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
@RequiredArgsConstructor
public class ProcessDefinitionMetadataServiceImpl implements ProcessDefinitionMetadataService {

    private final ProcessDefinitionMetadataMapperService processDefinitionMetadataMapperService;
    private final FormVersionMapperService formVersionMapperService;

    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;

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
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionTenantId(String.valueOf(systemId))
            .processDefinitionKey(processDefinitionKey)
            .latestVersion()
            .singleResult();
        String processDefinitionId = processDefinition.getId();

        // 流程开始事件是否需要填写表单
        if (processDefinition.hasStartFormKey()) {
            FormVersion formVersion = formVersionMapperService
                .fetchByFlowFormKey(formService.getStartFormKey(processDefinitionId));
            Form form = flowFormService.fetchFormByKey(formVersion.getFormKey());
            return ProcessStartEventResponse.builder()
                .formName(form.getName())
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
}
