package com.faynely.fybatis.result;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL 结果处理器
 * @author NickFayne 2018-05-04 10:15
 */
public class ResultHandler {
    public <T> T processResult(ResultSet rs, Class<T> clazz) {
        T resultObj = null;
        try{
            resultObj = clazz.newInstance();
            while (rs.next()) {
                Field[] fields = clazz.getDeclaredFields();
                for(Field perField : fields){
                    //获取属性的 set 方法
                    String fieldName = perField.getName();
                    Class fieldType = perField.getType();
                    String setMethodName = "set"
                            + fieldName.substring(0, 1).toUpperCase()
                            + fieldName.substring(1);
                    Method method = clazz.getMethod(setMethodName, fieldType);

                    //判断属性类型，并设置属性值
                    if("java.lang.Integer".equals(fieldType.getName())){
                        method.invoke(resultObj, rs.getInt(fieldName));
                    }else if("java.lang.String".equals(fieldType.getName())){
                        method.invoke(resultObj, rs.getString(fieldName));
                    }
                }
            }
            return resultObj;
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭结果集
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
