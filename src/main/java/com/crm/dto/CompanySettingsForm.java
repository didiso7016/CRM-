package com.crm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * 公司設定表單。
 */
public class CompanySettingsForm {

    @NotBlank(message = "公司名稱為必填")
    private String companyName;

    private String taxId;
    private String address;
    private String phone;
    private String fax;

    @Email(message = "Email 格式不正確")
    private String email;

    private String contactName;
    private String defaultCurrency = "USD";

    @DecimalMin(value = "0", message = "預設稅率不得小於 0")
    private BigDecimal defaultTaxRate = new BigDecimal("5");

    private String defaultPaymentTerms;
    private String defaultDeliveryTerms;

    @Min(value = 1, message = "提醒天數至少為 1")
    private Integer contactReminderDays = 30;

    @Min(value = 1, message = "報價跟進天數至少為 1")
    private Integer quotationFollowupDays = 14;

    // ===== Getter / Setter =====
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

    public Integer getQuotationFollowupDays() { return quotationFollowupDays; }
    public void setQuotationFollowupDays(Integer quotationFollowupDays) { this.quotationFollowupDays = quotationFollowupDays; }
}
