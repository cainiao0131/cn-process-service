package org.cainiao.process.service.impl;

import lombok.RequiredArgsConstructor;
import org.cainiao.process.dao.service.SystemMetadataMapperService;
import org.cainiao.process.entity.SystemMetadata;
import org.cainiao.process.service.SystemMetadataService;
import org.springframework.stereotype.Service;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
@RequiredArgsConstructor
public class SystemMetadataServiceImpl implements SystemMetadataService {

    private final SystemMetadataMapperService systemMetadataMapperService;

    @Override
    public void setSystemMetadata(long systemId, String userName, SystemMetadata systemMetadata) {
        systemMetadataMapperService.saveOrUpdateBySystemId(systemId, userName, systemMetadata);
    }

    @Override
    public SystemMetadata getSystemMetadata(Long systemId) {
        if (systemId == null) {
            return null;
        }
        return systemMetadataMapperService.getOne(systemId);
    }
}
