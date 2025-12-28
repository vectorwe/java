package com.score.view.login_register;

import com.score.dao.User;
import com.score.entity.UserDao;
import com.score.entity.UserDaoImpl;
import com.score.view.ScoreSystemMainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录界面（适配user_data表+增强验证逻辑）
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDao userDao = new UserDaoImpl(); // 关联数据库DAO

    public LoginFrame() {
        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("学生管理系统 - 登录");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 关闭时不退出程序
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }

    private void initComponents() {
        // 1. 标题面板
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("学生管理系统");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 2. 表单面板（用户名+密码输入，与user_data表的username/password字段对应）
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 25));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 10, 60));

        // 用户名输入框（对应user_data.username）
        JLabel usernameLabel = new JLabel("用户名：");
        usernameLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        usernameField = new JTextField() {{
            setFont(new Font("宋体", Font.PLAIN, 16));
            setToolTipText("请输入注册时的用户名");
        }};

        // 密码输入框（对应user_data.password）
        JLabel passwordLabel = new JLabel("密  码：");
        passwordLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        passwordField = new JPasswordField() {{
            setFont(new Font("宋体", Font.PLAIN, 16));
            setToolTipText("请输入注册时的密码");
        }};

        // 占位标签+忘记密码按钮
        JLabel emptyLabel = new JLabel();
        JButton forgetPwdBtn = new JButton("忘记密码？") {{
            setFont(new Font("宋体", Font.PLAIN, 14));
            setBorderPainted(false);
            setBackground(Color.WHITE);
            setForeground(Color.BLUE);
        }};

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(emptyLabel);
        formPanel.add(forgetPwdBtn);
        add(formPanel, BorderLayout.CENTER);

        // 3. 按钮面板
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        JButton loginBtn = new JButton("登录") {{
            setFont(new Font("宋体", Font.PLAIN, 16));
            setPreferredSize(new Dimension(120, 40));
        }};
        JButton registerBtn = new JButton("注册账号") {{
            setFont(new Font("宋体", Font.PLAIN, 16));
            setPreferredSize(new Dimension(120, 40));
        }};
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // ====================== 事件绑定（核心：数据库验证） ======================
        // 登录按钮：调用UserDao.login()验证数据库中的username/password
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // 1. 非空校验
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "用户名和密码不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 2. 调用DAO查询数据库（验证user_data表中的username和password）
                User user = userDao.login(username, password);
                if (user != null) {
                    // 验证成功：跳转主界面
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "登录成功！欢迎你，" + user.getName(), "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new ScoreSystemMainFrame().setVisible(true);
                } else {
                    // 验证失败：提示错误
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "用户名或密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
                    usernameField.setText("");
                    passwordField.setText("");
                }
            }
        });

        // 注册按钮：跳转到注册界面（添加新用户到数据库）
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
        });

        // 忘记密码按钮：跳转到密码重置界面（更新数据库password字段）
        forgetPwdBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ForgetPwdFrame().setVisible(true);
            }
        });
    }
}