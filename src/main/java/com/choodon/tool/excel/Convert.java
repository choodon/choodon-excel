package com.choodon.tool.excel;

/**
 * Convert
 *
 * @author michael
 * @since 2019-03-27
 */
@FunctionalInterface
public interface Convert<T> {
    String convert(T data);
}