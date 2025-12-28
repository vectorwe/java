package com.score.view;

import com.score.dao.User;
import com.score.view.menu_right.PersonalInfoWindow;
import com.score.view.menu_right.resetPassword;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;

/**
 * 学生成绩管理系统主界面
 * 核心功能：
 * 1. 接收登录用户信息并展示可编辑的个人信息（同步数据库）
 * 2. 提供成绩管理、编辑、设置、个人、反馈等菜单功能
 * 3. 展示成绩表格（空表格，可后续对接数据库）
 * 4. 提供反馈提交窗口
 * 优化点：
 * 1. 修复菜单栏重复添加问题（左侧+右侧重复菜单）
 * 2. 统一事件绑定方式，消除语法冗余
 * 3. 接入可修改的个人信息窗口（支持数据库更新）
 * 4. 完善空值校验和用户体验提示
 * 5. 修复修改密码菜单调用逻辑（适配resetPassword接收User对象）
 */
public class ScoreSystemMainFrame extends JFrame {
    // 类成员变量：添加private修饰符，规范作用域
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private User loginUser; // 存储当前登录用户信息（核心）

    /**
     * 构造方法：创建主界面时必须传入登录用户对象
     * @param loginUser 登录成功后的User对象（非null）
     */
    public ScoreSystemMainFrame(User loginUser) {
        // 强制非空校验：防止传入null导致后续空指针
        if (loginUser == null) {
            throw new IllegalArgumentException("登录用户信息不能为空！");
        }
        this.loginUser = loginUser;

        // 初始化流程：按"基础配置→菜单栏→核心布局→表格模型"执行
        initFrameConfig();
        initMenuBar();
        initMainLayout();
        initTableModel();
    }

    /**
     * 步骤1：初始化窗口基础配置（大小、布局、关闭规则等）
     */
    private void initFrameConfig() {
        // 主窗口布局：BorderLayout，组件间距10px（上下左右）
        this.setLayout(new BorderLayout(10, 10));
        this.setTitle("学生成绩管理系统"); // 窗口标题
        this.setSize(1000, 600); // 固定窗口大小
        this.setLocationRelativeTo(null); // 窗口居中显示
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口退出程序
        this.setResizable(false); // 禁止调整窗口大小（防止布局错乱）
    }

    /**
     * 步骤2：初始化顶部菜单栏（核心优化：消除重复菜单）
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ========== 左侧功能菜单组 ==========
        // 1. 成绩管理菜单（包含子菜单和功能项）
        JMenu scoreMenu = createScoreMenu();
        menuBar.add(scoreMenu);

        // 2. 编辑菜单（撤销/重做）
        JMenu editMenu = createEditMenu();
        menuBar.add(editMenu);

        // ========== 右侧菜单分隔符 ==========
        menuBar.add(Box.createHorizontalGlue()); // 水平分隔，推右侧菜单到最右

        // ========== 右侧系统菜单组 ==========
        // 3. 设置菜单
        JMenu settingMenu = createSettingMenu();
        menuBar.add(settingMenu);

        // 4. 个人菜单（核心：绑定可修改的个人信息窗口+修改密码窗口）
        JMenu personalMenu = createPersonalMenu();
        menuBar.add(personalMenu);

        // 5. 反馈菜单（绑定反馈提交窗口）
        JMenu feedbackMenu = createFeedbackMenu();
        menuBar.add(feedbackMenu);

        // 将菜单栏绑定到主窗口
        this.setJMenuBar(menuBar);
    }

    /**
     * 子方法：创建成绩管理菜单（拆分逻辑，降低initMenuBar复杂度）
     * @return 组装好的成绩管理JMenu
     */
    private JMenu createScoreMenu() {
        JMenu scoreMenu = new JMenu("成绩管理");

        // 子菜单：新增成绩（单个导入/批量导入）
        JMenu addScoreSubMenu = new JMenu("新增成绩");
        JMenuItem addSingleItem = new JMenuItem("单个导入");
        JMenuItem addBatchItem = new JMenuItem("批量导入");
        // 绑定事件：提示功能待实现
        addSingleItem.addActionListener(e -> showTip("单个导入成绩功能待实现"));
        addBatchItem.addActionListener(e -> showTip("批量导入成绩功能待实现"));
        addScoreSubMenu.add(addSingleItem);
        addScoreSubMenu.add(addBatchItem);
        scoreMenu.add(addScoreSubMenu);

        // 一级菜单项：修改/删除/查找成绩
        JMenuItem modifyItem = new JMenuItem("修改成绩");
        JMenuItem deleteItem = new JMenuItem("删除成绩");
        JMenuItem searchItem = new JMenuItem("查找成绩");
        // 绑定事件
        modifyItem.addActionListener(e -> showTip("修改成绩功能待实现"));
        deleteItem.addActionListener(e -> showTip("删除成绩功能待实现"));
        searchItem.addActionListener(e -> showTip("查找成绩功能待实现"));
        // 添加到菜单
        scoreMenu.add(modifyItem);
        scoreMenu.add(deleteItem);
        scoreMenu.add(searchItem);

        return scoreMenu;
    }

    /**
     * 子方法：创建编辑菜单（撤销/重做）
     * @return 组装好的编辑JMenu
     */
    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("编辑");
        JMenuItem undoItem = new JMenuItem("撤销");
        JMenuItem redoItem = new JMenuItem("重做");
        // 绑定事件
        undoItem.addActionListener(e -> showTip("撤销操作功能待实现"));
        redoItem.addActionListener(e -> showTip("重做操作功能待实现"));
        // 添加到菜单
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        return editMenu;
    }

    /**
     * 子方法：创建设置菜单（系统设置）
     * @return 组装好的设置JMenu
     */
    private JMenu createSettingMenu() {
        JMenu settingMenu = new JMenu("设置");
        JMenuItem sysSettingItem = new JMenuItem("系统设置");
        sysSettingItem.addActionListener(e -> showTip("系统设置功能待实现"));
        settingMenu.add(sysSettingItem);
        return settingMenu;
    }

    /**
     * 子方法：创建个人菜单（核心：接入可修改的个人信息窗口+修改密码窗口）
     * @return 组装好的个人JMenu
     */
    private JMenu createPersonalMenu() {
        JMenu personalMenu = new JMenu("个人");
        JMenuItem personalInfoItem = new JMenuItem("个人信息");

        // 绑定核心事件：点击后打开【可修改】的个人信息窗口
        personalInfoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用menu_right包下的工具类（支持修改+数据库更新）
                PersonalInfoWindow.openPersonalWindow(ScoreSystemMainFrame.this, loginUser);
            }
        });

        // 核心修复：修改密码菜单项（适配resetPassword接收User对象）
        JMenuItem changePwdItem = new JMenuItem("修改密码");
        changePwdItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 直接传入主窗口对象 + 登录用户对象（适配新版resetPassword构造函数）
                new resetPassword(ScoreSystemMainFrame.this, loginUser).setVisible(true);
            }
        });

        // 添加菜单项到个人菜单
        personalMenu.add(personalInfoItem);
        personalMenu.add(changePwdItem);

        return personalMenu;
    }

    /**
     * 子方法：创建反馈菜单（打开反馈提交窗口）
     * @return 组装好的反馈JMenu
     */
    private JMenu createFeedbackMenu() {
        JMenu feedbackMenu = new JMenu("反馈");
        JMenuItem submitFeedbackItem = new JMenuItem("提交反馈");

        // 绑定事件：点击后打开反馈窗口
        submitFeedbackItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFeedbackWindow();
            }
        });

        feedbackMenu.add(submitFeedbackItem);
        return feedbackMenu;
    }

    /**
     * 步骤3：初始化核心布局（CardLayout）- 用于后续切换不同面板
     */
    private void initMainLayout() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout); // 卡片面板：可切换不同内容

        // 默认显示的面板：成绩表格
        JPanel defaultPanel = createDefaultPanel();
        cardPanel.add(defaultPanel, "default"); // 给面板命名，方便后续切换

        // 将卡片面板添加到主窗口中央
        this.add(cardPanel, BorderLayout.CENTER);
    }

    /**
     * 子方法：创建默认显示的成绩列表面板
     * @return 包含表格的JPanel
     */
    private JPanel createDefaultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        // 面板内边距：上下左右各10px（避免组件贴边）
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 表格添加到滚动面板（防止数据过多时无法滚动）
        JScrollPane scrollPane = new JScrollPane(dataTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 步骤4：初始化成绩表格模型（不可编辑、固定列名）
     */
    private void initTableModel() {
        // 表格列名：学号、姓名、科目、成绩、录入时间
        String[] columnNames = {"学号", "姓名", "科目", "成绩", "录入时间"};

        // 创建表格模型：0行数据，不可编辑
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 所有单元格不可编辑
            }
        };

        // 初始化表格
        dataTable = new JTable(tableModel);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单行选择
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // 自动调整列宽
        dataTable.setRowHeight(30); // 行高30px（提升可读性）
    }

    /**
     * 核心功能：打开反馈提交窗口（模态对话框，阻塞主界面操作）
     */
    private void openFeedbackWindow() {
        // 创建模态对话框（必须关闭才能操作主界面）
        JDialog feedbackDialog = new JDialog(this, "提交反馈", true);
        feedbackDialog.setSize(600, 400); // 固定大小
        feedbackDialog.setLocationRelativeTo(this); // 相对于主窗口居中
        feedbackDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // 关闭仅销毁对话框
        feedbackDialog.setLayout(new BorderLayout(10, 10)); // 布局间距10px
        feedbackDialog.setResizable(false); // 禁止调整大小

        // ========== 反馈标题面板 ==========
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.add(new JLabel("反馈标题："), BorderLayout.WEST);
        JTextField titleField = new JTextField(); // 标题输入框
        titlePanel.add(titleField, BorderLayout.CENTER);

        // ========== 反馈内容面板 ==========
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(new JLabel("反馈内容："), BorderLayout.NORTH);
        JTextArea contentArea = new JTextArea(); // 内容输入框
        contentArea.setLineWrap(true); // 自动换行
        contentArea.setRows(10); // 默认10行高度
        contentPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER); // 滚动面板包裹

        // ========== 提交按钮 ==========
        JButton submitBtn = new JButton("提交");
        submitBtn.addActionListener(e -> {
            // 可选：后续可将反馈内容写入数据库
            showTip("反馈提交成功！");
            feedbackDialog.dispose(); // 关闭对话框
        });

        // ========== 组装反馈窗口 ==========
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 内边距10px
        mainPanel.add(titlePanel, BorderLayout.NORTH); // 标题在北
        mainPanel.add(contentPanel, BorderLayout.CENTER); // 内容在中
        mainPanel.add(submitBtn, BorderLayout.SOUTH); // 按钮在南

        // 添加主面板并显示对话框
        feedbackDialog.add(mainPanel);
        feedbackDialog.setVisible(true);
    }

    /**
     * 通用工具方法：显示提示框（抽离重复代码，提高复用性）
     * @param message 提示内容
     */
    private void showTip(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "提示",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // 测试主方法（可选：用于单独测试主界面）
    public static void main(String[] args) {
        // 模拟登录用户（实际使用时从登录窗口传入）
        User testUser = new User();
        testUser.setUsername("admin");
        testUser.setTitle("管理员");
        testUser.setName("系统管理员");
        testUser.setTel("13800138000");
        testUser.setEmail("admin@test.com");

        // 启动主界面
        SwingUtilities.invokeLater(() -> {
            new ScoreSystemMainFrame(testUser).setVisible(true);
        });
    }
}