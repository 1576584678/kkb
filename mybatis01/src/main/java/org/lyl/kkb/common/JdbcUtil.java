package org.lyl.kkb.common;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.util.Properties;

public class JdbcUtil {
    private static final BasicDataSource dataSource= new BasicDataSource();
    static {

        Properties properties=PropertiesUtil.loadProperties("jdbc.properties");
        String driverClass = properties.getProperty("jdbc.driver");
        String url = properties.getProperty("jdbc.url");
        String username=properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
    }

    public static Connection getConnection() {
        try {
            // 优化连接处理

            return dataSource.getConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
