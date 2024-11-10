package org.cainiao.process.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.task.api.Task;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTaskResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -1349369843664126143L;

    @Schema(description = "流程任务 ID", requiredMode = RequiredMode.NOT_REQUIRED)
    private String taskId;

    @Schema(description = "名称", requiredMode = RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "描述", requiredMode = RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "创建时间", requiredMode = RequiredMode.NOT_REQUIRED)
    private String createTime;

    @Schema(description = "任务状态", requiredMode = RequiredMode.NOT_REQUIRED)
    private String state;

    @Schema(description = "所有者", requiredMode = RequiredMode.NOT_REQUIRED)
    private String owner;

    @Schema(description = "委托人", requiredMode = RequiredMode.NOT_REQUIRED)
    private String assignee;

    @Schema(description = "表单Key", requiredMode = RequiredMode.NOT_REQUIRED)
    private String formKey;

    @Schema(description = "表单配置", requiredMode = RequiredMode.NOT_REQUIRED)
    private String formConfig;

    @Schema(description = "表单项", requiredMode = RequiredMode.NOT_REQUIRED)
    private String formItems;

    @Schema(description = "当前任务在 xml 中对应元素的 ID", requiredMode = RequiredMode.REQUIRED)
    private String elementId;

    @Schema(description = "流程详情", requiredMode = RequiredMode.REQUIRED)
    private ProcessInstanceDetail processInstanceDetail;

    public static ProcessTaskResponse from(Task task, SimpleDateFormat simpleDateFormat) {
        return ProcessTaskResponse.builder()
            .taskId(task.getId())
            .name(task.getName())
            .description(task.getDescription())
            .owner(task.getOwner())
            .assignee(task.getAssignee())
            .state(task.getState())
            .formKey(task.getFormKey())
            .createTime(simpleDateFormat.format(task.getCreateTime()))
            .build();
    }
}
