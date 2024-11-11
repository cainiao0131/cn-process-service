package org.cainiao.process.service.processengine;

import org.cainiao.process.dto.response.ProcessInstanceDetail;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.lang.NonNull;

public interface ProcessEngineService {

    String getProcessFormKey(String processDefinitionId, String flowElementId);

    String getProcessFormKeyByProcessInstanceId(String processInstanceId, String elementId);

    ProcessInstanceDetail processInstance(String processInstanceId);

    FlowElement getFlowElement(String processDefinitionId, String flowElementId);

    BpmnModel getBpmnModel(String processDefinitionId);

    ProcessDefinition getLatestVersionProcessDefinition(String processDefinitionTenantId, String processDefinitionKey);

    Integer deployProcessDefinition(@NonNull ProcessDefinitionMetadata processDefinitionMetadata, long systemId);

    void deleteProcessDefinition(long systemId, String processDefinitionKey, String userName);

    void deleteProcessInstance(String processInstanceId, String deleteReason, String userName);
}
