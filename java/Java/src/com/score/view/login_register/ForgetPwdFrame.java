package com.score.view.login_register;

import com.score.dao.User;
import com.score.entity.UserDao;
import com.score.entity.UserDaoImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 忘记密码界面（优化版：用户名+邮箱+手机号三重验证 + 密码重置同步数据库）
 */
public class ForgetPwdFrame extends JDialog {
    private JTextField usernameField, emailField, telField; // 新增：tel手机号文本框
    private JPasswordField newPwdField, confirmPwdField;
    private UserDao userDao = new UserDaoImpl();
    private boolean isAuthSuccess = false; // 身份验证标记

    public ForgetPwdFrame() {
        super((Frame) null, "忘记密码", true); // 模态对话框
        initFrame();
        initComponents();
    }

    private void initFrame() {
        setSize(480, 550); // 调整窗口高度，适配新增的tel文本框
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }

    private void initComponents() {
        // 1. 标题面板
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("重置密码");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 2. 表单面板（拆分：身份验证 + 密码重置）
        JPanel mainFormPanel = new JPanel();
        mainFormPanel.setLayout(new BoxLayout(mainFormPanel, BoxLayout.Y_AXIS));
        mainFormPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        // 2.1 身份验证区域（新增：tel手机号文本框）
        JPanel authPanel = new JPanel();
        authPanel.setLayout(new GridLayout(3, 2, 10, 15)); // 改为3行2列，适配tel字段
        authPanel.setBorder(BorderFactory.createTitledBorder("身份验证（用户名+邮箱+手机号）"));

        // 用户名
        authPanel.add(new JLabel("用户名：") {{ setFont(new Font("宋体", Font.PLAIN, 16)); }});
        usernameField = new JTextField() {{ setFont(new Font("宋体", Font.PLAIN, 16)); }};

        // 绑定邮箱
        authPanel.add(new JLabel("绑定邮箱：") {{ setFont(new Font("宋体", Font.PLAIN, 16)); }});
        emailField = new JTextField() {{ setFont(new Font("宋体", Font.PLAIN, 16)); }};

        // 新增：绑定手机号
        authPanel.add(new JLabel("绑定手机号：") {{ setFont(new Font("宋体", Font.PLAIN, 16)); }});
        telField = new JTextField() {{
            setFont(new Font("宋体", Font.PLAIN, 16));
            setToolTipText("请输入11位纯数字手机号"); // 提示文本
        }};

        // 验证按钮 + 提示标签面板
        JLabel authTipLabel = new JLabel("");
        authTipLabel.setForeground(Color.RED);
        authTipLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        JButton authBtn = new JButton("验证身份");
        authBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        authBtn.setPreferredSize(new Dimension(120, 30));

        JPanel authBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        authBtnPanel.add(authBtn);
        authBtnPanel.add(authTipLabel);

        // 2.2 密码重置区域（初始隐藏）
        JPanel pwdResetPanel = new JPanel();
        pwdResetPanel.setLayout(new GridLayout(2, 2, 10, 20));
        pwdResetPanel.setBorder(BorderFactory.createTitledBorder("设置新密码"));
        pwdResetPanel.setVisible(false); // 初始隐藏

        pwdResetPanel.add(new JLabel("新密码：") {{ setFont(new Font("宋体", Font.PLAIN, 16)); }});
        newPwdField = new JPasswordField() {{ setFont(new Font("宋体", Font.PLAIN, 16)); }};

        pwdResetPanel.add(new JLabel("确认新密码：") {{ setFont(new Font("宋体", Font.PLAIN, 16)); }});
        confirmPwdField = new JPasswordField() {{ setFont(new Font("宋体", Font.PLAIN, 16)); }};

        // 组装表单面板
        mainFormPanel.add(authPanel);
        mainFormPanel.add(authBtnPanel);
        mainFormPanel.add(Box.createVerticalStrut(15));
        mainFormPanel.add(pwdResetPanel);
        add(mainFormPanel, BorderLayout.CENTER);

        // 3. 底部按钮面板
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));

        JButton resetBtn = new JButton("确认重置");
        resetBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        resetBtn.setPreferredSize(new Dimension(120, 40));
        resetBtn.setEnabled(false); // 初始禁用

        JButton cancelBtn = new JButton("取消");
        cancelBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        cancelBtn.setPreferredSize(new Dimension(120, 40));

        btnPanel.add(resetBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // ====================== 核心事件绑定 ======================
        // 1. 身份验证按钮事件（用户名+邮箱+手机号三重校验）
        authBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. 获取并清洗输入数据
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String tel = telField.getText().trim();

                // 2. 基础非空校验
                if (username.isEmpty() || email.isEmpty() || tel.isEmpty()) {
                    authTipLabel.setText("用户名、邮箱、手机号不能为空！");
                    return;
                }

                // 3. 手机号格式校验（11位纯数字）
                if (!tel.matches("^\\d{11}$")) {
                    authTipLabel.setText("手机号格式错误（请输入11位纯数字）！");
                    return;
                }

                // 4. 调用DAO查询数据库，比对三重信息
                User user = userDao.getUserByUsernameAndEmail(username, email);
                if (user != null) {
                    // 额外比对手机号（和数据库中的tel字段匹配）
                    String dbTel = user.getTel(); // 从数据库获取绑定的手机号
                    if (tel.equals(dbTel)) {
                        // 验证成功
                        isAuthSuccess = true;
                        authTipLabel.setForeground(Color.GREEN);
                        authTipLabel.setText("验证成功！请设置新密码");
                        pwdResetPanel.setVisible(true);
                        resetBtn.setEnabled(true);
                        // 锁定输入框，防止篡改
                        usernameField.setEditable(false);
                        emailField.setEditable(false);
                        telField.setEditable(false);
                    } else {
                        // 手机号不匹配
                        isAuthSuccess = false;
                        authTipLabel.setForeground(Color.RED);
                        authTipLabel.setText("手机号与账户绑定的不一致！");
                        pwdResetPanel.setVisible(false);
                        resetBtn.setEnabled(false);
                    }
                } else {
                    // 用户名/邮箱不匹配
                    isAuthSuccess = false;
                    authTipLabel.setForeground(Color.RED);
                    authTipLabel.setText("用户名或邮箱不匹配！");
                    pwdResetPanel.setVisible(false);
                    resetBtn.setEnabled(false);
                }
            }
        });

        // 2. 确认重置密码事件（同步到数据库）
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 双重校验身份
                if (!isAuthSuccess) {
                    JOptionPane.showMessageDialog(ForgetPwdFrame.this,
                            "请先完成身份验证！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 1. 获取密码数据
                String username = usernameField.getText().trim();
                String newPwd = new String(newPwdField.getPassword()).trim();
                String confirmPwd = new String(confirmPwdField.getPassword()).trim();

                // 2. 密码校验
                if (newPwd.isEmpty()) {
                    JOptionPane.showMessageDialog(ForgetPwdFrame.this,
                            "新密码不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (newPwd.length() < 6) { // 新增：密码长度校验
                    JOptionPane.showMessageDialog(ForgetPwdFrame.this,
                            "密码长度不能少于6位！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!newPwd.equals(confirmPwd)) {
                    JOptionPane.showMessageDialog(ForgetPwdFrame.this,
                            "两次密码输入不一致！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 3. 调用DAO重置密码（更新数据库）
                boolean isSuccess = userDao.resetPassword(username, newPwd);
                if (isSuccess) {
                    JOptionPane.showMessageDialog(ForgetPwdFrame.this,
                            "密码重置成功！新密码已同步到数据库", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 关闭窗口
                    // 可选：跳转到登录界面
                    new LoginFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(ForgetPwdFrame.this,
                            "密码重置失败！请检查数据库连接或重试", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 3. 取消按钮事件
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭窗口
            }
        });
    }

    // 测试主方法（可选）
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ForgetPwdFrame().setVisible(true));
    }
}