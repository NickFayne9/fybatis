package com.faynely.fybatis;


import com.faynely.fybatis.executor.Executor;

/**
 * Student 表操作实现类
 * @author Faynely 2018-05-03 23:04
 */
public class StudentMapperImpl implements IStudentMapper {

    private Executor<Student> executor;

    public StudentMapperImpl(Executor executor) {
        this.executor = executor;
    }

    /**
     * 通过学生的 id 获得某个学生
     * @param id
     * @return
     */
    public Student selectStuById(String statement, Integer id) {
        return executor.query(statement, id, Student.class);
    }
}
