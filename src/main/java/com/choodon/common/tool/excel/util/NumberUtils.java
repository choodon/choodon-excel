/**
 * Copyright [2019] [choodon-excel of copyright https://github.com/choodon account owner]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 */
package com.choodon.common.tool.excel.util;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * NumberUtils
 *
 * @author michael
 * @since 2018/6/14
 */
public class NumberUtils {
    private NumberUtils() {
    }

    /**
     * 数字相等比较
     * 如果任意一个为null 返回false
     * 适用于 Number 所有的子类 Byte Short Integer Long Double AtomicInteger 等
     * 忽略精度 2.00 2.0 认为相等
     *
     * @param n1
     * @param n2
     * @return
     */
    public static final boolean equals(Number n1, Number n2) {
        if (n1 == null || n2 == null) {
            return false;
        } else {
            BigDecimal num1 = new BigDecimal(n1.toString());
            BigDecimal num2 = new BigDecimal(n2.toString());
            return num1.compareTo(num2) == 0;
        }
    }

    public static final boolean notEquals(Number n1, Number n2) {
        return !equals(n1, n2);
    }

    public static Long negativeToZero(Long value) {
        if (Objects.isNull(value)) {
            return null;
        }
        return value < 0 ? 0 : value;
    }

    public static final Long nullToZero(Long value) {
        return value == null ? 0 : value;
    }
}
