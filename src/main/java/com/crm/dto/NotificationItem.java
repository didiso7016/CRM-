package com.crm.dto;

/**
 * 通知中心的一則提醒。
 * kind:CUSTOMER(客戶未聯絡)/ QUOTATION(報價未成交)。
 */
public class NotificationItem {

    private final String kind;
    private final String message;   // 例如:「Hydro System」已經 10 天沒聯絡
    private final String linkUrl;   // 點擊前往的網址
    private final Long customerId;  // 可直接記錄通知的客戶(報價類可為 null)
    private final long days;        // 已間隔天數

    public NotificationItem(String kind, String message, String linkUrl, Long customerId, long days) {
        this.kind = kind;
        this.message = message;
        this.linkUrl = linkUrl;
        this.customerId = customerId;
        this.days = days;
    }

    public String getKind() { return kind; }
    public String getMessage() { return message; }
    public String getLinkUrl() { return linkUrl; }
    public Long getCustomerId() { return customerId; }
    public long getDays() { return days; }
}
