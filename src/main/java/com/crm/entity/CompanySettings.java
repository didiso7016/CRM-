package com.crm.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * 公司資料與預設值設定。系統僅維護單一列(id=1)。
 */
@Entity
@Table(name = "company_settings")
public class CompanySettings {

    @Id
    private Long id = 1L; // 固定單列

    @Column(name = "company_name", length = 200)
    private String companyName = "我的公司";

    @Column(name = "tax_id", length = 20)
    private String taxId;

    @Column(length = 300)
    private String address;

    @Column(length = 50)
    private String phone;

    @Column(length = 50)
    private String fax;

    @Column(length = 120)
    private String email;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    /** 公司 Logo 檔案路徑(存於 uploads/) */
    @Column(name = "logo_path", length = 300)
    private String logoPath;

    @Column(name = "default_currency", length = 10)
    private String defaultCurrency = "TWD";

    @Column(name = "default_tax_rate", precision = 6, scale = 2)
    private BigDecimal defaultTaxRate = new BigDecimal("5");

    @Column(name = "default_payment_terms", length = 200)
    private String defaultPaymentTerms;

    @Column(name = "default_delivery_terms", length = 200)
    private String defaultDeliveryTerms;

    /** 客戶關懷提醒天數:超過此天數未聯絡的客戶會出現在首頁提醒 */
    @Column(name = "contact_reminder_days")
    private Integer contactReminderDays = 30;

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    public String getDefaultCurrency() { return defaultCurrency; }
    public void setDefaultCurrency(String defaultCurrency) { this.defaultCurrency = defaultCurrency; }

    public BigDecimal getDefaultTaxRate() { return defaultTaxRate; }
    public void setDefaultTaxRate(BigDecimal defaultTaxRate) { this.defaultTaxRate = defaultTaxRate; }

    public String getDefaultPaymentTerms() { return defaultPaymentTerms; }
    public void setDefaultPaymentTerms(String defaultPaymentTerms) { this.defaultPaymentTerms = defaultPaymentTerms; }

    public String getDefaultDeliveryTerms() { return defaultDeliveryTerms; }
    public void setDefaultDeliveryTerms(String defaultDeliveryTerms) { this.defaultDeliveryTerms = defaultDeliveryTerms; }

    public Integer getContactReminderDays() { return contactReminderDays; }
    public void setContactReminderDays(Integer contactReminderDays) { this.contactReminderDays = contactReminderDays; }
}
