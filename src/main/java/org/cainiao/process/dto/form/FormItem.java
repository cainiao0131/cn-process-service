package org.cainiao.process.dto.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormItem implements Serializable {

    @Serial
    private static final long serialVersionUID = -1771845499893301013L;

    /**
     * 组件类型
     */
    private String type;

    /**
     * 组件分组
     */
    private String group;

    /**
     * 组件配置
     */
    private FormItemConfig config;

    /**
     * 组件属性
     */
    private Map<String, Object> properties;

    /**
     * 组件配置信息
     */
    private List<FormItem> propertyConfigFormComponents;

    /**
     * 隐藏自动的计算逻辑
     */
    private String hiddenFieldsFun;

    /**
     * 渲染时的值预处理器
     */
    private String propertiesTransfer;

    /**
     * 表单验证规则
     */
    private List<Rule> rules;

    /**
     * 是否隐藏，隐藏后提交表单时仍然存在
     */
    private boolean hidden;

    /**
     * 选项
     */
    private List<Map<String, Object>> options;

    /**
     * 是否忽略，隐藏后提交表单时不存在
     */
    private boolean ignore;

    /**
     * 是否忽略，隐藏后提交表单时不存在
     */
    private String formItemClass;

    private List<FormItem> children;
}
