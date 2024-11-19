package org.cainiao.process.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonGetter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.cainiao.common.constant.Codebook;
import org.cainiao.common.constant.ICodeBook;
import org.cainiao.common.dao.IdBaseEntity;

import java.io.Serial;
import java.util.Map;

/**
 * 流程事件日志汇总表，方便基于时间查看事件发生的过程<br />
 * TODO 将请求重试数据解耦出去，一条事件记录只代表一次流程事件
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_process_event_log")
@Schema(name = "ProcessEventLog", description = "流程事件日志汇总表")
public class ProcessEventLog extends IdBaseEntity {

    @Serial
    private static final long serialVersionUID = -1643171496764425642L;

    @TableField(value = "pel_process_definition_key", insertStrategy = FieldStrategy.NOT_EMPTY)
    @Schema(description = "流程定义 Key")
    private String processDefinitionKey;

    @TableField(value = "pel_process_instance_id", insertStrategy = FieldStrategy.NOT_EMPTY)
    @Schema(description = "流程实例 ID")
    private String processInstanceId;

    @TableField(value = "pel_element_type", insertStrategy = FieldStrategy.NOT_EMPTY)
    @Schema(description = "元素类型")
    private ElementTypeEnum elementType;

    @TableField(value = "pel_element_instance_id", insertStrategy = FieldStrategy.NOT_EMPTY)
    @Schema(description = "元素实例 ID，例如用户任务 ID")
    private String elementInstanceId;

    @TableField(value = "pel_event_type")
    @Schema(description = "事件类型", requiredMode = RequiredMode.NOT_REQUIRED)
    private EventType eventType;

    @TableField(value = "pel_trace_id")
    @Schema(description = "追踪 ID，例如追踪一个请求的多次重试")
    private String traceId;

    @TableField(value = "pel_request_body")
    @Schema(description = "请求体")
    private Map<String, Object> requestBody;

    @TableField(value = "pel_http_status_code")
    @Schema(description = "HTTP 响应码")
    private Integer httpStatusCode;

    @TableField(value = "pel_context")
    @Schema(description = "上下文")
    private Map<String, Object> context;

    @TableField(value = "pel_exception_message")
    @Schema(description = "异常消息")
    private String exceptionMessage;

    @JsonGetter
    public Codebook getElementTypeInfo() {
        return elementType == null ? null : elementType.build();
    }

    public void setElementTypeInfo(Codebook elementTypeInfo) {
        this.elementType = ElementTypeEnum.valueOf(elementTypeInfo.getName());
    }

    @JsonGetter
    public Codebook getEventTypeInfo() {
        return eventType == null ? null : eventType.build();
    }

    public void setEventTypeInfo(Codebook eventTypeInfo) {
        this.eventType = EventType.valueOf(eventTypeInfo.getName());
    }

    @Getter
    @AllArgsConstructor
    public enum ElementTypeEnum implements ICodeBook {
        USER_TASK("userTask", "用户任务"), START_EVENT("startEvent", "开始事件");

        final String code;
        final String description;
    }

    @Getter
    @AllArgsConstructor
    public enum EventType implements ICodeBook {
        CREATE("create", "创建"), COMPLETE("complete", "完成");

        final String code;
        final String description;
    }
}
