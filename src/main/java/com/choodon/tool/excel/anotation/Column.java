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
     * When format is com.choodon.common.tool.excel.enums.DataFormat#ENUM , Specify a enumClass which has a [desc] method like
     * String desc(java.lang.Number)
     */
    Class<?>[] enumClass() default {};

    /**
     * When format is com.choodon.common.tool.excel.enums.DataFormat#NUMBER, Specify a scale .
     */
    int scale() default -1;

    /**
     * When format is com.choodon.common.tool.excel.enums.DataFormat#NUMBER, Specify a rounding behavior for numerical operations
     * capable of discarding precision.
     */
    int roundingMode() default BigDecimal.ROUND_DOWN;

    /**
     * When format is com.choodon.common.tool.excel.enums.DataFormat#NUMBER, Specify a operation.
     */
    Operation operation() default Operation.NONE;

    /**
     * When format is com.choodon.common.tool.excel.enums.DataFormat#NUMBER, Specify a operation number.
     */
    double[] operationNumber() default {};

    /**
     * When format is com.choodon.common.tool.excel.enums.DataFormat#NUMBER, Specify a operation number format.
     */
    String numberFormat() default "";

    /**
     * When format is com.choodon.common.tool.excel.enums.DataFormat#DATE_TIME, Specify a operation date format.
     */
    String dateTimeFormat() default "yyyy-MM-dd HH:mm:ss";

    /**
     * When format is com.choodon.common.tool.excel.enums.DataFormat#CUSTOM , Specify a class which implement com.choodon.tool.excel.Convert
     */
    Class<?>[] convertClass() default {};

}
