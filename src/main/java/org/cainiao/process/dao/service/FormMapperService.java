package org.cainiao.process.dao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cainiao.process.dao.mapper.FormMapper;
import org.cainiao.process.entity.Form;
import org.springframework.stereotype.Service;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
public class FormMapperService extends ServiceImpl<FormMapper, Form> implements IService<Form> {

    public Form fetchByKey(String key) {
        return lambdaQuery().eq(Form::getKey, key).one();
    }
}
