package org.cainiao.process.dao.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dao.mapper.FormMapper;
import org.cainiao.process.dto.FormWithVersion;
import org.cainiao.process.dto.response.FormResponse;
import org.cainiao.process.entity.Form;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
public class FormMapperService extends ServiceImpl<FormMapper, Form> implements IService<Form> {

    public IPage<FormResponse> forms(long systemId, int current, int size, String key) {
        QueryWrapper<Form> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Form::getSystemId, systemId).and(StringUtils.hasText(key), wrapper -> wrapper
            .like(Form::getKey, key).or().like(Form::getName, key).or().like(Form::getDescription, key));
        return searchPage(current, size, () -> count(queryWrapper),
            (offset) -> getBaseMapper().formInfos(offset, size, queryWrapper));
    }

    private IPage<FormResponse> searchPage(int current, int size, Supplier<Long> countFactory,
                                           Function<Integer, List<FormResponse>> recordFactory) {
        IPage<FormResponse> page = new Page<>(current, size);
        page.setTotal(countFactory.get());
        page.setRecords(recordFactory.apply((current - 1) * size));
        return page;
    }

    public Form fetchByKey(String formKey) {
        if (!StringUtils.hasText(formKey)) {
            return null;
        }
        return lambdaQuery().eq(Form::getKey, formKey).one();
    }

    public Form fetchById(Long formId) {
        if (formId == null || formId <= 0) {
            return null;
        }
        return getById(formId);
    }

    public void updateFormById(Form form, String userName) {
        form.setUpdatedBy(userName);
        form.setUpdatedAt(LocalDateTime.now());
        if (!updateById(form)) {
            throw new BusinessException("更新流程表单失败！");
        }
    }

    public Form addOrEditForm(long systemId, FormWithVersion formWithVersion, String userName) {
        Form form = formWithVersion.getForm();
        Long formId = form.getId();
        Form old = fetchById(formId);
        LambdaQueryChainWrapper<Form> sameNameCnd = lambdaQuery()
            .eq(Form::getSystemId, systemId).eq(Form::getName, form.getName());
        LocalDateTime now = LocalDateTime.now();
        if (old == null) {
            // 添加
            if (sameNameCnd.exists()) {
                throw new BusinessException("表单名称重复！");
            }
            if (lambdaQuery().eq(Form::getSystemId, systemId).eq(Form::getKey, form.getKey()).exists()) {
                throw new BusinessException("表单 Key 重复！");
            }
            form.setId(null);
            form.setCreatedBy(userName);
            form.setUpdatedBy(userName);
            form.setCreatedAt(now);
            form.setUpdatedAt(now);
            if (!save(form)) {
                throw new BusinessException("添加流程表单失败！");
            }
            return form;
        }
        // 编辑
        if (!old.getKey().equals(form.getKey())) {
            throw new BusinessException("不允许修改流程表单 Key!");
        }
        if (sameNameCnd.ne(Form::getId, formId).exists()) {
            throw new BusinessException("表单名称重复！");
        }
        old.setName(form.getName());
        old.setDescription(form.getDescription());
        updateFormById(old, userName);
        return old;
    }
}
