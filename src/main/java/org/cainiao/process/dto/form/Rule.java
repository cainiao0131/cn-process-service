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
public class Rule implements Serializable {

    @Serial
    private static final long serialVersionUID = -1227357784222216833L;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 字段类型：'string' | 'number' | 'boolean' | 'method' | 'regexp' | 'integer' | 'float' | 'object' | 'enum' | 'date' | 'url' | 'hex' | 'email'
     */
    private String type;
}
