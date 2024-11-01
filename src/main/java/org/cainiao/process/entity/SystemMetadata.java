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
 * 与流程相关的系统元数据<br />
 * 即对技术中台系统表的纵向拆分，将与流程相关的字段放到这个表中<br />
 * sm_system_id 为主键
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_system_metadata")
@Schema(name = "SystemMetadata", description = "系统流程元数据")
public class SystemMetadata extends IdBaseEntity {

    @Serial
    private static final long serialVersionUID = 4146146795245676376L;

    @TableField(value = "sm_system_id", insertStrategy = FieldStrategy.NOT_NULL)
    @Schema(description = "系统 ID")
    private Long systemId;

    @TableField(value = "sm_webhook", insertStrategy = FieldStrategy.NOT_EMPTY)
    @Schema(description = "网络钩子，流程统一通过这个地址对系统进行调用")
    private String webhook;
}
