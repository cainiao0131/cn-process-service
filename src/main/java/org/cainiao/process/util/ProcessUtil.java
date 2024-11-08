package org.cainiao.process.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.experimental.UtilityClass;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dto.form.FormItem;
import org.cainiao.process.dto.form.Rule;
import org.cainiao.process.entity.FormVersion;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@UtilityClass
public class ProcessUtil {

    public static void validateForm(FormVersion formVersion, Map<String, Object> variables) {
        if (formVersion == null) {
            return;
        }
        List<FormItem> formItems;
        try {
            formItems = JsonUtil.jsonToList(formVersion.getFormItems(), FormItem.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException("JSON 转换错误", e);
        }
        for (FormItem formItem : formItems) {
            String fieldName = formItem.getConfig().getName();
            validateField(fieldName, variables.get(fieldName), formItem.getRules());
        }
    }

    /**
     * 校验字段<br />
     * TODO 待完善，目前只校验了必填
     *
     * @param fieldName 字段名称
     * @param value     字段的值
     * @param rules     校验规则
     */
    public static void validateField(String fieldName, Object value, List<Rule> rules) {
        for (Rule rule : rules) {
            if (rule.isRequired() && (value == null || !StringUtils.hasText(value.toString()))) {
                String message = rule.getMessage();
                throw new BusinessException(StringUtils.hasText(message) ? message : fieldName + " 不能为空");
            }
        }
    }
}
