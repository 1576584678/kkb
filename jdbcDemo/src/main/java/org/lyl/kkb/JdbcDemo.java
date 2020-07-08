package org.lyl.kkb;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @Description
 * @Author liyuelin
 * @Date 2020/7/8
 */
public class JdbcDemo {

    Properties properties = new Properties();
    public JdbcDemo(){
        InputStream inStream = JdbcDemo.class.getClassLoader().getResourceAsStream("jdbc.properties");
        try {
            properties.load(inStream);
            Class.forName(properties.getProperty("jdbc.driver"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test() throws SQLException {
        Connection connection=null;
        Statement statement=null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        CallableStatement callableStatement = null;

        try{
            String url = properties.getProperty("jdbc.url");
            String username=properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");
            connection= DriverManager.getConnection(url,username,password);
            String sql = "select * from user where id = 1";

            statement=connection.createStatement();
            rs=statement.executeQuery(sql);

            // 预处理
//            preparedStatement=connection.prepareStatement(sql);
//            preparedStatement.executeQuery();

            // 存储过程
//            callableStatement=connection.prepareCall(sql);



            while (rs.next()){
                System.out.println(rs.getString("username"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            if (rs!=null){
                rs.close();
            }
            if(statement!=null){
                statement.close();
            }
            if (connection!=null){
                connection.close();
            }
        }
    }
}
