package org.cainiao.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.dao.service.FormMapperService;
import org.cainiao.process.dao.service.FormVersionMapperService;
import org.cainiao.process.dto.FormWithVersion;
import org.cainiao.process.dto.response.FormResponse;
import org.cainiao.process.entity.Form;
import org.cainiao.process.entity.FormVersion;
import org.cainiao.process.service.FormService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {

    private final FormMapperService formMapperService;
    private final FormVersionMapperService formVersionMapperService;

    @Transactional
    @Override
    public void addOrEditForm(long systemId, FormWithVersion formWithVersion, String userName) {
        Form form = formMapperService.addOrEditForm(systemId, formWithVersion, userName);
        formVersionMapperService.addFormVersion(form, formWithVersion.getFormVersion(), userName);
    }

    @Override
    public IPage<FormResponse> forms(long systemId, int current, int size, String key) {
        return formMapperService.forms(systemId, current, size, key);
    }

    @Override
    public List<FormVersion> versions(String formKey) {
        return formVersionMapperService.versions(formKey);
    }

    @Override
    public FormWithVersion form(String formKey) {
        Form form = formMapperService.fetchByKey(formKey);
        if (form == null) {
            return null;
        }
        return FormWithVersion.builder()
            .form(form)
            .formVersion(formVersionMapperService.fetchNewestVersion(formKey))
            .build();
    }

    @Override
    public void deleteForm(String formKey) {
        // TODO 检查是否存在引用了这个表单的流程实例，如果存在则删除失败抛异常
        // TODO 在发起和部署流程时，检查流程定义中是否存在被删除的表单版本，如果存在则发起或部署失败，提示用户修改
        // TODO 删除表单时同时删除所有表单版本
    }
}
