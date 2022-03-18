package com.interfaces.iat.util;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.Properties;

/**
 * HikariCP jdbc 数据库连接池工具
 */
public class HikariCPUtil implements Serializable {
    public static String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/iat_base?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useServerPrepStmts=true&cachePrepStmts=true&rewriteBatchedStatements=true";
    public static String jdbcUsername = "root";
    public static String jdbcPassword = "root";

    //根据cpu的数量动态的配置核心线程数和最大线程数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程数 = CPU核心数 + 1050
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1050;
    //线程池最大线程数 = CPU核心数 * 2 + 100000
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 100000;

    private final static HikariCPUtil HIKARI_CP_UTI = new HikariCPUtil();

    private static Properties properties = null;
    private static HikariDataSource dataSource = null;

    //1.单例模式中,创建私有构造方法
    private HikariCPUtil() {
        // 私有构造
    }

    /**
     * 1.配置和获取数据库连接配置信息
     * 2.扩展HikariCP功能,进行配置
     * 3.获取数据库连接,提供对外获取数据库资源的方法
     */

    private void initConfig() throws IOException {

    }

    private void registerHikariCP() {
        if (null != dataSource) {
            return;
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(jdbcUsername);
        config.setPassword(jdbcPassword);


        //config.addDataSourceProperty("readOnly", "false");//连接只读数据库时配置为true， 保证安全
        config.addDataSourceProperty("connectionTimeout", "60000");//等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
        config.addDataSourceProperty("maxLifetime", "30000");//一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，建议设置比数据库超时时长少30秒，参考MySQL wait_timeout参数（show variables like '%timeout%';）
        config.addDataSourceProperty("idleTimeout", "600000");//一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "100");
        config.addDataSourceProperty("maximumPoolSize", MAXIMUM_POOL_SIZE);//连接池中允许的最大连接数。缺省值：10；推荐的公式：((core_count * 2) + effective_spindle_count)
        dataSource = new HikariDataSource(config);
    }

    //2.提供对外 获取 HikariCPDatasource 的方法
    public static DataSource getHikariCPDataSource() {
        if (null != dataSource) {
            return dataSource;
        }
        try {
            HIKARI_CP_UTI.initConfig();
            HIKARI_CP_UTI.registerHikariCP();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    public static String run(String sql) {
        String str = "";
        // 获取数据库资源
        DataSource dataSource = HikariCPUtil.getHikariCPDataSource();
        System.out.println(Thread.currentThread().getName() + " dataSource = " + dataSource);

        // 使用 try-resource-catch 方式,自动关闭资源
        try {
            //获取数据库连接
            Connection connection = dataSource.getConnection();
            //预编译
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            /**向sql插入数据
             * PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM students WHERE grade = ? AND score >= ?");
             * preparedStatement.setObject(1, 1);// 第一个参数grade=? 第一个参数值为1
             * preparedStatement.setObject(2, 90);// 第二个参数score=? 第二个参数值为90
             * */

            //执行sql - 查询
            ResultSet resultSet = preparedStatement.executeQuery();
            //执行sql - 增、删、改
            //int rows = preparedStatement.executeUpdate();//返回影响的行数

            //获取数据
            ResultSetMetaData metaData = preparedStatement.getMetaData();
            while (resultSet.next()) {
                /**
                 * resultSet.getObject("列名")//根据列名称获取相应列数据信息
                 * */
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    /**
                     * 获取所有列名   metaData.getColumnName(i)
                     * 获取所有行数据 resultSet.getObject(i)
                     * resultSet.getObject(i);//获取所有数据
                     */
                    //str +=metaData.getColumnName(i) + " : " + resultSet.getObject(i);
                    str += resultSet.getObject(i);//获取所有数据
                }
            }
            return str;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return sqlException.toString();
        }
    }


    public static void main(String[] args) {
        String sql = "select name from tb_module limit 1";

        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                Object res = HikariCPUtil.run(sql);
                System.out.println(res.toString());
            }).start();
        }


    }


}
