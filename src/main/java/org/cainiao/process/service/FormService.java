package org.cainiao.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cainiao.process.dto.FormWithVersion;
import org.cainiao.process.dto.response.FormResponse;
import org.cainiao.process.entity.FormVersion;

import java.util.List;

public interface FormService {

    void addOrEditForm(long systemId, FormWithVersion formWithVersion, String userName);

    IPage<FormResponse> forms(long systemId, int current, int size, String key);

    List<FormVersion> versions(String formKey);

    FormWithVersion form(String formKey);
}
