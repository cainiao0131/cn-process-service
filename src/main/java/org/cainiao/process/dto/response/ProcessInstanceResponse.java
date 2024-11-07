package org.cainiao.process.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.history.HistoricProcessInstance;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 2161300273426808349L;

    @Schema(description = "流程实例ID", requiredMode = RequiredMode.NOT_REQUIRED)
    private String id;

    @Schema(description = "名称", requiredMode = RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "描述", requiredMode = RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "业务状态", requiredMode = RequiredMode.NOT_REQUIRED)
    private String businessStatus;

    @Schema(description = "是否暂停", requiredMode = RequiredMode.NOT_REQUIRED)
    private boolean suspended;

    @Schema(description = "是否结束", requiredMode = RequiredMode.NOT_REQUIRED)
    private boolean ended;

    @Schema(description = "开始时间", requiredMode = RequiredMode.NOT_REQUIRED)
    private String startTime;

    @Schema(description = "结束时间", requiredMode = RequiredMode.NOT_REQUIRED)
    private String endTime;

    @Schema(description = "流程发起人", requiredMode = RequiredMode.NOT_REQUIRED)
    private String startUserId;

    @Schema(description = "活动节点ID", requiredMode = RequiredMode.NOT_REQUIRED)
    private List<String> activityIds;

    @Schema(description = "结束节点ID", requiredMode = RequiredMode.NOT_REQUIRED)
    private String endActivityId;

    @Schema(description = "删除原因", requiredMode = RequiredMode.NOT_REQUIRED)
    private String deleteReason;

    @Schema(description = "持续时间", requiredMode = RequiredMode.NOT_REQUIRED)
    private Long durationInMillis;

    public static ProcessInstanceResponse from(HistoricProcessInstance historicProcessInstance,
                                               SimpleDateFormat simpleDateFormat) {
        Date endTime = historicProcessInstance.getEndTime();
        return ProcessInstanceResponse.builder()
            .id(historicProcessInstance.getId())
            .startUserId(historicProcessInstance.getStartUserId())
            .name(historicProcessInstance.getName())
            .description(historicProcessInstance.getDescription())
            .businessStatus(historicProcessInstance.getBusinessStatus())
            .ended(endTime != null)
            .startTime(simpleDateFormat.format(historicProcessInstance.getStartTime()))
            .endTime(endTime == null ? null : simpleDateFormat.format(endTime))
            .durationInMillis(historicProcessInstance.getDurationInMillis())
            .endActivityId(historicProcessInstance.getEndActivityId())
            .deleteReason(historicProcessInstance.getDeleteReason())
            .build();
    }
}
