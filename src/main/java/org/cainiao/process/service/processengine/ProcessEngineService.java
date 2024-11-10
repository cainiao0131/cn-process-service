package org.cainiao.process.service.processengine;

import org.cainiao.process.dto.response.ProcessInstanceDetail;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.repository.ProcessDefinition;

public interface ProcessEngineService {

    ProcessInstanceDetail processInstance(String processInstanceId);

    FlowElement getFlowElement(String processDefinitionId, String flowElementId);

    BpmnModel getBpmnModel(String processDefinitionId);

    ProcessDefinition getLatestVersionProcessDefinition(String processDefinitionTenantId, String processDefinitionKey);
}
