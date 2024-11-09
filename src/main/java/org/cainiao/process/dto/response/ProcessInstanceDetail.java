package org.cainiao.process.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = -5388777095448333942L;
    
    @Schema(description = "流程实例 ID", requiredMode = RequiredMode.REQUIRED)
    private String processInstanceId;

    @Schema(description = "bpmn xml", requiredMode = RequiredMode.REQUIRED)
    private String xml;

    @Schema(description = "已执行的节点 ID", requiredMode = RequiredMode.REQUIRED)
    private List<String> finishedActivityIds;

    @Schema(description = "正在执行的节点 ID", requiredMode = RequiredMode.REQUIRED)
    private List<String> activeActivityIds;
}
