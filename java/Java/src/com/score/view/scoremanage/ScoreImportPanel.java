package com.score.view.scoremanage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 成绩单个导入面板（添加列直接输入列名，默认表头为“列名X”）
 */
public class ScoreImportPanel extends JPanel {
    private DefaultTableModel importTableModel;
    private JTable importTable;
    private CardLayout parentCardLayout;
    private JPanel parentCardPanel;
    private List<String> columnNames; // 存储自定义列名

    public ScoreImportPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.parentCardLayout = cardLayout;
        this.parentCardPanel = cardPanel;
        this.columnNames = new ArrayList<>();
        initPanel();
    }

    private void initPanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部功能按钮面板
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addColBtn = new JButton("添加列");
        JButton editHeaderBtn = new JButton("修改表头");
        JButton delColBtn = new JButton("删除列");
        JButton addRowBtn = new JButton("添加数据行");
        JButton delRowBtn = new JButton("删除选中行");
        JButton backBtn = new JButton("返回上一级");

        // 按钮事件绑定
        addColBtn.addActionListener(e -> addTableColumn());
        editHeaderBtn.addActionListener(e -> editSelectedHeader());
        delColBtn.addActionListener(e -> deleteSelectedColumn());
        addRowBtn.addActionListener(e -> addDataRow());
        delRowBtn.addActionListener(e -> deleteSelectedRow());
        backBtn.addActionListener(e -> backToDefaultPanel());

        btnPanel.add(addColBtn);
        btnPanel.add(editHeaderBtn);
        btnPanel.add(delColBtn);
        btnPanel.add(addRowBtn);
        btnPanel.add(delRowBtn);
        btnPanel.add(backBtn);
        this.add(btnPanel, BorderLayout.NORTH);

        // 初始化表格
        initTable();
        JScrollPane scrollPane = new JScrollPane(importTable);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void initTable() {
        // 初始模型：0列，1行（列名配置行）
        importTableModel = new DefaultTableModel(1, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Object.class;
            }

            // 表头显示逻辑：直接显示自定义列名，默认名称改为“列名X”
            @Override
            public String getColumnName(int column) {
                if (column < columnNames.size() && !columnNames.get(column).trim().isEmpty()) {
                    return columnNames.get(column).trim();
                }
                // 核心修改：默认显示“列名X”而非“未命名列X”
                return "列名" + (column + 1);
            }
        };

        // 表格配置
        importTable = new JTable(importTableModel);
        importTable.setRowHeight(0, 35);
        importTable.setRowHeight(30);
        importTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        importTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        importTable.getTableHeader().setReorderingAllowed(false);

        // 表头点击事件（仍保留手动修改表头功能）
        JTableHeader header = importTable.getTableHeader();
        header.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = importTable.columnAtPoint(e.getPoint());
                if (col != -1) {
                    editHeaderByColumn(col);
                }
            }
        });

        // 监听列名配置行编辑（同步更新列名）
        importTableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row == 0 && col != -1) {
                Object value = importTableModel.getValueAt(row, col);
                String newName = (value == null) ? "" : value.toString().trim();
                while (columnNames.size() <= col) {
                    columnNames.add("");
                }
                columnNames.set(col, newName);
                importTable.getTableHeader().repaint();
            }
        });
    }

    // 修改选中列的表头
    private void editSelectedHeader() {
        int selectedCol = importTable.getSelectedColumn();
        if (selectedCol == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要修改的列！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        editHeaderByColumn(selectedCol);
    }

    // 编辑指定列的表头
    private void editHeaderByColumn(int col) {
        importTable.editCellAt(0, col);
        TableCellEditor editor = importTable.getCellEditor();
        if (editor != null) {
            Component editorComponent = editor.getTableCellEditorComponent(
                    importTable, importTable.getValueAt(0, col), true, 0, col);
            editorComponent.requestFocus();
        }
    }

    // 删除选中列
    private void deleteSelectedColumn() {
        int selectedCol = importTable.getSelectedColumn();
        if (selectedCol == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的列！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 二次确认
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除选中的列吗？删除后该列数据也会被清除！",
                "删除确认",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            // 获取选中的列对象
            TableColumn column = importTable.getColumnModel().getColumn(selectedCol);
            // 通过JTable删除列
            importTable.removeColumn(column);
            // 同步更新列名列表
            if (selectedCol < columnNames.size()) {
                columnNames.remove(selectedCol);
            }
            // 刷新表格
            importTable.doLayout();
        }
    }

    // 添加列（核心修改：弹出输入框提示“输入列名”，默认名称为“列名X”）
    private void addTableColumn() {
        // 弹出输入框，提示文字改为“输入列名”
        String columnName = JOptionPane.showInputDialog(
                this,
                "输入列名：", // 核心修改：提示文字简化为“输入列名”
                "添加列",
                JOptionPane.PLAIN_MESSAGE
        );

        // 处理用户取消输入或输入为空的情况
        if (columnName == null) { // 用户点击取消
            return;
        }
        columnName = columnName.trim();
        if (columnName.isEmpty()) { // 输入为空，使用默认名称“列名X”
            columnName = "列名" + (columnNames.size() + 1);
        }

        // 添加列并设置列名
        int newColIndex = importTableModel.getColumnCount();
        importTableModel.addColumn(columnName); // 列名直接显示在表头
        importTableModel.setValueAt(columnName, 0, newColIndex); // 列名配置行同步显示
        columnNames.add(columnName); // 存入列名列表

        // 刷新表格
        importTable.doLayout();
        JOptionPane.showMessageDialog(this,
                "已添加列：" + columnName,
                "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    // 添加数据行
    private void addDataRow() {
        int colCount = importTableModel.getColumnCount();
        if (colCount == 0) {
            JOptionPane.showMessageDialog(this, "请先添加列后再添加数据行！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object[] emptyRow = new Object[colCount];
        for (int i = 0; i < colCount; i++) {
            emptyRow[i] = "";
        }
        importTableModel.addRow(emptyRow);
        importTable.scrollRectToVisible(importTable.getCellRect(importTableModel.getRowCount()-1, 0, true));
    }

    // 删除选中行
    private void deleteSelectedRow() {
        int selectedRow = importTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的行！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedRow == 0) {
            JOptionPane.showMessageDialog(this, "列名配置行不可删除！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        importTableModel.removeRow(selectedRow);
    }

    // 返回上一级
    private void backToDefaultPanel() {
        parentCardLayout.show(parentCardPanel, "default");
    }
}