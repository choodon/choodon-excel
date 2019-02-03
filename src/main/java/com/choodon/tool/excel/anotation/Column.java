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
package com.choodon.tool.excel.anotation;

import com.choodon.tool.excel.enums.DataFormat;
import com.choodon.tool.excel.enums.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

/**
 * Column
 *
 * @author michael
 * @since 2019-01-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    int index() default -1;

    String name();

    DataFormat format() default DataFormat.PLAIN;

    /**
     * com.choodon.common.tool.excel.enums.DataFormat#ENUM 需指定枚举类
     * 枚举类有String desc(java.lang.Number);方法
     *
     * @return
     */
    Class<?>[] enumClass() default {};

    /**
     * com.choodon.common.tool.excel.enums.DataFormat#NUMBER 数字格式化需要指定精度，默认精度是0
     * 数字格式化指定精度
     */
    int scale() default 0;

    /**
     * com.choodon.common.tool.excel.enums.DataFormat#NUMBER 数字操作指定roundingMode
     *
     * @return
     */
    int roundingMode() default BigDecimal.ROUND_DOWN;

    /**
     * com.choodon.common.tool.excel.enums.DataFormat#NUMBER 加减乘除操作
     *
     * @return
     */
    Operation operation() default Operation.NONE;

    /**
     * com.choodon.common.tool.excel.enums.DataFormat#NUMBER 操作数-加数、减数、乘数、除数
     *
     * @return
     */
    double[] operationNumber() default {};

    /**
     * com.choodon.common.tool.excel.enums.DataFormat#NUMBER 数字格式化
     *
     * @return
     */
    String numberFormat() default "";

    /**
     * com.choodon.common.tool.excel.enums.DataFormat#DATE_TIME 时间格式化
     *
     * @return
     */
    String dateTimeFormat() default "yyyy-MM-dd HH:mm:ss";

}
