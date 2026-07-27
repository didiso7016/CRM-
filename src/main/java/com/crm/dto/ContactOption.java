package com.crm.dto;

/**
 * 供報價單表單前端 JS 使用的聯絡人精簡資料(依客戶篩選聯絡人下拉、預設帶主要聯絡人)。
 */
public class ContactOption {
    private final Long id;
    private final String name;
    private final Long customerId;
    private final boolean primary;

    public ContactOption(Long id, String name, Long customerId, boolean primary) {
        this.id = id;
        this.name = name;
        this.customerId = customerId;
        this.primary = primary;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getCustomerId() { return customerId; }
    public boolean isPrimary() { return primary; }
}
