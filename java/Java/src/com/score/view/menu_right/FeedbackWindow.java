package com.score.view.menu_right;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 反馈窗口工具类（存放openFeedbackWindow方法）
 * 属于menu_right包
 */
public class FeedbackWindow {
    /**
     * 打开反馈提交窗口
     * @param parent 父窗口（用于窗口居中、模态关联）
     */
    public static void openFeedbackWindow(JFrame parent) {
        // 创建模态对话框（依赖父窗口）
        JDialog feedbackDialog = new JDialog(parent, "提交反馈", true);
        feedbackDialog.setSize(600, 400);
        feedbackDialog.setLocationRelativeTo(parent); // 相对于父窗口居中
        feedbackDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        feedbackDialog.setLayout(new BorderLayout(10, 10));
        feedbackDialog.setResizable(false);

        // 1. 反馈标题面板
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.add(new JLabel("反馈标题："), BorderLayout.WEST);
        JTextField titleField = new JTextField();
        titlePanel.add(titleField, BorderLayout.CENTER);

        // 2. 反馈内容面板
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(new JLabel("反馈内容："), BorderLayout.NORTH);
        JTextArea contentArea = new JTextArea();
        contentArea.setLineWrap(true); // 自动换行
        contentArea.setRows(10); // 默认10行高度
        contentPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        // 3. 提交按钮
        JButton submitBtn = new JButton("提交");
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(feedbackDialog, "反馈提交成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                feedbackDialog.dispose(); // 关闭窗口
            }
        });

        // 组装主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(submitBtn, BorderLayout.SOUTH);

        feedbackDialog.add(mainPanel);
        feedbackDialog.setVisible(true);
    }
}