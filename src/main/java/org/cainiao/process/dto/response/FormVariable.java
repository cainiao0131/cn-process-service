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
public class FormVariable implements Serializable {

    @Serial
    private static final long serialVersionUID = -7961239763017153701L;
    
    @Schema(description = "类型", requiredMode = RequiredMode.REQUIRED)
    private String type;

    @Schema(description = "名称", requiredMode = RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "值", requiredMode = RequiredMode.REQUIRED)
    private String value;
}
