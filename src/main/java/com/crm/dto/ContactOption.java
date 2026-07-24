package com.crm.dto;

/**
 * 供報價單表單前端 JS 使用的聯絡人精簡資料(依客戶篩選聯絡人下拉)。
 */
public class ContactOption {
    private final Long id;
    private final String name;
    private final Long customerId;

    public ContactOption(Long id, String name, Long customerId) {
        this.id = id;
        this.name = name;
        this.customerId = customerId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getCustomerId() { return customerId; }
}
