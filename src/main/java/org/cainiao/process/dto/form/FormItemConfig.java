package org.cainiao.process.dto.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormItemConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -8257233926966277713L;

    /**
     * 字段名称
     */
    private String name;

    private String label;
}
