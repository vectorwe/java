package com.score.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // 核心修复：导入Statement类

/**
 * 数据库连接工具类（统一配置，适配所有场景）
 */
public class DBUtil {
    // 核心修改：统一数据库配置（与UserDaoImpl的业务库保持一致）
    private static final String URL = "jdbc:mysql://localhost:3306/java?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // 加载MySQL驱动（仅执行一次）
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[DBUtil] MySQL驱动加载成功");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBUtil] MySQL驱动加载失败：" + e.getMessage());
            throw new RuntimeException("驱动加载失败，无法连接数据库", e);
        }
    }

    /**
     * 获取数据库连接（抛出运行时异常，上层无需捕获）
     */
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DBUtil] 数据库连接成功：" + URL);
            return conn;
        } catch (SQLException e) {
            System.err.println("[DBUtil] 数据库连接失败：" + e.getMessage());
            throw new RuntimeException("数据库连接失败，请检查配置", e);
        }
    }

    /**
     * 关闭数据库资源（重载1：支持PreparedStatement+ResultSet）
     */
    public static void close(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
            System.out.println("[DBUtil] 资源关闭成功");
        } catch (SQLException e) {
            System.err.println("[DBUtil] 关闭资源失败：" + e.getMessage());
        }
    }

    /**
     * 关闭数据库资源（重载2：仅PreparedStatement）
     */
    public static void close(Connection conn, PreparedStatement stmt) {
        close(conn, stmt, null);
    }

    /**
     * 新增重载：支持Statement+ResultSet（适配listAllUsers方法）
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close(); // 修复：Statement的close()方法需捕获SQLException
            if (conn != null) conn.close();
            System.out.println("[DBUtil] Statement资源关闭成功");
        } catch (SQLException e) {
            System.err.println("[DBUtil] 关闭Statement资源失败：" + e.getMessage());
        }
    }
}