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
package com.choodon.tool.excel.util;

import java.math.BigDecimal;

/**
 * NumberUtils
 *
 * @author michael
 * @since 2019-02-14
 */
public class NumberUtils {
    private NumberUtils() {
    }

    /**
     * compare two number
     * any parameter is null return false
     * <p>
     * ignore precision, 2.00 2.0 return true
     *
     * @param n1
     * @param n2
     * @return
     */
    public static boolean equals(Number n1, Number n2) {
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

}
