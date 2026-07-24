package com.crm.enums;

/**
 * 客戶類型。enum 名稱存入資料庫,顯示文字用於畫面。
 */
public enum CustomerType {
    GENERAL("一般客戶"),
    LONG_TERM("長期合作客戶"),
    POTENTIAL("潛在客戶"),
    DEALER("經銷商"),
    SUPPLIER("供應商"),
    OTHER("其他");

    private final String label;

    CustomerType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
