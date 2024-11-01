package org.cainiao.process.service;

import org.cainiao.process.entity.SystemMetadata;

public interface SystemMetadataService {

    void setSystemMetadata(long systemId, String userName, SystemMetadata systemMetadata);
}
