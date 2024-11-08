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

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_form")
@Schema(name = "Form", description = "流程表单")
public class Form extends IdBaseEntity {

    @Serial
    private static final long serialVersionUID = -5576186859284046249L;

    @TableField(value = "f_system_id", insertStrategy = FieldStrategy.NOT_NULL)
    @Schema(description = "所属系统 ID")
    private Long systemId;

    @TableField(value = "f_form_id", insertStrategy = FieldStrategy.NOT_NULL)
    @Schema(description = "流程表单 Key，用于做业务的主键")
    private String key;

    @TableField(value = "f_name", insertStrategy = FieldStrategy.NOT_EMPTY)
    @Schema(description = "表单名称")
    private String name;

    @TableField(value = "f_description")
    @Schema(description = "表单描述")
    private String description;
}
