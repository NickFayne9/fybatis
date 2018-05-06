package com.faynely.fybatis;

import com.faynely.fybatis.binding.MapperProxy;
import com.faynely.fybatis.executor.SimpleExecutor;
import com.faynely.fybatis.configuration.Configuration;
import com.faynely.fybatis.sqlsession.SqlSession;
import com.faynely.fybatis.sqlsession.SqlSessionFactory;

/**
 * 测试 fybatis 类
 * @author NickFayne 2018-05-03 23:05
 */
public class FybatisTest {

    public static void main(String[] args) {
        SqlSession sqlSession = SqlSessionFactory.newInstance();
        IStudentMapper studentMapper = sqlSession.getMapper(IStudentMapper.class);

        Student student = studentMapper.selectStuById(1);
        System.out.println(student);

        student = studentMapper.selectStuByIdAndName(1, "小明");
        System.out.println(student);
    }
}