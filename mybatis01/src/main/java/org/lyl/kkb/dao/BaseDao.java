package org.lyl.kkb.dao;

import org.lyl.kkb.common.JdbcUtil;
import org.lyl.kkb.common.PropertiesUtil;
import org.lyl.kkb.entity.User;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BaseDao {
    private static Properties sqlProperties = PropertiesUtil.loadProperties("sql.properties");
    private String nameSpace="db.sql";
    public BaseDao(String name){
        nameSpace+="."+name;
    }

    public <T> List<T> query(String statementId,Object pram){
        String statementName = nameSpace+"."+statementId;
        String sql = sqlProperties.getProperty(statementName);
        List<T> ts = new ArrayList<>();
        try {

            Connection connection = JdbcUtil.getConnection();
            assert connection != null;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if (pram instanceof Map){
                String[] params = sqlProperties.getProperty(statementName+".params").split(",");
                Map<String,Object> paramMap = (Map) pram;
                for (int i = 0; i <params.length ; i++) {
                    preparedStatement.setObject(i+1,paramMap.get(params[i]));
                }
            }else{
                preparedStatement.setObject(1,pram);
            }
            String resultClass = sqlProperties.getProperty(statementName+".resultClass");
            ResultSet rs = preparedStatement.executeQuery();
            Class clazz = Class.forName(resultClass);
            ResultSetMetaData metaData= rs.getMetaData();

            while (rs.next()){
                T t= (T) clazz.getDeclaredConstructor().newInstance();
                int columnCount=metaData.getColumnCount();
                for (int i = 0; i <columnCount ; i++) {
                    String columnName = metaData.getColumnName(i+1);
                    Field field = clazz.getDeclaredField(columnName);
                    String methodName="set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method method = clazz.getMethod(methodName,field.getType());
                    method.invoke(t,rs.getObject(i+1));
                }
                ts.add(t);
            }
            return ts;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
}
