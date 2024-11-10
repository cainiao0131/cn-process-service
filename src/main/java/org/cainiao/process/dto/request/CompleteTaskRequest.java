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
public class CompleteTaskRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 719930154876053264L;
    
    @Schema(description = "任务变量", requiredMode = RequiredMode.REQUIRED)
    private Map<String, Object> localVariables;

    @Schema(description = "流程变量", requiredMode = RequiredMode.REQUIRED)
    private Map<String, Object> processVariables;
}
