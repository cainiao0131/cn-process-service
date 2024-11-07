package org.cainiao.process.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.cainiao.common.constant.Codebook;
import org.cainiao.common.constant.ICodeBook;
import org.cainiao.common.dao.IdBaseEntity;

import java.io.Serial;
import java.util.Optional;

/**
 * 表示正在编辑的流程，可能还没有部署到流程引擎<br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_process_definition_metadata")
@Schema(name = "ProcessDefinitionMetadata", description = "流程定义元数据")
public class ProcessDefinitionMetadata extends IdBaseEntity {

    @Serial
    private static final long serialVersionUID = 573186224996665678L;

    @TableField(value = "pdm_system_id", insertStrategy = FieldStrategy.NOT_NULL)
    @Schema(description = "所属系统 ID")
    private Long systemId;

    @TableField(value = "pdm_name", insertStrategy = FieldStrategy.NOT_EMPTY)
    @Schema(description = "名称")
    private String name;

    @TableField(value = "pdm_process_definition_key", insertStrategy = FieldStrategy.NOT_EMPTY)
    @Schema(description = "流程定义 Key，用于做业务")
    private String processDefinitionKey;

    @TableField(value = "pdm_xml")
    @Schema(description = "流程定义最新编辑中的 BPMN 规范的 XML，要么对应流程引擎最新版本，要么还没发布到流程引擎")
    private String xml;

    @TableField(value = "pdm_version", insertStrategy = FieldStrategy.NOT_NULL)
    @Schema(description = "流程定义在流程引擎中的最新版本")
    private Integer version;

    @TableField(value = "pdm_status", insertStrategy = FieldStrategy.NOT_EMPTY)
    @Schema(description = "流程状态")
    @Builder.Default
    private StatusEnum status = StatusEnum.NEW;

    @TableField(value = "pdm_description")
    @Schema(description = "流程描述")
    private String description;

    @JsonGetter
    public Codebook getStatusInfo() {
        return Optional.ofNullable(getStatus()).orElse(StatusEnum.NEW).build();
    }

    @JsonSetter
    public void setStatusInfo(Codebook statusInfo) {
        // do nothing
    }

    @Getter
    @AllArgsConstructor
    public enum StatusEnum implements ICodeBook {
        NEW("new", "未部署"), DEPLOYED("deployed", "已部署");

        final String code;
        final String description;
    }
}
