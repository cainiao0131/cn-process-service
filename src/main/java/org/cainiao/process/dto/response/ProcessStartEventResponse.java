package org.cainiao.process.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发起流程请求的返回数据结构<br />
 * 用于指明发起流程是否需要填写表单，如果需要则返回需要的表单定义，如果不需要则表示已成功发起一个流程
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStartEventResponse {

    /**
     * 如果不需要填表单，流程已经成功发起，则返回发起的流程实例 ID
     */
    @Schema(description = "流程实例 ID", requiredMode = RequiredMode.NOT_REQUIRED)
    private String processInstanceId;

    /**
     * 流程定义 ID<br />
     * 因为启动事件表单是特定于流程定义的某个版本的<br />
     * 因此当用户填写完启动事件表单后，需要依赖这个 ID 来发起这个表单对应的流程定义版本的流程
     */
    @Schema(description = "流程定义 ID", requiredMode = RequiredMode.NOT_REQUIRED)
    private String processDefinitionId;

    @Schema(description = "发起流程是否需要填表单", requiredMode = RequiredMode.NOT_REQUIRED)
    private boolean needForm;

    @Schema(description = "表单名称", requiredMode = RequiredMode.NOT_REQUIRED)
    private String formName;

    @Schema(description = "表单配置", requiredMode = RequiredMode.NOT_REQUIRED)
    private String formConfig;

    @Schema(description = "表单项", requiredMode = RequiredMode.NOT_REQUIRED)
    private String formItems;
}
