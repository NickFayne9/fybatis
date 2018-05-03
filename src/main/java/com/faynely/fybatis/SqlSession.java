package com.faynely.fybatis;

/**
 * Sql 会话类
 * @author Faynely 2018-05-03 22:47
 */
public class SqlSession {

    /**
     * 获取操作 Student 表的 Mapper
     * @return
     */
    public static StudentMapper getStudentMapper(){
        return new StudentMapperImpl();
    }
}
