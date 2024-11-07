package org.cainiao.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.dao.service.ProcessDefinitionMetadataMapperService;
import org.cainiao.process.dto.response.ProcessInstanceResponse;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.cainiao.process.service.ProcessDefinitionMetadataService;
import org.springframework.stereotype.Service;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
@RequiredArgsConstructor
public class ProcessDefinitionMetadataServiceImpl implements ProcessDefinitionMetadataService {

    private final ProcessDefinitionMetadataMapperService processDefinitionMetadataMapperService;

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
        // TODO
        return null;
    }
}
