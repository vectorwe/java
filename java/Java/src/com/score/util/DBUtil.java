package com.score.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库连接工具类
 * 适配MySQL 8.0+，配置：数据库名java，用户名root，密码root
 */
public class DBUtil {
    // 数据库连接配置（根据你的环境调整）
    private static final String URL = "jdbc:mysql://localhost:3306/java";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // 静态代码块：加载MySQL驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL驱动加载成功");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL驱动加载失败：" + e.getMessage());
            throw new RuntimeException("驱动加载失败，无法连接数据库");
        }
    }

    /**
     * 获取数据库连接
     * @return Connection对象
     */
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("数据库连接成功");
            return conn;
        } catch (SQLException e) {
            System.err.println("数据库连接失败：" + e.getMessage());
            throw new RuntimeException("数据库连接失败");
        }
    }

    /**
     * 关闭数据库资源（重载：支持不同参数组合）
     */
    public static void close(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("关闭资源失败：" + e.getMessage());
        }
    }

    // 重载：无ResultSet时调用
    public static void close(Connection conn, PreparedStatement stmt) {
        close(conn, stmt, null);
    }
}