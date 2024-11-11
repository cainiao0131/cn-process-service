package org.cainiao.process.service.impl;

import lombok.RequiredArgsConstructor;
import org.cainiao.process.dao.service.FormMapperService;
import org.cainiao.process.dao.service.FormVersionMapperService;
import org.cainiao.process.dto.FormWithVersion;
import org.cainiao.process.entity.Form;
import org.cainiao.process.service.FormService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
