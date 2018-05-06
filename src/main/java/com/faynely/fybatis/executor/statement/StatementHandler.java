package com.faynely.fybatis.executor.statement;

/**
 * SQL 语句处理器
 * @author NickFayne 2018-05-04 10:14
 */
public class StatementHandler {

    public String processStatement(String statement, Object[] param) {
        return String.format(statement, param);
    }
}
