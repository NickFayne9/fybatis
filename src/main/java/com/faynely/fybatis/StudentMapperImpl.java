package com.faynely.fybatis;


import java.sql.*;

/**
 * Student 表操作实现类
 * @author Faynely 2018-05-03 23:04
 */
public class StudentMapperImpl implements IStudentMapper {

    /**
     * 通过学生的 id 获得某个学生
     * @param id
     * @return
     */
    public Student selectStuById(Integer id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Student student = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String statement = "select * from student where id = %d";
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fybatis?useUnicode=true&characterEncoding=utf-8&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "123456");
            String sql = String.format(statement, id);
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                student = new Student();
                student.setId(rs.getInt(1));
                student.setName(rs.getString(2));
                student.setAge(rs.getInt(3));
                student.setClassId(rs.getInt(4));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return student;
    }
}
