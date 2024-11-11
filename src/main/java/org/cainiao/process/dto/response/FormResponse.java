package org.cainiao.process.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.cainiao.process.entity.Form;

import java.io.Serial;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FormResponse extends Form {

    @Serial
    private static final long serialVersionUID = -8663952532028786581L;

    @TableField(value = "latest_version")
    @Schema(description = "表单最新版本")
    private Long latestVersion;
}
