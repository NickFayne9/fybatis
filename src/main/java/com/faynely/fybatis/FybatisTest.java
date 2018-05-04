package com.faynely.fybatis;

/**
 * 测试 fybatis 类
 * @author NickFayne 2018-05-03 23:05
 */
public class FybatisTest {
    public static void main(String[] args) {
        IStudentMapper studentMapper = SqlSession.getStudentMapper();
        Student student = studentMapper.selectStuById(1);

        System.out.println(student);
    }
}