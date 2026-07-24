package com.crm.enums;

/**
 * 通知 / 聯絡方式。
 */
public enum ContactType {
    EMAIL("發信 Email"),
    PHONE("電話"),
    MEETING("拜訪 / 會議"),
    QUOTE("寄報價單"),
    OTHER("其他");

    private final String label;

    ContactType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
