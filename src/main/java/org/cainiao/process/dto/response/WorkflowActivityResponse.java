package org.cainiao.process.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowActivityResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -7852751594575222833L;
    
    @Schema(description = "活动实例 ID", requiredMode = RequiredMode.REQUIRED)
    private String activityInstanceId;

    @Schema(description = "活动 ID", requiredMode = RequiredMode.REQUIRED)
    private String activityId;

    @Schema(description = "活动名称", requiredMode = RequiredMode.REQUIRED)
    private String activityName;

    @Schema(description = "活动类型", requiredMode = RequiredMode.REQUIRED)
    private String activityType;

    @Schema(description = "活动创建时间", requiredMode = RequiredMode.NOT_REQUIRED)
    private Date createTime;

    @Schema(description = "活动结束时间", requiredMode = RequiredMode.NOT_REQUIRED)
    private Date endTime;

    @Schema(description = "结束原因", requiredMode = RequiredMode.NOT_REQUIRED)
    private String endReason;

    @Schema(description = "活动状态", requiredMode = RequiredMode.REQUIRED)
    private String state;

    @Schema(description = "委托人", requiredMode = RequiredMode.NOT_REQUIRED)
    private String assignee;

    @Schema(description = "下一步，例如互斥网关的output", requiredMode = RequiredMode.NOT_REQUIRED)
    private String nextActivity;

    @Schema(description = "活动变量", requiredMode = RequiredMode.NOT_REQUIRED)
    private List<VariableInfo> variables;
}
