package org.cainiao.process.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class Util {

    public static String fixString(String origin) {
        return StringUtils.hasText(origin) ? origin.trim() : "";
    }
}
