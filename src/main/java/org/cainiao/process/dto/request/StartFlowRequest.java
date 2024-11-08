package org.cainiao.process.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartFlowRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2226219246730320323L;

    @Schema(description = "流程定义 Key", requiredMode = RequiredMode.NOT_REQUIRED)
    private String processDefinitionKey;

    @Schema(description = "流程定义 ID", requiredMode = RequiredMode.NOT_REQUIRED)
    private String processDefinitionId;

    @Schema(description = "流程变量", requiredMode = RequiredMode.NOT_REQUIRED)
    private Map<String, Object> variables;
}
