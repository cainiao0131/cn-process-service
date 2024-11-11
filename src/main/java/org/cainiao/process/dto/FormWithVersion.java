package org.cainiao.process.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cainiao.process.entity.Form;
import org.cainiao.process.entity.FormVersion;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FormWithVersion {

    @Schema(description = "表单", requiredMode = RequiredMode.REQUIRED)
    private Form form;

    @Schema(description = "表单版本信息", requiredMode = RequiredMode.NOT_REQUIRED)
    private FormVersion formVersion;
}
