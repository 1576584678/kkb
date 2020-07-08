### 最原始的持久化操作
1. 配置依赖
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.20</version>
</dependency>
```
2. 加载驱动
```java
// 根据数据库类型选择对应的驱动类
Class.forName("com.mysql.cj.jdbc.Driver");
```
3. 获取连接
```java
Connection connection= DriverManager.getConnection(url,username,password);
```
4. 构建Statement
```java
Statement statement=connection.createStatement();
```
5. 利用Statement执行sql,并获取结果集
```java
String sql = "select * from user where id = 1";
ResultSet rs=statement.executeQuery(sql);
```
6. 处理结果集
```java
while (rs.next()){
    System.out.println(rs.getString("username"));
}
```
7. 关闭连接
```java
if (rs!=null){
    rs.close();
}
if(statement!=null){
    statement.close();
}
if (connection!=null){
    connection.close();
}
```