package com.faynely.fybatis;

import com.faynely.fybatis.annotation.Repository;
import com.faynely.fybatis.annotation.Select;

/**
 * Student 表操作类型接口
 * @author NickFayne 2018-05-03 23:00
 */
@Repository
public interface IStudentMapper {
    @Select("select * from student where id = %d")
    Student selectStuById(Integer id);

    @Select("select * from student where id = %d and name = '%s'")
    Student selectStuByIdAndName(Integer id, String name);
}