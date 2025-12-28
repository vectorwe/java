package com.score.view;

import java.util.UUID;

/**
 * 反馈核心处理类（数据验证、编号生成）
 */
public class TeacherFeedbackSystem {
    // ========== 新增：反馈一级分类（对应图片中的选项） ==========
    public enum FeedbackCategory {
        SUPPLEMENT("进行补充"),
        REPORT_ISSUE("报告问题"),
        SUGGESTION("提出建议");

        private final String categoryName;

        FeedbackCategory(String categoryName) {
            this.categoryName = categoryName;
        }

        @Override
        public String toString() {
            return categoryName;
        }

        public String getCategoryName() {
            return categoryName;
        }
    }

    // 反馈二级类型（原类型，关联一级分类）
    public enum FeedbackType {
        // 关联“报告问题”分类
        SYSTEM_ERROR("系统功能问题", FeedbackCategory.REPORT_ISSUE),
        DATA_ABNORMAL("数据异常反馈", FeedbackCategory.REPORT_ISSUE),
        // 关联“提出建议”分类
        FUNCTION_SUGGESTION("功能优化建议", FeedbackCategory.SUGGESTION),
        // 关联“进行补充”分类
        OPERATION_CONSULT("操作使用咨询", FeedbackCategory.SUPPLEMENT),
        // 兜底选项
        OTHER("其他", null);

        private final String typeName;
        private final FeedbackCategory category; // 关联的一级分类

        FeedbackType(String typeName, FeedbackCategory category) {
            this.typeName = typeName;
            this.category = category;
        }

        @Override
        public String toString() {
            return typeName;
        }

        public String getTypeName() {
            return typeName;
        }

        public FeedbackCategory getCategory() {
            return category;
        }
    }

    // 紧急程度枚举（保持不变）
    public enum UrgencyLevel {
        NORMAL("普通"),
        URGENT("紧急"),
        VERY_URGENT("非常紧急");

        private final String levelName;

        UrgencyLevel(String levelName) {
            this.levelName = levelName;
        }

        @Override
        public String toString() {
            return levelName;
        }

        public String getLevelName() {
            return levelName;
        }
    }

    // 反馈实体类（新增一级分类字段）
    public static class Feedback {
        private String feedbackId;
        private FeedbackCategory category; // 新增：一级分类
        private FeedbackType type;          // 二级类型
        private String title;
        private String detail;
        private String attachments;
        private UrgencyLevel urgency;
        private String contact;

        // 生成提交成功提示文本（新增一级分类展示）
        public String getSubmitResult() {
            return "========== 反馈提交成功 ==========\n" +
                    "反馈编号：" + feedbackId + "\n" +
                    "一级分类：" + category.getCategoryName() + "\n" +
                    "反馈类型：" + type.getTypeName() + "\n" +
                    "标题：" + title + "\n" +
                    "详细描述：" + detail + "\n" +
                    "附件：" + (attachments.isEmpty() ? "无" : attachments) + "\n" +
                    "紧急程度：" + urgency.getLevelName() + "\n" +
                    "联系方式：" + (contact.isEmpty() ? "未填写" : contact) + "\n" +
                    "==================================\n" +
                    "预计1-3个工作日处理，请耐心等待。";
        }

        // Getter & Setter（新增category的方法）
        public String getFeedbackId() { return feedbackId; }
        public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
        public FeedbackCategory getCategory() { return category; }
        public void setCategory(FeedbackCategory category) { this.category = category; }
        public FeedbackType getType() { return type; }
        public void setType(FeedbackType type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDetail() { return detail; }
        public void setDetail(String detail) { this.detail = detail; }
        public String getAttachments() { return attachments; }
        public void setAttachments(String attachments) { this.attachments = attachments; }
        public UrgencyLevel getUrgency() { return urgency; }
        public void setUrgency(UrgencyLevel urgency) { this.urgency = urgency; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
    }

    /**
     * 生成唯一反馈编号（保持不变）
     */
    public static String generateFeedbackId() {
        return "FB" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 验证反馈必填项（新增一级分类验证）
     */
    public static String validateFeedback(Feedback feedback) {
        if (feedback.getCategory() == null) {
            return "请选择反馈一级分类！";
        }
        if (feedback.getType() == null) {
            return "请选择反馈类型！";
        }
        if (feedback.getTitle() == null || feedback.getTitle().trim().isEmpty()) {
            return "反馈标题不能为空！";
        }
        if (feedback.getDetail() == null || feedback.getDetail().trim().isEmpty()) {
            return "详细描述不能为空！";
        }
        if (feedback.getUrgency() == null) {
            return "请选择紧急程度！";
        }
        return "success";
    }
}