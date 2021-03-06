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
package com.choodon.tool.excel.enums;

/**
 * DataFormat
 *
 * @author michael
 * @since 2019-01-08
 */
public enum DataFormat {
    /**
     * when use this data format, data format will call String toString() method.
     */
    PLAIN,
    /**
     * when type is java.util.Date,  use this data format.
     */
    DATE_TIME,
    /**
     * when type is  java.lang.Number, use this data format.
     */
    NUMBER,
    /**
     * when type is  enum class , use this data format.
     */
    ENUM,
    /**
     * when custom format , use this data format.
     */
    CUSTOM,

}
