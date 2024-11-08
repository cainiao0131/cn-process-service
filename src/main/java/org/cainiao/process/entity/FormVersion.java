package org.cainiao.process.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.cainiao.common.dao.IdBaseEntity;

import java.io.Serial;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_form_version")
@Schema(name = "FormVersion", description = "流程表单版本")
public class FormVersion extends IdBaseEntity {

    @Serial
    private static final long serialVersionUID = -5563961137771745802L;

    @TableField(value = "fv_form_id", insertStrategy = FieldStrategy.NOT_NULL)
    @Schema(description = "流程表单 Key，用于做业务的主键")
    private String formKey;

    @TableField(value = "fv_version", insertStrategy = FieldStrategy.NOT_NULL)
    @Schema(description = "表单版本号")
    private Long version;

    @TableField(value = "fv_form_config")
    @Schema(description = "流程表单配置")
    private String formConfig;

    @TableField(value = "fv_form_items", insertStrategy = FieldStrategy.NOT_EMPTY)
    @Schema(description = "表单项")
    private String formItems;
}
