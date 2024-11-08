package org.cainiao.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cainiao.process.dto.response.ProcessInstanceResponse;
import org.cainiao.process.dto.response.ProcessStartEventResponse;
import org.cainiao.process.entity.ProcessDefinitionMetadata;

import java.util.Map;

public interface ProcessDefinitionMetadataService {

    IPage<ProcessDefinitionMetadata> processDefinitions(long systemId, long current, long size, String key);

    ProcessDefinitionMetadata processDefinition(long systemId, String processDefinitionKey);

    IPage<ProcessInstanceResponse> processInstances(long systemId, String processDefinitionKey,
                                                    Boolean finished, long current, long size);

    ProcessStartEventResponse startProcess(long systemId, String userName,
                                           String processDefinitionKey, Map<String, Object> variables);
}
