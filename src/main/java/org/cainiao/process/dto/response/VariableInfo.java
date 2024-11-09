package org.cainiao.process.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariableInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 5617293288704217842L;
    
    @Schema(description = "说明", requiredMode = RequiredMode.REQUIRED)
    private String label;

    @Schema(description = "值", requiredMode = RequiredMode.REQUIRED)
    private Object value;
}
