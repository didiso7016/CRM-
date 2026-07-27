package com.crm.enums;

/**
 * 報價狀態。label 顯示文字;icon 為 Bootstrap Icons 類別;
 * 徽章以「顏色 + 圖示 + 文字」三重標示,色盲也能辨識。
 */
public enum QuotationStatus {
    DRAFT("草稿", "pencil"),
    SENT("已送出", "send"),
    CONFIRMING("客戶確認中", "hourglass"),
    ACCEPTED("已收訂", "circle-check"),
    REJECTED("已拒絕", "circle-x"),
    EXPIRED("已失效", "circle-slash"),
    CANCELLED("已取消", "circle-minus");

    private final String label;
    private final String icon;

    QuotationStatus(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() { return label; }
    public String getIcon() { return icon; }
}
