package org.cainiao.process.service;

import org.cainiao.process.dto.FormWithVersion;

public interface FormService {

    void addOrEditForm(long systemId, FormWithVersion formWithVersion, String userName);
}
