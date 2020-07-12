package org.lyl.kkb.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    public static Properties loadProperties(String propertiesName){
        InputStream inStream = JdbcUtil.class.getClassLoader().getResourceAsStream(propertiesName);
        Properties properties = new Properties();
        try {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
