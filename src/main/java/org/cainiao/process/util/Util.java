package org.cainiao.process.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.UUID;

@UtilityClass
public class Util {

    public static String fixString(String origin) {
        return StringUtils.hasText(origin) ? origin.trim() : "";
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
