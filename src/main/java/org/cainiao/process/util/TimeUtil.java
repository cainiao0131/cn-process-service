package org.cainiao.process.util;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@UtilityClass
public class TimeUtil {

    // TODO SimpleDateFormat 是可变对象，是伪常量，需要包装一下
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
