package org.cainiao.process.dao.service;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dao.mapper.SystemMetadataMapper;
import org.cainiao.process.entity.SystemMetadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
public class SystemMetadataMapperService extends ServiceImpl<SystemMetadataMapper, SystemMetadata>
    implements IService<SystemMetadata> {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void saveOrUpdateBySystemId(long systemId, String userName, SystemMetadata systemMetadata) {
        LocalDateTime now = LocalDateTime.now();
        if (lambdaQuery().eq(SystemMetadata::getSystemId, systemId).exists()) {
            // 更新
            LambdaUpdateChainWrapper<SystemMetadata> updateWrapper = lambdaUpdate();
            updateWrapper.eq(SystemMetadata::getSystemId, systemId);
            updateWrapper.set(SystemMetadata::getWebhook, systemMetadata.getWebhook());
            updateWrapper.set(SystemMetadata::getUpdatedBy, userName);
            updateWrapper.set(SystemMetadata::getUpdatedAt, now);
            if (!update(updateWrapper)) {
                throw new BusinessException("编辑系统元数据失败！");
            }
        } else {
            // 新增
            systemMetadata.setId(null);
            systemMetadata.setSystemId(systemId);
            systemMetadata.setCreatedBy(userName);
            systemMetadata.setUpdatedBy(userName);
            systemMetadata.setCreatedAt(now);
            systemMetadata.setUpdatedAt(now);
            if (!save(systemMetadata)) {
                throw new BusinessException("添加系统元数据失败！");
            }
        }
    }
}
