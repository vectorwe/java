package com.score.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;

/**
 * 学生成绩管理系统主界面
 * 优化点：规范命名、修复编译错误、完善布局、添加事件绑定、优化代码结构
 */
public class ScoreSystemMainFrame extends JFrame {
    // 类成员变量声明 - 添加访问修饰符，规范命名
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    /**
     * 构造方法 - 主入口
     */
    public ScoreSystemMainFrame() {
        // 初始化流程：基础配置 → 菜单栏 → 核心布局 → 表格模型
        initFrameConfig();
        initMenuBar();
        initMainLayout();
        initTableModel();
    }

    /**
     * 窗口基础设置 - 修复拼写错误，完善注释
     */
    private void initFrameConfig() {
        // 明确设置主窗口布局为BorderLayout（带间距）
        this.setLayout(new BorderLayout(10, 10));
        this.setLocationRelativeTo(null); // 窗口居中显示
        this.setTitle("学生成绩管理系统");
        this.setSize(1000, 600);
        // 关闭窗口时退出程序
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 禁止窗口大小调整，保证布局稳定
        this.setResizable(false);
    }

    /**
     * 构建顶部菜单栏（北区域）
     * 优化：修复重复定义、规范命名、添加事件监听器、拆分逻辑
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // 左侧功能菜单组
        // 新增成绩子菜单
        JMenu scoreMenu = new JMenu("成绩管理");
        JMenu addScore = new JMenu ("新增成绩");
        JMenuItem addScoreitem= new JMenuItem("单个导入");
        JMenuItem addScoreitems = new JMenuItem("批量导入");
        addScore.add(addScoreitem);       // 修复：原代码重复添加了addScoreitem，现在改为添加addScoreitems
        addScore.add(addScoreitems);
        scoreMenu.add(addScore);

// 修改成绩
        JMenuItem changeScore= new JMenuItem("修改成绩");
        scoreMenu.add(changeScore);
// 删除成绩
        JMenuItem deleteScoreItem = new JMenuItem("删除成绩");
        scoreMenu.add(deleteScoreItem);
        JMenuItem findSelectItem = new JMenuItem("查找成绩");
        scoreMenu.add(findSelectItem);
        menuBar.add(scoreMenu);

// 编辑菜单（修复：原代码重复定义了editMenu，只保留一份即可）
        JMenu editMenu = new JMenu("编辑");
        JMenuItem undoItem = new JMenuItem("撤销");
        JMenuItem redoItem = new JMenuItem("重做");
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        menuBar.add(editMenu);

// 右侧菜单组
        JMenu settingMenu = new JMenu("设置");
        menuBar.add(settingMenu);

        JMenu personalMenu = new JMenu("个人");
        menuBar.add(personalMenu);

        JMenu feedbackMenu = new JMenu("反馈");
        menuBar.add(feedbackMenu);
        // 添加水平分隔符
        menuBar.add(Box.createHorizontalGlue());

        // 右侧系统菜单组
        menuBar.add(createSettingMenu());    // 设置菜单
        menuBar.add(createPersonalMenu());   // 个人菜单
        menuBar.add(createFeedbackMenu());   // 反馈菜单

        // 将菜单栏设置到窗口
        this.setJMenuBar(menuBar);
    }

    /**
     * 创建成绩管理菜单
     */
    private JMenu createScoreMenu() {
        JMenu scoreMenu = new JMenu("成绩管理");

        // 新增成绩子菜单
        JMenu addScoreMenu = new JMenu("新增成绩");
        JMenuItem addSingleScoreItem = new JMenuItem("单个导入");
        JMenuItem addBatchScoreItem = new JMenuItem("批量导入");
        // 添加事件监听器
        addSingleScoreItem.addActionListener(e -> showTip("单个导入成绩功能待实现"));
        addBatchScoreItem.addActionListener(e -> showTip("批量导入成绩功能待实现"));
        addScoreMenu.add(addSingleScoreItem);
        addScoreMenu.add(addBatchScoreItem);
        scoreMenu.add(addScoreMenu);

        // 其他成绩操作菜单项
        JMenuItem modifyScoreItem = new JMenuItem("修改成绩");
        JMenuItem deleteScoreItem = new JMenuItem("删除成绩");
        JMenuItem searchScoreItem = new JMenuItem("查找成绩");

        // 绑定事件
        modifyScoreItem.addActionListener(e -> showTip("修改成绩功能待实现"));
        deleteScoreItem.addActionListener(e -> showTip("删除成绩功能待实现"));
        searchScoreItem.addActionListener(e -> showTip("查找成绩功能待实现"));

        scoreMenu.add(modifyScoreItem);
        scoreMenu.add(deleteScoreItem);
        scoreMenu.add(searchScoreItem);

        return scoreMenu;
    }

    /**
     * 创建编辑菜单
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
     * 创建设置菜单
     */
    private JMenu createSettingMenu() {
        JMenu settingMenu = new JMenu("设置");
        JMenuItem sysSettingItem = new JMenuItem("系统设置");
        sysSettingItem.addActionListener(e -> showTip("系统设置功能待实现"));
        settingMenu.add(sysSettingItem);
        return settingMenu;
    }

    /**
     * 创建个人菜单
     */
    private JMenu createPersonalMenu() {
        JMenu personalMenu = new JMenu("个人");
        JMenuItem personalInfoItem = new JMenuItem("个人信息");
        personalInfoItem.addActionListener(e -> openPersonalWindow());
        personalMenu.add(personalInfoItem);
        return personalMenu;
    }

    /**
     * 创建反馈菜单
     */
    private JMenu createFeedbackMenu() {
        JMenu feedbackMenu = new JMenu("反馈");
        JMenuItem submitFeedbackItem = new JMenuItem("提交反馈");
        submitFeedbackItem.addActionListener(e -> openFeedbackWindow());
        feedbackMenu.add(submitFeedbackItem);
        return feedbackMenu;
    }

    /**
     * 初始化核心布局（CardLayout）
     * 完善原未实现的方法
     */
    private void initMainLayout() {
        // 初始化卡片布局和面板
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 创建默认显示的成绩列表面板
        JPanel defaultPanel = createDefaultPanel();
        cardPanel.add(defaultPanel, "default");

        // 将卡片面板添加到主窗口中心区域
        this.add(cardPanel, BorderLayout.CENTER);
    }

    /**
     * 创建默认显示的成绩列表面板
     */
    private JPanel createDefaultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 添加表格到滚动面板
        JScrollPane scrollPane = new JScrollPane(dataTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 初始化表格模型
     * 完善原未实现的方法
     */
    private void initTableModel() {
        // 定义表格列名
        String[] columnNames = {"学号", "姓名", "科目", "成绩", "录入时间"};
        // 创建表格模型（不可编辑）
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格单元格默认不可编辑
            }
        };

        // 初始化表格
        dataTable = new JTable(tableModel);
        // 设置表格选择模式：单行选择
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 设置表格自动调整列宽
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // 设置行高
        dataTable.setRowHeight(30);
    }

    /**
     * 打开个人信息新窗口
     * 优化：添加基础组件，完善窗口内容
     */
    private void openPersonalWindow() {
        JFrame personalFrame = new JFrame("个人信息");
        personalFrame.setSize(400, 300);
        personalFrame.setLocationRelativeTo(this);
        personalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        personalFrame.setLayout(new BorderLayout(10, 10));
        personalFrame.setResizable(false);

        // 添加基础信息面板
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        infoPanel.add(new JLabel("用户名："));
        infoPanel.add(new JLabel("admin"));
        infoPanel.add(new JLabel("角色："));
        infoPanel.add(new JLabel("管理员"));
        infoPanel.add(new JLabel("部门："));
        infoPanel.add(new JLabel("教务处"));
        infoPanel.add(new JLabel("联系方式："));
        infoPanel.add(new JLabel("12345678901"));

        personalFrame.add(infoPanel, BorderLayout.CENTER);
        personalFrame.setVisible(true);
    }

    /**
     * 打开反馈提交新窗口
     * 优化：添加反馈输入组件，完善窗口内容
     */
    private void openFeedbackWindow() {
        JDialog feedbackDialog = new JDialog(this, "提交反馈", true);
        feedbackDialog.setSize(600, 400);
        feedbackDialog.setLocationRelativeTo(this);
        feedbackDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        feedbackDialog.setLayout(new BorderLayout(10, 10));
        feedbackDialog.setResizable(false);

        // 反馈标题
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.add(new JLabel("反馈标题："), BorderLayout.WEST);
        JTextField titleField = new JTextField();
        titlePanel.add(titleField, BorderLayout.CENTER);

        // 反馈内容
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(new JLabel("反馈内容："), BorderLayout.NORTH);
        JTextArea contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        contentArea.setRows(10);
        contentPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        // 提交按钮
        JButton submitBtn = new JButton("提交");
        submitBtn.addActionListener(e -> {
            showTip("反馈提交成功！");
            feedbackDialog.dispose();
        });

        // 组装面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(submitBtn, BorderLayout.SOUTH);

        feedbackDialog.add(mainPanel);
        feedbackDialog.setVisible(true);
    }

    /**
     * 通用提示框方法
     * 抽离重复代码，提高复用性
     */
    private void showTip(String message) {
        JOptionPane.showMessageDialog(this, message, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Swing单线程模型：所有UI操作放在事件调度线程中执行
        SwingUtilities.invokeLater(() -> {
            ScoreSystemMainFrame frame = new ScoreSystemMainFrame();
            frame.setVisible(true);
        });
    }
}