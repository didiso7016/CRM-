package com.crm.enums;

/**
 * 報價狀態。label 用於顯示,badgeClass 用於畫面標籤樣式(Bootstrap)。
 */
public enum QuotationStatus {
    DRAFT("草稿", "text-bg-secondary"),
    SENT("已送出", "text-bg-primary"),
    CONFIRMING("客戶確認中", "text-bg-info"),
    ACCEPTED("已接受", "text-bg-success"),
    REJECTED("已拒絕", "text-bg-danger"),
    EXPIRED("已失效", "text-bg-dark"),
    CANCELLED("已取消", "text-bg-light border");

    private final String label;
    private final String badgeClass;

    QuotationStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() { return label; }
    public String getBadgeClass() { return badgeClass; }
}
