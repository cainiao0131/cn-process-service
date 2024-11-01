package org.cainiao.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.entity.ProcessDefinition;
import org.cainiao.process.service.ProcessDefinitionService;
import org.springframework.stereotype.Service;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
@RequiredArgsConstructor
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Override
    public IPage<ProcessDefinition> processDefinitions(long systemId, long current, long size, String key) {
        // TODO
        return null;
    }
}
