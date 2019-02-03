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
import com.choodon.common.tool.excel.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel
 *
 * @author michael
 * @since 2019-01-08
 */
public class Excel {

    private static final Logger LOGGER = LoggerFactory.getLogger(Excel.class);

    private static final Map<DataFormat, FormatConvert> FORMAT_CONVERT_HOLDER = new HashMap(DataFormat.values().length * 2);

    private Excel() {
    }

    public static final String getTableName(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("clazz is null");
        }
        Header header = clazz.getAnnotation(Header.class);
        return header == null ? "Book1" : header.value();
    }

    public static final <T> byte[] create(Class<T> clazz, List<T> dataList) {
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("clazz is null");
        }
        String key = UUID.randomUUID().toString();
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet();
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            Row row = sheet.createRow(MapCounter.get(key).intValue());
            List<FiledCell> filedCellList = createFiledCell(clazz);
            filedCellList.stream().forEach(filedCell -> {
                Cell cell = row.createCell(filedCell.getIndex());
                cell.setCellValue(filedCell.getName());
            });
            dataList.stream().filter(Objects::nonNull).forEach(data -> {
                Row excelRow = sheet.createRow(MapCounter.get(key).intValue());
                filedCellList.stream().forEach(filedCell -> {
                    Cell cell = excelRow.createCell(filedCell.getIndex());
                    cell.setCellValue(getCellValue(filedCell, data));
                });
            });
            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error("generate excel file exception", e);
        } finally {
            MapCounter.remove(key);
        }
        return new byte[0];
    }

    private static final List<FiledCell> createFiledCell(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<FiledCell> filedCellList = Arrays.stream(fields).filter(field -> {
            Column column = field.getAnnotation(Column.class);
            if (Objects.isNull(column)) {
                return false;
            }
            Integer index = column.index();
            if (Objects.isNull(index)) {
                return false;
            }
            if (NumberUtils.equals(-1, index)) {
                return false;
            }
            return true;
        }).map(field -> {
            Column column = field.getAnnotation(Column.class);
            FiledCell filedCell = new FiledCell();
            field.setAccessible(true);
            filedCell.setField(field);
            filedCell.setColumn(column);
            filedCell.setIndex(column.index());
            filedCell.setName(column.name());
            return filedCell;
        }).sorted(Comparator.comparing(FiledCell::getIndex)).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filedCellList)) {
            String key = UUID.randomUUID().toString();
            filedCellList = Arrays.stream(fields).filter(field -> {
                Column column = field.getAnnotation(Column.class);
                if (Objects.isNull(column)) {
                    return false;
                }
                return true;
            }).map(field -> {
                Column column = field.getAnnotation(Column.class);
                FiledCell filedCell = new FiledCell();
                field.setAccessible(true);
                filedCell.setField(field);
                filedCell.setColumn(column);
                filedCell.setIndex(MapCounter.get(key).intValue());
                filedCell.setName(column.name());
                return filedCell;
            }).sorted(Comparator.comparing(FiledCell::getIndex)).distinct().collect(Collectors.toList());
            MapCounter.remove(key);
        }
        if (CollectionUtils.isEmpty(filedCellList)) {
            throw new IllegalArgumentException("@Column is null");
        }
        return filedCellList;
    }

    private static final <T> String getCellValue(FiledCell filedCell, T data) {
        if (FORMAT_CONVERT_HOLDER.containsKey(filedCell.getColumn().format())) {
            return FORMAT_CONVERT_HOLDER.get(filedCell.getColumn().format()).convert(filedCell, data);
        }
        return getPlainValue(filedCell, data);
    }

    private static final <T, V> V getVale(FiledCell filedCell, Class<V> clazz, T data) {
        try {
            Object object = filedCell.getField().get(data);
            if (Objects.isNull(object)) {
                return null;
            }
            return (V) object;
        } catch (Exception e) {
            LOGGER.error("get value exception", e);
            return null;
        }
    }

    private static final <T> String getPlainValue(FiledCell filedCell, T data) {
        try {
            Object object = filedCell.getField().get(data);
            if (Objects.isNull(object)) {
                return null;
            }
            return object.toString();
        } catch (Exception e) {
            LOGGER.error("get value exception", e);
            return null;
        }
    }

    private static final String getEnumDes(Class clazz, Number number) {
        if (clazz.isEnum()) {
            Method[] methods = clazz.getDeclaredMethods();
            Optional<Method> methodOptional = Arrays.stream(methods).filter(method -> "desc".equalsIgnoreCase(method.getName())).findFirst();
            if (methodOptional.isPresent()) {
                try {
                    Method method = clazz.getMethod("values");
                    Object[] objects = (Object[]) method.invoke(null);
                    method = methodOptional.get();
                    method.setAccessible(true);
                    Object object = method.invoke(objects[0], number);
                    if (Objects.isNull(object)) {
                        return null;
                    }
                    return object.toString();
                } catch (Exception e) {
                    LOGGER.error("get value exception", e);
                    return null;
                }
            }
        }
        return null;
    }


    private static String getNumberFormat(BigDecimal bigDecimal, Column column) {

        if (column.operationNumber().length > 0 && column.operation() != Operation.NONE) {
            Operation operation = column.operation();
            BigDecimal operationNumber = new BigDecimal(column.operationNumber()[0]);
            switch (operation) {
                case ADD:
                    bigDecimal = bigDecimal.add(operationNumber);
                    break;
                case SUBTRACT:
                    bigDecimal = bigDecimal.subtract(operationNumber);
                    break;
                case MULTIPLY:
                    bigDecimal = bigDecimal.multiply(operationNumber);
                    break;
                case DIVIDE:
                    if (NumberUtils.equals(0, operationNumber)) {
                        LOGGER.error("divide by zero");
                        return null;
                    }
                    bigDecimal = bigDecimal.divide(operationNumber, 20, column.roundingMode());
                    break;
                default:
                    break;
            }

        }
        bigDecimal = bigDecimal.setScale(column.scale(), column.roundingMode());
        if (StringUtils.isNotBlank(column.numberFormat())) {
            DecimalFormat decimalFormat = new DecimalFormat(column.numberFormat());
            return decimalFormat.format(bigDecimal);
        }
        return bigDecimal.toPlainString();
    }


    static {
        FORMAT_CONVERT_HOLDER.put(DataFormat.PLAIN, (filedCell, data) -> getPlainValue(filedCell, data));
        FORMAT_CONVERT_HOLDER.put(DataFormat.DATE_TIME, (filedCell, data) -> {
            if (Date.class == filedCell.getField().getType()) {
                Date date = getVale(filedCell, Date.class, data);
                if (Objects.isNull(date)) {
                    return null;
                }
                String dateTimeFormat = filedCell.getColumn().dateTimeFormat();
                return new SimpleDateFormat(dateTimeFormat).format(date);
            }
            return getPlainValue(filedCell, data);
        });
        FORMAT_CONVERT_HOLDER.put(DataFormat.NUMBER, (filedCell, data) -> {
            if (Number.class.isAssignableFrom(filedCell.getField().getType())) {
                Number number = getVale(filedCell, Number.class, data);
                if (Objects.isNull(number)) {
                    return null;
                }
                BigDecimal bigDecimal = new BigDecimal(number.toString());
                return getNumberFormat(bigDecimal.divide(new BigDecimal(100)), filedCell.getColumn());
            }
            return getPlainValue(filedCell, data);
        });
        FORMAT_CONVERT_HOLDER.put(DataFormat.ENUM, (filedCell, data) -> {
            if (Number.class.isAssignableFrom(filedCell.getField().getType())) {
                Number number = getVale(filedCell, Number.class, data);
                if (Objects.isNull(number)) {
                    return null;
                }
                if (filedCell.getColumn().enumClass().length == 0) {
                    return null;
                }
                return getEnumDes(filedCell.getColumn().enumClass()[0], number);
            }
            return getPlainValue(filedCell, data);
        });

    }

}