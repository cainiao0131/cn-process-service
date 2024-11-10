package org.cainiao.process.dto.request;

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
public class ReassignTaskRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 4984170979069699670L;

    @Schema(description = "目标用户名", requiredMode = RequiredMode.NOT_REQUIRED)
    private String toUserName;
}
