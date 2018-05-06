package com.faynely.fybatis.binding;

/**
 * sql 和 returnType 封装对象类
 * @author NickFayne 2018-05-06 19:11
 */
public class MapperData {
    private String sql;
    private Class returnType;

    public MapperData(String sql, Class returnType) {
        this.sql = sql;
        this.returnType = returnType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Class getReturnType() {
        return returnType;
    }

    public void setReturnType(Class returnType) {
        this.returnType = returnType;
    }
}
