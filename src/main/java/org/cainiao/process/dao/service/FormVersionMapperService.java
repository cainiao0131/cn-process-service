package org.cainiao.process.dao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dao.mapper.FormVersionMapper;
import org.cainiao.process.entity.Form;
import org.cainiao.process.entity.FormVersion;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
public class FormVersionMapperService extends ServiceImpl<FormVersionMapper, FormVersion>
    implements IService<FormVersion> {

    public FormVersion fetchByProcessFormKey(@NonNull String processFormKey) {
        String[] processFormKeyParts = processFormKey.split(":");
        return lambdaQuery()
            .eq(FormVersion::getFormKey, processFormKeyParts[0])
            .eq(FormVersion::getVersion, Long.valueOf(processFormKeyParts[1]))
            .one();
    }

    public void addFormVersion(Form form, FormVersion formVersion, String userName) {
        formVersion.setId(null);
        String formKey = form.getKey();
        formVersion.setFormKey(formKey);
        LocalDateTime now = LocalDateTime.now();
        formVersion.setCreatedBy(userName);
        formVersion.setUpdatedBy(userName);
        formVersion.setCreatedAt(now);
        formVersion.setUpdatedAt(now);
        FormVersion old = fetchNewestVersion(formKey);
        formVersion.setVersion(old == null ? 1 : old.getVersion() + 1);
        if (!save(formVersion)) {
            throw new BusinessException("添加流程表单版本失败！");
        }
    }

    public FormVersion fetchNewestVersion(String formKey) {
        return lambdaQuery().eq(FormVersion::getFormKey, formKey).orderByDesc(FormVersion::getVersion).one();
    }

    public List<FormVersion> formVersions(String formKey) {
        return lambdaQuery().eq(FormVersion::getFormKey, formKey).orderByDesc(FormVersion::getVersion).list();
    }
}
