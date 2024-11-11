package org.cainiao.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cainiao.process.dto.response.ProcessInstanceDetail;
import org.cainiao.process.dto.response.ProcessInstanceResponse;
import org.cainiao.process.dto.response.ProcessStartEventResponse;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.Map;

public interface ProcessService {

    void setProcessDefinitionMetadata(Long systemId, ProcessDefinitionMetadata processDefinitionMetadata);

    IPage<ProcessDefinitionMetadata> processDefinitions(long systemId, long current, int size, String searchKey);

    ProcessDefinitionMetadata processDefinition(long systemId, String processDefinitionKey);

    IPage<ProcessInstanceResponse> processInstances(long systemId, String processDefinitionKey,
                                                    Boolean finished, long current, int size);

    ProcessStartEventResponse startProcess(long systemId, String userName,
                                           String processDefinitionKey, Map<String, Object> variables);

    ProcessInstance startFlowByFormAndDefinitionId(String userName, String processDefinitionId,
                                                   @Nullable Map<String, Object> variables);

    ProcessInstanceDetail processInstance(String processInstanceId);

    ResponseEntity<Resource> processDiagram(String processInstanceId) throws IOException;

    void deleteProcessDefinition(String processDefinitionKey);
}
