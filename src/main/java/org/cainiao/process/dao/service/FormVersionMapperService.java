package org.cainiao.process.dao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cainiao.process.dao.mapper.FormVersionMapper;
import org.cainiao.process.entity.FormVersion;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
public class FormVersionMapperService extends ServiceImpl<FormVersionMapper, FormVersion>
    implements IService<FormVersion> {

    public FormVersion fetchByFormKey(@NonNull String processFormKey) {
        String[] startFormInfo = processFormKey.split(":");
        return lambdaQuery()
            .eq(FormVersion::getFormKey, startFormInfo[0])
            .eq(FormVersion::getVersion, Long.valueOf(startFormInfo[1]))
            .one();
    }
}