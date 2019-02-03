
##choodon-excel
* a simple excel generator


##Developer

* Email: godcin@foxmail.com
* Wechat: godcin




##Demo

```javascript
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
package com.choodon.common.tool.excel;


import com.choodon.common.tool.excel.anotation.Column;
import com.choodon.common.tool.excel.anotation.Header;
import com.choodon.common.tool.excel.enums.DataFormat;
import com.choodon.common.tool.excel.enums.Operation;
import com.choodon.common.tool.excel.util.NumberUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * com.choodon.common.tool.excel.Demo
 *
 * @author michael
 * @since 2018-12-18
 */
@Header("test")
public class Demo {
    @Column(name = "plain")
    private String plain = "hello";
    @Column(name = "date", format = DataFormat.DATE_TIME, dateTimeFormat = "yyyy-MM-dd")
    private Date date = new Date();
    @Column(name = "date_time", format = DataFormat.DATE_TIME)
    private Date date_time = new Date();


    @Column(name = "money_fen_2_yuan_without_scale", format = DataFormat.NUMBER, operation = Operation.DIVIDE, operationNumber = 100)
    private Long money_fen_2_yuan_without_scale = 123456789L;
    @Column(name = "money_fen_2_yuan_with_2_scale", format = DataFormat.NUMBER, operation = Operation.DIVIDE, operationNumber = 100, scale = 2)
    private Long money_fen_2_yuan_with_2_scale = 123456789L;


    @Column(name = "money_fen_2_wan_with_comma_without_scale", format = DataFormat.NUMBER, operation = Operation.DIVIDE, operationNumber = 100, scale = 2, numberFormat = "#,##0.00")
    private Long money_fen_2_yuan_with_comma_without_scale = 123456789L;
    @Column(name = "money_fen_2_wan_with_comma_with_2_scale", format = DataFormat.NUMBER, operation = Operation.DIVIDE, operationNumber = 100, scale = 2, roundingMode = BigDecimal.ROUND_HALF_UP, numberFormat = "#,##0")
    private Long money_fen_2_yuan_with_comma_with_2_scale = 123456789L;

    @Column(name = "enum", format = DataFormat.ENUM, enumClass = DemoEnum.class)
    private Number enumX = 1;

    enum DemoEnum {
        /**
         * enum
         */
        FFF;

        private static String desc(Number number) {
            if (NumberUtils.equals(number, 1)) {
                return "enum1";
            }
            if (NumberUtils.equals(number, 2)) {
                return "enum2";
            }
            return "";
        }

    }

    public static void main(String[] args) throws IOException {
        List<Demo> demoList = new ArrayList();
        demoList.add(new Demo());
        String tableName = Excel.getTableName(Demo.class);
        byte[] bytes = Excel.create(Demo.class, demoList);
        //test.xlsx
        File file = new File(tableName + ".xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
    }
}

```