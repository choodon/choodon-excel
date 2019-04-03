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
package com.choodon.tool.excel;

import com.choodon.tool.excel.anotation.Column;
import com.choodon.tool.excel.anotation.Header;
import com.choodon.tool.excel.enums.DataFormat;
import com.choodon.tool.excel.enums.Operation;
import com.choodon.tool.excel.util.NumberUtils;
import com.choodon.tool.excel.util.StringUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Excel
 *
 * @author michael
 * @since 2019-01-08
 */
public class Excel {

    private static final Logger LOGGER = LoggerFactory.getLogger(Excel.class);

    private static final Map<DataFormat, Format> FORMAT_HOLDER = new HashMap(DataFormat.values().length * 2);

    private Excel() {
    }

    public static <Model> String getTableName(Class<Model> clazz) {
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("clazz is null");
        }
        Header header = clazz.getAnnotation(Header.class);
        return header == null ? "Book1" : header.value();
    }

    public static <Model> byte[] create(Class<Model> clazz, List<Model> modelList) {
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("clazz is null");
        }
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet();
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            AtomicInteger rowNum = new AtomicInteger();
            Row row = sheet.createRow(rowNum.getAndIncrement());
            List<FiledCell> filedCellList = createFiledCell(clazz);
            filedCellList.stream().forEach(filedCell -> {
                Cell cell = row.createCell(filedCell.getIndex());
                cell.setCellValue(filedCell.getName());
            });
            modelList.stream().filter(Objects::nonNull).forEach(model -> {
                Row excelRow = sheet.createRow(rowNum.getAndIncrement());
                filedCellList.stream().forEach(filedCell -> {
                    Cell cell = excelRow.createCell(filedCell.getIndex());
                    cell.setCellValue(getCellValue(filedCell, model));
                });
            });
            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error("generate excel file exception", e);
        }
        return new byte[0];
    }

    private static List<Field> listField(Class<?> clazz) {
        List<Class> clazzList = new ArrayList<>();
        while (clazz != Object.class) {
            clazzList.add(clazz);
            clazz = clazz.getSuperclass();
        }
        Collections.reverse(clazzList);
        return clazzList.stream().flatMap(item -> Arrays.stream(item.getDeclaredFields())).collect(Collectors.toList());
    }

    private static <Model> List<FiledCell> createFiledCell(Class<Model> clazz) {
        List<Field> fieldList = listField(clazz);

        List<FiledCell> filedCellList = fieldList.stream().filter(field -> {
            Column column = field.getAnnotation(Column.class);
            if (Objects.isNull(column)) {
                return false;
            }
            Integer index = column.index();
            if (NumberUtils.equals(-1, index)) {
                return false;
            }
            return true;
        }).map(field -> buildFiledCell(field, null))
                .sorted(Comparator.comparing(FiledCell::getIndex)).distinct().collect(Collectors.toList());

        if (CollectionUtils.isEmpty(filedCellList)) {
            AtomicInteger index = new AtomicInteger();
            filedCellList = fieldList.stream().filter(field -> {
                Column column = field.getAnnotation(Column.class);
                if (Objects.isNull(column)) {
                    return false;
                }
                return true;
            }).map(field -> buildFiledCell(field, index.getAndIncrement()))
                    .sorted(Comparator.comparing(FiledCell::getIndex)).distinct().collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(filedCellList)) {
            throw new IllegalArgumentException("@Column is null");
        }

        return filedCellList;
    }

    private static FiledCell buildFiledCell(Field field, Integer index) {
        Column column = field.getAnnotation(Column.class);
        if (Objects.isNull(index)) {
            index = column.index();
        }
        FiledCell filedCell = new FiledCell();
        filedCell.setIndex(index);
        filedCell.setName(column.name());
        filedCell.setColumn(column);
        field.setAccessible(true);
        filedCell.setField(field);
        if (DataFormat.PLAIN.equals(column.format())) {
            return filedCell;
        }
        if (DataFormat.DATE_TIME.equals(column.format())) {
            buildDateTimeFiledCell(filedCell);
            return filedCell;
        }
        if (DataFormat.ENUM.equals(column.format())) {
            buildEnumFiledCell(filedCell);
            return filedCell;
        }
        if (DataFormat.CUSTOM.equals(column.format())) {
            buildCustomFiledCell(filedCell);
            return filedCell;
        }
        if (DataFormat.NUMBER.equals(column.format())) {
            buildNumberFiledCell(filedCell);
            return filedCell;
        }
        return filedCell;
    }

    private static void buildDateTimeFiledCell(FiledCell filedCell) {
        Column column = filedCell.getColumn();
        if (StringUtils.isBlank(column.dateTimeFormat())) {
            return;
        }
        filedCell.setFormat(new SimpleDateFormat(column.dateTimeFormat()));
    }

    private static void buildEnumFiledCell(FiledCell filedCell) {
        Column column = filedCell.getColumn();
        if (column.enumClass().length == 0) {
            return;
        }
        Class<?> clazz = column.enumClass()[0];
        if (clazz.isEnum()) {
            Method[] methods = clazz.getDeclaredMethods();
            Optional<Method> methodOptional = Arrays.stream(methods).filter(method -> "desc".equalsIgnoreCase(method.getName())).findFirst();
            if (methodOptional.isPresent()) {
                try {
                    Method method = clazz.getMethod("values");
                    Object[] objects = (Object[]) method.invoke(null);
                    filedCell.setFormat(objects[0]);
                    method = methodOptional.get();
                    method.setAccessible(true);
                    filedCell.setMethod(method);
                } catch (Exception e) {
                    LOGGER.error("get [desc] method exception", e);
                }
            }
        }
    }

    private static void buildCustomFiledCell(FiledCell filedCell) {
        Column column = filedCell.getColumn();
        if (column.convertClass().length == 0) {
            return;
        }
        Class convertClass = filedCell.getColumn().convertClass()[0];
        boolean isSub = Arrays.stream(convertClass.getInterfaces()).anyMatch(interfaceClazz -> interfaceClazz == Convert.class);
        if (isSub) {
            try {
                filedCell.setFormat(convertClass.newInstance());
            } catch (Exception e) {
                LOGGER.error("create instance exception", e);
            }

        }
    }

    private static void buildNumberFiledCell(FiledCell filedCell) {
        Column column = filedCell.getColumn();
        if (StringUtils.isNotBlank(column.numberFormat())) {
            DecimalFormat decimalFormat = new DecimalFormat(column.numberFormat());
            filedCell.setFormat(decimalFormat);
        }
    }

    private static <Model> String getCellValue(FiledCell filedCell, Model model) {
        if (FORMAT_HOLDER.containsKey(filedCell.getColumn().format())) {
            return FORMAT_HOLDER.get(filedCell.getColumn().format()).format(filedCell, model);
        }
        return getPlainValue(filedCell, model);
    }

    private static <Model> Object getVale(FiledCell filedCell, Model model) {
        try {
            Object object = filedCell.getField().get(model);
            if (Objects.isNull(object)) {
                return null;
            }
            return object;
        } catch (Exception e) {
            LOGGER.error("get value exception", e);
            return null;
        }
    }

    private static final <Model, DATA> DATA getVale(FiledCell filedCell, Class<DATA> clazz, Model model) {
        try {
            Object object = filedCell.getField().get(model);
            if (Objects.isNull(object)) {
                return null;
            }
            return (DATA) object;
        } catch (Exception e) {
            LOGGER.error("get value exception", e);
            return null;
        }
    }

    private static <Model> String getPlainValue(FiledCell filedCell, Model model) {
        try {
            Object object = filedCell.getField().get(model);
            if (Objects.isNull(object)) {
                return null;
            }
            return object.toString();
        } catch (Exception e) {
            LOGGER.error("get value exception", e);
            return null;
        }
    }


    private static <Model> BigDecimal getNumberValue(FiledCell filedCell, Model model) {
        Column column = filedCell.getColumn();
        Object object = getVale(filedCell, model);
        if (Objects.isNull(object)) {
            return null;
        }
        BigDecimal bigDecimal = new BigDecimal(object.toString());
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
        if (NumberUtils.notEquals(-1, column.scale()) && column.scale() >= 0) {
            bigDecimal = bigDecimal.setScale(column.scale(), column.roundingMode());
        }
        return bigDecimal;
    }

    private static String format(FiledCell filedCell, Object value) {
        boolean canFormat = Objects.nonNull(filedCell.getFormat()) && Objects.nonNull(filedCell.getMethod());
        if (canFormat) {
            try {
                value = filedCell.getMethod().invoke(filedCell.getFormat(), value);
                if (Objects.nonNull(value)) {
                    return value.toString();
                }
            } catch (Exception e) {
                LOGGER.error("format data exception", e);
            }
        }
        return value.toString();
    }

    static {
        FORMAT_HOLDER.put(DataFormat.PLAIN, (filedCell, model) -> getPlainValue(filedCell, model));
        FORMAT_HOLDER.put(DataFormat.DATE_TIME, (filedCell, model) -> {
            if (Date.class == filedCell.getField().getType()) {
                Date value = getVale(filedCell, Date.class, model);
                if (Objects.isNull(value)) {
                    return null;
                }
                if (Objects.nonNull(filedCell.getFormat())) {
                    SimpleDateFormat simpleDateFormat = (SimpleDateFormat) filedCell.getFormat();
                    return simpleDateFormat.format(value);
                }

            }
            return getPlainValue(filedCell, model);
        });
        FORMAT_HOLDER.put(DataFormat.NUMBER, (filedCell, model) -> {
            if (isNumPrimitive(filedCell.getField().getType()) || Number.class.isAssignableFrom(filedCell.getField().getType())) {
                BigDecimal value = getNumberValue(filedCell, model);
                if (Objects.isNull(value)) {
                    return null;
                }
                if (Objects.nonNull(filedCell.getFormat())) {
                    DecimalFormat decimalFormat = (DecimalFormat) filedCell.getFormat();
                    return decimalFormat.format(value);
                }
                return value.toPlainString();
            }
            return getPlainValue(filedCell, model);
        });
        FORMAT_HOLDER.put(DataFormat.ENUM, (filedCell, model) -> {
            if (Number.class.isAssignableFrom(filedCell.getField().getType())) {
                Object value = getVale(filedCell, model);
                if (Objects.isNull(value)) {
                    return null;
                }
                return format(filedCell, value);
            }
            return getPlainValue(filedCell, model);
        });
        FORMAT_HOLDER.put(DataFormat.CUSTOM, (filedCell, model) -> {
            Object value = getVale(filedCell, model);
            if (Objects.isNull(value)) {
                return null;
            }
            if (Objects.nonNull(filedCell.getFormat())) {
                Convert convert = (Convert) filedCell.getFormat();
                return convert.convert(value);
            }
            return null;
        });
    }

    private static boolean isNumPrimitive(Class clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == char.class || clazz == boolean.class || clazz == Void.class) {
                return false;
            }
            return true;
        }
        return false;

    }

}
