## mybatis v1
1. 添加数据源依赖
`pom.xml`
···xml
<dependency>
    <groupId>commons-dbcp</groupId>
    <artifactId>commons-dbcp</artifactId>
    <version>1.4</version>
</dependency>
```
2. 配置数据源及获取连接的方法

```java
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
```
3. 将sql相关信息抽取到properties文件中

`sql.properties`

```properties
# sql
db.sql.user.queryUserByName=select * from user where username = ?
# 返回类型
db.sql.user.queryUserByName.resultClass=org.lyl.kkb.entity.User
db.sql.user.queryUserBySex=select * from user where sex = ?
db.sql.user.queryUserBySex.resultClass=org.lyl.kkb.entity.User
db.sql.user.queryUserByParams=select * from user where username = ? and sex = ?
# 参数列表
db.sql.user.queryUserByParams.params=username,sex
db.sql.user.queryUserByParams.resultClass=org.lyl.kkb.entity.User
```
4. 读取`sql.properties`，并通过这些信息构建Statement,调用执行方法。将结果集通过反射set到指定的类型实例中。返回List

```java
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
```

5. 测试

```java
@org.junit.Test
public void testQueryUser(){
    BaseDao baseDao = new BaseDao("user");
    List<User> users1 =baseDao.query("queryUserByName","lyl");
    List<User> users2 =baseDao.query("queryUserBySex",0);
    System.out.println(users1);
    System.out.println(users2);
    Map<String,Object> map = new HashMap<>();
    map.put("username","lyl");
    map.put("sex",1);
    List<User> users3 = baseDao.query("queryUserByParams",map);
    System.out.println(users3);
}
```

## 总结
这里的核心是将硬编码提取到配置文件中，通过加载配置文件获取这些信息。该demo中只对查询进行了简单封装。其它都差不多