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

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * FiledCell
 *
 * @author michael
 * @since 2019-01-08
 */
public class FiledCell {

    private Field field;
    private Column column;
    private Integer index;
    private String name;


    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiledCell)) return false;
        FiledCell filedCell = (FiledCell) o;
        return index.equals(filedCell.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}