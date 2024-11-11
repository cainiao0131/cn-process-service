package org.cainiao.process.dao.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dao.mapper.ProcessDefinitionMetadataMapper;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.cainiao.process.entity.ProcessDefinitionMetadata.StatusEnum;
import org.cainiao.process.service.processengine.ProcessEngineService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.cainiao.process.util.Util.fixString;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
public class ProcessDefinitionMetadataMapperService
    extends ServiceImpl<ProcessDefinitionMetadataMapper, ProcessDefinitionMetadata>
    implements IService<ProcessDefinitionMetadata> {

    public void saveOrUpdateProcessDefinitionMetadata(
        Long systemId, ProcessDefinitionMetadata processDefinitionMetadata, String userName,
        ProcessEngineService processEngineService) {

        Long currentId = processDefinitionMetadata.getId();
        ProcessDefinitionMetadata old = processDefinition(currentId);
        LambdaQueryChainWrapper<ProcessDefinitionMetadata> sameNameCnd = getSystemLambdaQuery(systemId)
            .eq(ProcessDefinitionMetadata::getName, processDefinitionMetadata.getName());
        if (old == null) {
            // 添加
            if (lambdaQuery(systemId, processDefinitionMetadata.getProcessDefinitionKey()).exists()) {
                throw new BusinessException("流程定义 Key 重复!");
            }
            if (sameNameCnd.exists()) {
                throw new BusinessException("流程名称重复!");
            }
            processDefinitionMetadata.setId(null);
            processDefinitionMetadata.setCreatedBy(userName);
            processDefinitionMetadata.setUpdatedBy(userName);
            LocalDateTime now = LocalDateTime.now();
            processDefinitionMetadata.setCreatedAt(now);
            processDefinitionMetadata.setUpdatedAt(now);
            if (!save(processDefinitionMetadata)) {
                throw new BusinessException("添加流程定义元数据失败！");
            }
        } else {
            // 编辑
            if (!old.getProcessDefinitionKey().equals(processDefinitionMetadata.getProcessDefinitionKey())) {
                throw new BusinessException("不允许修改流程定义 Key!");
            }
            if (sameNameCnd.ne(ProcessDefinitionMetadata::getId, currentId).exists()) {
                throw new BusinessException("流程名称重复!");
            }
            if (StatusEnum.DEPLOYED.equals(old.getStatus())
                && (!fixString(old.getXml()).equals(fixString(processDefinitionMetadata.getXml()))
                || !Objects.equals(old.getCategory(), processDefinitionMetadata.getCategory()))) {

                // 如果这个流程定义已经部署过，且本次编辑修改了流程定义的 XML 或修改了类型，则重新部署
                old.setVersion(processEngineService.deployProcessDefinition(processDefinitionMetadata, systemId));
                updateProcessDefinitionMetadataById(old, userName);
            } else {
                // 如果这个流程定义没有部署过，或本次编辑没有修改流程定义的 XML，则不部署新版本，只保存更新的数据
                if (!update(lambdaUpdate()
                    .eq(ProcessDefinitionMetadata::getId, currentId)
                    .set(ProcessDefinitionMetadata::getName, processDefinitionMetadata.getName())
                    .set(ProcessDefinitionMetadata::getDescription, processDefinitionMetadata.getDescription())
                    .set(ProcessDefinitionMetadata::getUpdatedBy, userName)
                    .set(ProcessDefinitionMetadata::getUpdatedAt, LocalDateTime.now()))) {

                    throw new BusinessException("更新流程定义失败！");
                }
            }
        }
    }

    public void updateProcessDefinitionMetadataById(ProcessDefinitionMetadata processDefinitionMetadata,
                                                    String userName) {
        processDefinitionMetadata.setUpdatedBy(userName);
        processDefinitionMetadata.setUpdatedAt(LocalDateTime.now());
        if (!updateById(processDefinitionMetadata)) {
            throw new BusinessException("更新流程定义元数据失败！");
        }
    }

    public IPage<ProcessDefinitionMetadata> searchPageBySystemId(long systemId, long current, int size, String key) {
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
            .ne(ProcessDefinitionMetadata::getStatus, StatusEnum.DELETED)
            .orderByDesc(ProcessDefinitionMetadata::getUpdatedAt));
    }

    public ProcessDefinitionMetadata processDefinition(Long processDefinitionId) {
        if (processDefinitionId == null || processDefinitionId <= 0) {
            return null;
        }
        return lambdaQuery().eq(ProcessDefinitionMetadata::getId, processDefinitionId)
            .ne(ProcessDefinitionMetadata::getStatus, StatusEnum.DELETED).one();
    }

    public ProcessDefinitionMetadata processDefinition(long systemId, String processDefinitionKey) {
        return lambdaQuery(systemId, processDefinitionKey).one();
    }

    public boolean exists(long systemId, String processDefinitionKey) {
        return lambdaQuery(systemId, processDefinitionKey).exists();
    }

    private LambdaQueryChainWrapper<ProcessDefinitionMetadata> getSystemLambdaQuery(long systemId) {
        return lambdaQuery().eq(ProcessDefinitionMetadata::getSystemId, systemId)
            .ne(ProcessDefinitionMetadata::getStatus, StatusEnum.DELETED);
    }

    private LambdaQueryChainWrapper<ProcessDefinitionMetadata> lambdaQuery(long systemId, String processDefinitionKey) {
        return getSystemLambdaQuery(systemId)
            .eq(ProcessDefinitionMetadata::getProcessDefinitionKey, processDefinitionKey);
    }
}
