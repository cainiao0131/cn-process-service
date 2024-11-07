package org.cainiao.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.dao.service.ProcessDefinitionMetadataMapperService;
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
    public IPage<ProcessDefinitionMetadata> processDefinitions(long systemId, long current, long size, String key) {
        return processDefinitionMetadataMapperService.searchPageBySystemId(systemId, current, size, key);
    }
}
