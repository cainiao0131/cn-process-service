package org.cainiao.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cainiao.process.entity.ProcessDefinition;

public interface ProcessDefinitionService {

    IPage<ProcessDefinition> processDefinitions(long systemId, long current, long size, String key);
}
