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
package com.choodon.common.tool.excel.enums;

/**
 * DataFormat
 *
 * @author michael
 * @since 2019-01-08
 */
public enum DataFormat {
    /**
     * java.lang.Object#toString()
     */
    PLAIN,
    /**
     * @see java.util.Date
     */
    DATE_TIME,
    /**
     * 数字格式化
     */
    NUMBER,
    /**
     * 枚举，必须指定一个含有String desc(java.lang.Number);方法的枚举类
     */
    ENUM,

}
