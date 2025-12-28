package com.score.entity;

import com.score.dao.User;
import com.score.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问实现类（适配忘记密码的三重验证：用户名+邮箱+手机号）
 */
public class UserDaoImpl implements UserDao {

    // 1. 登录验证实现（优化错误提示）
    @Override
    public User login(String username, String password) {
        // 空值校验：避免空指针
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.err.println("登录失败：用户名或密码为空");
            return null;
        }

        String sql = "SELECT * FROM user_data WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setSex(rs.getString("sex"));
                user.setTitle(rs.getString("title"));
                user.setTel(rs.getString("tel")); // 保持tel为字符串类型
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return user;
            } else {
                System.err.println("登录失败：用户名或密码错误，用户名=" + username);
            }
        } catch (SQLException e) {
            System.err.println("登录数据库异常：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // 2. 新增用户（注册）实现（强化tel校验+错误提示）
    @Override
    public boolean addUser(User user) {
        // 空值校验：User对象或关键字段为空直接返回失败
        if (user == null) {
            System.err.println("新增用户失败：User对象为空");
            return false;
        }
        // 关键字段非空校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getTel() == null || user.getTel().trim().isEmpty()) {
            System.err.println("新增用户失败：用户名或手机号为空");
            return false;
        }
        // 手机号格式校验（11位纯数字）
        String tel = user.getTel().trim();
        if (!tel.matches("^\\d{11}$")) {
            System.err.println("新增用户失败：手机号格式错误（非11位纯数字），tel=" + tel);
            return false;
        }

        String sql = "INSERT INTO user_data(name, sex, title, tel, email, username, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 赋值：所有字段做trim，避免空格问题
            stmt.setString(1, user.getName() == null ? "" : user.getName().trim());
            stmt.setString(2, user.getSex() == null ? "" : user.getSex().trim());
            stmt.setString(3, user.getTitle() == null ? "普通用户" : user.getTitle().trim());
            stmt.setString(4, tel); // 已校验的11位手机号字符串
            stmt.setString(5, user.getEmail() == null ? "" : user.getEmail().trim());
            stmt.setString(6, user.getUsername().trim());
            stmt.setString(7, user.getPassword() == null ? "" : user.getPassword().trim());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("新增用户成功：用户名=" + user.getUsername() + "，手机号=" + tel);
                return true;
            } else {
                System.err.println("新增用户失败：无数据插入");
                return false;
            }
        } catch (SQLException e) {
            // 区分用户名重复和其他数据库错误
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("username")) {
                System.err.println("新增用户失败：用户名已存在，username=" + user.getUsername());
            } else {
                System.err.println("新增用户数据库异常：" + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    // 3. 根据用户名查询用户实现（优化空值+资源关闭）
    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("查询用户失败：用户名为空");
            return null;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM user_data WHERE username = ?";
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setSex(rs.getString("sex"));
                user.setTitle(rs.getString("title"));
                user.setTel(rs.getString("tel")); // 字符串tel
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return user;
            } else {
                System.err.println("查询用户失败：用户名不存在，username=" + username);
            }
        } catch (SQLException e) {
            System.err.println("查询用户数据库异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs); // 确保资源关闭
        }
        return null;
    }

    // 4. 修改用户信息实现（强化tel校验）
    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            System.err.println("修改用户失败：User对象或用户名为空");
            return false;
        }
        // 手机号非空时校验格式
        if (user.getTel() != null && !user.getTel().trim().isEmpty()) {
            String tel = user.getTel().trim();
            if (!tel.matches("^\\d{11}$")) {
                System.err.println("修改用户失败：手机号格式错误，tel=" + tel);
                return false;
            }
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE user_data SET name=?, sex=?, title=?, tel=?, email=?, password=? WHERE username=?";
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            // 赋值：trim处理+默认值
            stmt.setString(1, user.getName() == null ? "" : user.getName().trim());
            stmt.setString(2, user.getSex() == null ? "" : user.getSex().trim());
            stmt.setString(3, user.getTitle() == null ? "普通用户" : user.getTitle().trim());
            stmt.setString(4, user.getTel() == null ? "" : user.getTel().trim());
            stmt.setString(5, user.getEmail() == null ? "" : user.getEmail().trim());
            stmt.setString(6, user.getPassword() == null ? "" : user.getPassword().trim());
            stmt.setString(7, user.getUsername().trim());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("修改用户成功：用户名=" + user.getUsername());
                return true;
            } else {
                System.err.println("修改用户失败：用户名不存在或无数据变更，username=" + user.getUsername());
            }
        } catch (SQLException e) {
            System.err.println("修改用户数据库异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, null);
        }
        return false;
    }

    // 5. 删除用户实现（优化提示）
    @Override
    public boolean deleteUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("删除用户失败：用户名为空");
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM user_data WHERE username = ?";
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username.trim());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("删除用户成功：用户名=" + username);
                return true;
            } else {
                System.err.println("删除用户失败：用户名不存在，username=" + username);
            }
        } catch (SQLException e) {
            System.err.println("删除用户数据库异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, null);
        }
        return false;
    }

    // 6. 查询所有用户实现（优化空列表返回）
    @Override
    public List<User> listAllUsers() {
        List<User> userList = new ArrayList<>(); // 确保返回非null列表
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM user_data ORDER BY id DESC";

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setSex(rs.getString("sex"));
                user.setTitle(rs.getString("title"));
                user.setTel(rs.getString("tel")); // 字符串tel
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                userList.add(user);
            }
            System.out.println("查询所有用户成功：共查询到" + userList.size() + "条用户数据");
        } catch (SQLException e) {
            System.err.println("查询所有用户数据库异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }
        return userList;
    }

    // 7. 根据用户名+邮箱查询用户（优化空值）
    @Override
    public User getUserByUsernameAndEmail(String username, String email) {
        if (username == null || email == null || username.trim().isEmpty() || email.trim().isEmpty()) {
            System.err.println("验证用户失败：用户名或邮箱为空");
            return null;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM user_data WHERE username = ? AND email = ?";
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username.trim());
            stmt.setString(2, email.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setSex(rs.getString("sex"));
                user.setTitle(rs.getString("title"));
                user.setTel(rs.getString("tel"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return user;
            } else {
                System.err.println("验证用户失败：用户名或邮箱不匹配，username=" + username + "，email=" + email);
            }
        } catch (SQLException e) {
            System.err.println("验证用户数据库异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }
        return null;
    }

    // ========== 新增核心方法：用户名+邮箱+手机号三重验证 ==========
    /**
     * 根据用户名、邮箱、手机号查询用户（适配忘记密码的三重身份验证）
     * @param username 用户名
     * @param email 绑定邮箱
     * @param tel 绑定手机号
     * @return 匹配的User对象，无匹配返回null
     */
    public User getUserByUsernameEmailTel(String username, String email, String tel) {
        // 1. 空值校验
        if (username == null || email == null || tel == null ||
                username.trim().isEmpty() || email.trim().isEmpty() || tel.trim().isEmpty()) {
            System.err.println("三重验证失败：用户名/邮箱/手机号为空");
            return null;
        }
        // 2. 手机号格式预校验（11位纯数字）
        String cleanTel = tel.trim();
        if (!cleanTel.matches("^\\d{11}$")) {
            System.err.println("三重验证失败：手机号格式错误，tel=" + cleanTel);
            return null;
        }

        // 3. 数据库精准查询
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        // SQL：同时匹配用户名、邮箱、手机号
        String sql = "SELECT * FROM user_data WHERE username = ? AND email = ? AND tel = ?";
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username.trim());
            stmt.setString(2, email.trim());
            stmt.setString(3, cleanTel); // 已校验的11位手机号
            rs = stmt.executeQuery();

            if (rs.next()) {
                // 封装用户对象返回
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setSex(rs.getString("sex"));
                user.setTitle(rs.getString("title"));
                user.setTel(rs.getString("tel"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                System.out.println("三重验证成功：username=" + username + "，tel=" + cleanTel);
                return user;
            } else {
                System.err.println("三重验证失败：用户名/邮箱/手机号不匹配，username=" + username);
            }
        } catch (SQLException e) {
            System.err.println("三重验证数据库异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }
        return null;
    }

    // 8. 重置密码（优化提示）
    @Override
    public boolean resetPassword(String username, String newPassword) {
        if (username == null || newPassword == null || username.trim().isEmpty() || newPassword.trim().isEmpty()) {
            System.err.println("重置密码失败：用户名或新密码为空");
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE user_data SET password = ? WHERE username = ?";
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword.trim());
            stmt.setString(2, username.trim());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("重置密码成功：用户名=" + username);
                return true;
            } else {
                System.err.println("重置密码失败：用户名不存在，username=" + username);
            }
        } catch (SQLException e) {
            System.err.println("重置密码数据库异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, null);
        }
        return false;
    }

}