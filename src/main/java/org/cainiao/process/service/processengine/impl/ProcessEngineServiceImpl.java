package org.cainiao.process.service.processengine.impl;

import lombok.RequiredArgsConstructor;
import org.cainiao.process.dto.response.ProcessInstanceDetail;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.cainiao.process.service.processengine.ProcessEngineService;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
@RequiredArgsConstructor
public class ProcessEngineServiceImpl implements ProcessEngineService {

    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;

    @Override
    public Deployment deployProcessDefinition(@NonNull ProcessDefinitionMetadata processDefinitionMetadata,
                                              long systemId) {
        /*
         * 将服务任务的 type 由 bpmn:ServiceTask 改为空字符串，否则在部署的校验阶段会抛异常：无效的 type
         * 只有 camel 或空字符串不会进行任何校验，但是设置为 camel 后边的相关操作又会失败，因此只能设置为空字符串
         */
        String xml = processDefinitionMetadata.getXml().replace("bpmn:ServiceTask", "");
        return repositoryService.createDeployment()
            .addInputStream(processDefinitionMetadata.getProcessDefinitionKey() + ".bpmn",
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)))
            .category(processDefinitionMetadata.getCategory())
            .tenantId(String.valueOf(systemId))
            .deploy();
    }

    @Override
    public String getProcessFormKey(String processDefinitionId, String flowElementId) {
        FlowElement flowElement = getFlowElement(processDefinitionId, flowElementId);
        if (flowElement instanceof UserTask userTask) {
            return userTask.getFormKey();
        }
        if (flowElement instanceof StartEvent startEvent) {
            return startEvent.getFormKey();
        }
        return null;
    }

    @Override
    public String getProcessFormKeyByProcessInstanceId(String processInstanceId, String elementId) {
        return getProcessFormKey(runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult().getProcessDefinitionId(), elementId);
    }

    @Override
    public ProcessDefinition getLatestVersionProcessDefinition(String processDefinitionTenantId,
                                                               String processDefinitionKey) {
        return repositoryService.createProcessDefinitionQuery()
            .processDefinitionTenantId(processDefinitionTenantId)
            .processDefinitionKey(processDefinitionKey)
            .latestVersion()
            .singleResult();
    }

    @Override
    public BpmnModel getBpmnModel(String processDefinitionId) {
        return repositoryService.getBpmnModel(processDefinitionId);
    }

    @Override
    public FlowElement getFlowElement(String processDefinitionId, String flowElementId) {
        return getBpmnModel(processDefinitionId).getFlowElement(flowElementId);
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
}
