package com.faynely.fybatis;

import com.faynely.fybatis.binding.MapperProxy;
import com.faynely.fybatis.executor.SimpleExecutor;
import com.faynely.fybatis.configuration.Configuration;
import com.faynely.fybatis.sqlsession.SqlSession;

/**
 * 测试 fybatis 类
 * @author NickFayne 2018-05-03 23:05
 */
public class FybatisTest {

    public static void main(String[] args) {
        SqlSession sqlSession = new SqlSession(new SimpleExecutor(), new Configuration());
        IStudentMapper studentMapper = sqlSession.getMapper(IStudentMapper.class);
        Student student = studentMapper.selectStuById(1);
        System.out.println(student);
    }
}