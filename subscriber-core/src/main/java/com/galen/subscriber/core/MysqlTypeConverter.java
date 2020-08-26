package com.galen.subscriber.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shuaiys
 * @version 1.0
 * @package com.galen.subscriber.core
 * @description mysql类型转换成java基本类型
 * @date 2020-08-25 22:45
 */
public class MysqlTypeConverter {

    /**
     * mysql数据类型和Java数据类型映射表
     */
    private static final Map<String, Class<?>> mapping = new HashMap<>();

    static {
        // 初始化映射表
        for (MysqlTypeEnum value : MysqlTypeEnum.values()) {
            mapping.put(value.type, value.clazz);
        }
    }

    /**
     * 将mysql类型转换成具体Class对象
     * @param mysqlType
     * @param value
     * @return
     */
    public static Object convert(String mysqlType, String value) {
        String type = mysqlType.split("\\(")[0];
        Class<?> aClass = mapping.get(type);
        return castValue(value, aClass, type);
    }

    private static Object castValue(String value, Class<?> aClass, String type) {
        Object val;
        if (aClass.equals(Integer.class)) {
            val = Integer.valueOf(value);
        } else if (aClass.equals(Long.class)) {
            val = Long.valueOf(value);
        } else if (aClass.equals(Float.class)) {
            val = Float.valueOf(value);
        } else if (aClass.equals(Double.class)) {
            val = Double.valueOf(value);
        } else if (aClass.equals(BigDecimal.class)) {
            val = new BigDecimal(value);
        } else if (aClass.equals(LocalDate.class)) {
            val = LocalDate.parse(value);
        } else if (aClass.equals(LocalDateTime.class)) {
            if (type.equals("timestamp")) {
                val = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            } else {
                val = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } else if (aClass.equals(LocalTime.class)) {
            val = LocalTime.parse(value);
        } else {
            val = value;
        }
        return val;
    }

    /**
     * 将string类型的值按照class类型转换
     * @param value
     * @param aClass
     * @return
     */
    public static Object castValue(String value, Class<?> aClass) {
        Object val = value;
        if (aClass.equals(Integer.class)) {
            val = Integer.valueOf(value);
        } else if (aClass.equals(Long.class)) {
            val = Long.valueOf(value);
        } else if (aClass.equals(Float.class)) {
            val = Float.valueOf(value);
        } else if (aClass.equals(Double.class)) {
            val = Double.valueOf(value);
        } else if (aClass.equals(BigDecimal.class)) {
            val = new BigDecimal(value);
        } else if (aClass.equals(LocalDate.class)) {
            val = LocalDate.parse(value);
        } else if (aClass.equals(LocalDateTime.class)) {
            LocalDateTime.parse(value);
        } else if (aClass.equals(LocalTime.class)) {
            val = LocalTime.parse(value);
        }
        return val;
    }

    @Getter
    @AllArgsConstructor
    enum MysqlTypeEnum {
        CHAR(1, "char", String.class),
        VARCHAR(2, "varchar", String.class),
        TINY_TEXT(3, "tinytext", String.class),
        TEXT(4, "text", String.class),
        MEDIUM_TEXT(5, "mediumtext", String.class),
        LONG_TEXT(6, "longtext", String.class),
        TINY_INT(7, "tinyint", Integer.class),
        SMALL_INT(8, "smallint", Integer.class),
        MEDIUM_INT(9, "mediumint", Integer.class),
        INT(10, "int", Integer.class),
        BIGINT(11, "bigint", Long.class),
        FLOAT(12, "float", Float.class),
        DOUBLE(13, "double", Double.class),
        DECIMAL(14, "decimal", BigDecimal.class),
        DATE(15, "date", LocalDate.class),
        DATETIME(16, "datetime", LocalDateTime.class),
        TIMESTAMP(17, "timestamp", LocalDateTime.class),
        TIME(18, "time", LocalTime.class),
        ENUM(19, "enum", Enum.class),

        ;

        public int code;

        public String type;

        public Class<?> clazz;
    }


}
