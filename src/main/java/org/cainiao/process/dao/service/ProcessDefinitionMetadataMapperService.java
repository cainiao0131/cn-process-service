package org.cainiao.process.dao.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cainiao.process.dao.mapper.ProcessDefinitionMetadataMapper;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
public class ProcessDefinitionMetadataMapperService
    extends ServiceImpl<ProcessDefinitionMetadataMapper, ProcessDefinitionMetadata>
    implements IService<ProcessDefinitionMetadata> {

    public IPage<ProcessDefinitionMetadata> searchPageBySystemId(long systemId, long current, long size, String key) {
        IPage<ProcessDefinitionMetadata> page = new Page<>(current, size);
        if (StringUtils.hasText(key)) {
            return page(page, lambdaQuery()
                .eq(ProcessDefinitionMetadata::getSystemId, systemId)
                .and(lambdaQueryWrapper -> lambdaQueryWrapper
                    .like(ProcessDefinitionMetadata::getProcessDefinitionKey, key)
                    .or().like(ProcessDefinitionMetadata::getName, key)
                    .or().like(ProcessDefinitionMetadata::getDescription, key))
                .select(ProcessDefinitionMetadata::getProcessDefinitionKey,
                    ProcessDefinitionMetadata::getName,
                    ProcessDefinitionMetadata::getDescription,
                    ProcessDefinitionMetadata::getVersion,
                    ProcessDefinitionMetadata::getStatus,
                    ProcessDefinitionMetadata::getCreatedAt,
                    ProcessDefinitionMetadata::getCreatedBy,
                    ProcessDefinitionMetadata::getUpdatedAt,
                    ProcessDefinitionMetadata::getUpdatedBy)
                .orderByDesc(ProcessDefinitionMetadata::getUpdatedAt));
        }
        return page(page, lambdaQuery()
            .eq(ProcessDefinitionMetadata::getSystemId, systemId)
            .orderByDesc(ProcessDefinitionMetadata::getUpdatedAt));
    }

    public ProcessDefinitionMetadata processDefinition(long systemId, String processDefinitionKey) {
        return lambdaQuery().eq(ProcessDefinitionMetadata::getSystemId, systemId)
            .eq(ProcessDefinitionMetadata::getProcessDefinitionKey, processDefinitionKey).one();
    }
}
