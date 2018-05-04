package com.faynely.fybatis.executor;

import com.faynely.fybatis.executor.parameter.ParameterHandler;
import com.faynely.fybatis.executor.result.ResultHandler;
import com.faynely.fybatis.executor.statement.StatementHandler;

import java.sql.*;

/**
 * 简单的 SQL 执行器
 * @author NickFayne 2018-05-04 10:16
 */
public class SimpleExecutor<T> implements Executor {

    private ParameterHandler parameterHandler;
    private StatementHandler statementHandler;
    private ResultHandler resultHandler;

    public SimpleExecutor(ParameterHandler parameterHandler, StatementHandler statementHandler, ResultHandler resultHandler) {
        this.parameterHandler = parameterHandler;
        this.statementHandler = statementHandler;
        this.resultHandler = resultHandler;
    }

    /**
     * 简单查询
     * @param statement
     * @param parameter
     * @return
     */
    public Object query(String statement, Object parameter, Class clazz) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        T resultObj = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fybatis?useUnicode=true&characterEncoding=utf-8&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "123456");

            //处理参数
            Object param = parameterHandler.processParameter(parameter);
            //拼接 SQL
            String sql = statementHandler.processStatement(statement, param);
            //处理返回结果
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            resultObj = (T) resultHandler.processResult(rs, clazz);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭语句
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                //关闭连接
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return resultObj;
    }
}
