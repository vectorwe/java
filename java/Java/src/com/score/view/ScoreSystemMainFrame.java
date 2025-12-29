package com.score.view;

import com.score.dao.User;
import com.score.util.DBUtil; // 引入统一数据库工具类
import com.score.view.menu_right.FeedbackWindow; // 引入反馈窗口工具类
import com.score.view.menu_right.PersonalInfoWindow;
import com.score.view.menu_right.resetPassword;
import com.score.view.menu_right.SystemSettingWindow;
import com.score.view.scoremanage.ScoreImportPanel; // 引入独立的导入面板类
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;

/**
 * 学生成绩管理系统主界面
 * 核心优化：
 * 1. 移除硬编码的反馈窗口逻辑，统一调用FeedbackWindow工具类
 * 2. 复用DBUtil解决数据库连接的Public Key Retrieval错误
 * 3. 适配feedback表的实际字段结构
 * 4. 修复createScoreMenu方法返回值错误，保留原有菜单样式
 * 5. 新增：调用独立的ScoreImportPanel实现自定义表头的单个导入功能
 */
public class ScoreSystemMainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private User loginUser; // 存储当前登录用户信息

    /**
     * 构造方法：创建主界面时必须传入登录用户对象
     *
     * @param loginUser 登录成功后的User对象（非null）
     */
    public ScoreSystemMainFrame(User loginUser) {
        if (loginUser == null) {
            throw new IllegalArgumentException("登录用户信息不能为空！");
        }
        this.loginUser = loginUser;

        initFrameConfig();
        initMenuBar();
        initMainLayout();
        initTableModel();
    }

    /**
     * 步骤1：初始化窗口基础配置
     */
    private void initFrameConfig() {
        this.setLayout(new BorderLayout(10, 10));
        this.setTitle("学生成绩管理系统");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
    }

    /**
     * 步骤2：初始化顶部菜单栏
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 左侧功能菜单组
        JMenu scoreMenu = createScoreMenu();
        menuBar.add(scoreMenu);

        JMenu editMenu = createEditMenu();
        menuBar.add(editMenu);
        JMenu findScore = new JMenu("查找");
        menuBar.add(findScore);
        JMenu changeScore = new JMenu("修改");
        menuBar.add(changeScore);
        // 右侧菜单分隔符
        menuBar.add(Box.createHorizontalGlue());

        // 右侧系统菜单组
        JMenu settingMenu = createSettingMenu();
        menuBar.add(settingMenu);

        JMenu personalMenu = createPersonalMenu();
        menuBar.add(personalMenu);

        JMenu feedbackMenu = createFeedbackMenu(); // 核心修改：调用工具类
        menuBar.add(feedbackMenu);

        this.setJMenuBar(menuBar);
    }

    /**
     * 子方法：创建成绩管理菜单（修复返回值错误，新增单个导入点击事件）
     */
    private JMenu createScoreMenu() {
        // 修复：返回JMenu而非JMenuBar，保留原有菜单结构和样式
        JMenu scoreMenu = new JMenu("成绩管理");

        // 新增成绩子菜单
        JMenu addScore = new JMenu("新增成绩");
        JMenuItem addScoreitem = new JMenuItem("单个导入");
        JMenuItem addScoreitems = new JMenuItem("批量导入");

        // ========== 核心新增：绑定单个导入点击事件 ==========
        addScoreitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToScoreImportPanel();
            }
        });
        // ==================================================

        addScore.add(addScoreitem);
        addScore.add(addScoreitems);
        scoreMenu.add(addScore);

        // 修改成绩
        JMenuItem changeScore = new JMenuItem("修改成绩");
        scoreMenu.add(changeScore);

        // 删除成绩
        JMenuItem deleteScoreItem = new JMenuItem("删除成绩");
        scoreMenu.add(deleteScoreItem);

        // 查找成绩
        JMenuItem findSelectItem = new JMenuItem("查找成绩");
        scoreMenu.add(findSelectItem);
        return scoreMenu;
    }

    /**
     * 核心修改：调用独立的ScoreImportPanel（替换原硬编码面板）
     */
    private void switchToScoreImportPanel() {
        // 1. 创建独立的ScoreImportPanel（传入CardLayout和CardPanel用于返回上一级）
        ScoreImportPanel importPanel = new ScoreImportPanel(cardLayout, cardPanel);
        // 2. 将面板添加到CardLayout（唯一标识：scoreImport）
        cardPanel.add(importPanel, "scoreImport");
        // 3. 切换显示录入面板
        cardLayout.show(cardPanel, "scoreImport");
    }

    /**
     * 子方法：创建编辑菜单
     */
    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("编辑");
        JMenuItem undoItem = new JMenuItem("撤销");
        JMenuItem redoItem = new JMenuItem("重做");
        undoItem.addActionListener(e -> showTip("撤销操作功能待实现"));
        redoItem.addActionListener(e -> showTip("重做操作功能待实现"));
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        return editMenu;
    }

    /**
     * 子方法：创建设置菜单
     */
    private JMenu createSettingMenu() {
        JMenu settingMenu = new JMenu("设置");
        JMenuItem sysSettingItem = new JMenuItem("系统设置");

        sysSettingItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SystemSettingWindow(ScoreSystemMainFrame.this).setVisible(true);
            }
        });

        settingMenu.add(sysSettingItem);
        return settingMenu;
    }

    /**
     * 子方法：创建个人菜单
     */
    private JMenu createPersonalMenu() {
        JMenu personalMenu = new JMenu("个人");
        JMenuItem personalInfoItem = new JMenuItem("个人信息");

        personalInfoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PersonalInfoWindow.openPersonalWindow(ScoreSystemMainFrame.this, loginUser);
            }
        });

        JMenuItem changePwdItem = new JMenuItem("修改密码");
        changePwdItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new resetPassword(ScoreSystemMainFrame.this, loginUser).setVisible(true);
            }
        });

        personalMenu.add(personalInfoItem);
        personalMenu.add(changePwdItem);

        return personalMenu;
    }

    /**
     * 子方法：创建反馈菜单（核心修改：调用FeedbackWindow工具类）
     */
    private JMenu createFeedbackMenu() {
        JMenu feedbackMenu = new JMenu("反馈");
        JMenuItem submitFeedbackItem = new JMenuItem("提交反馈");

        // 核心修改：绑定事件调用FeedbackWindow，传入主窗口+当前登录用户名
        submitFeedbackItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用工具类，传入主窗口和当前登录用户的用户名
                FeedbackWindow.openFeedbackWindow(ScoreSystemMainFrame.this, loginUser.getUsername());
            }
        });

        feedbackMenu.add(submitFeedbackItem);
        return feedbackMenu;
    }

    /**
     * 步骤3：初始化核心布局（CardLayout）
     */
    private void initMainLayout() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel defaultPanel = createDefaultPanel();
        cardPanel.add(defaultPanel, "default");

        this.add(cardPanel, BorderLayout.CENTER);
    }

    /**
     * 子方法：创建默认显示的成绩列表面板
     */
    private JPanel createDefaultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(dataTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 步骤4：初始化成绩表格模型
     */
    private void initTableModel() {
        String[] columnNames = {"学号", "姓名", "科目", "成绩", "录入时间"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dataTable = new JTable(tableModel);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        dataTable.setRowHeight(30);
    }

    /**
     * 通用工具方法：显示提示框
     *
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

    // 测试主方法
    public static void main(String[] args) {
        // 模拟登录用户
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