package org.cainiao.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cainiao.process.entity.ProcessDefinitionMetadata;

public interface ProcessDefinitionMetadataService {

    IPage<ProcessDefinitionMetadata> processDefinitions(long systemId, long current, long size, String key);
}
