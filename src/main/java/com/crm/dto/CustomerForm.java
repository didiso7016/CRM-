package com.crm.dto;

import com.crm.enums.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 客戶新增/編輯表單。後端以此接收並驗證,不直接綁定 Entity。
 */
public class CustomerForm {

    private Long id;

    // 客戶編號由系統自動產生(CUS-YYYY-NNN),不由使用者輸入,故不做必填驗證
    @Size(max = 50, message = "客戶編號不可超過 50 字")
    private String customerCode;

    @NotBlank(message = "公司名稱為必填")
    @Size(max = 200, message = "公司名稱不可超過 200 字")
    private String companyName;

    @Size(max = 20)
    private String taxId;

    private String phone;
    private String fax;

    @Email(message = "Email 格式不正確")
    @Size(max = 120)
    private String email;

    private String address;
    private String country;
    private String city;
    private CustomerType customerType = CustomerType.GENERAL;
    private String industry;
    private String source;
    /** 交易預設值(建報價時可帶入) */
    private String defaultCurrency;
    private String defaultPaymentTerms;
    private String defaultDeliveryTerms;
    private String notes;
    private boolean active = true;
    /** 納入跟進提醒(預設不提醒) */
    private boolean followUpEnabled = false;

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public CustomerType getCustomerType() { return customerType; }
    public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDefaultCurrency() { return defaultCurrency; }
    public void setDefaultCurrency(String defaultCurrency) { this.defaultCurrency = defaultCurrency; }

    public String getDefaultPaymentTerms() { return defaultPaymentTerms; }
    public void setDefaultPaymentTerms(String defaultPaymentTerms) { this.defaultPaymentTerms = defaultPaymentTerms; }

    public String getDefaultDeliveryTerms() { return defaultDeliveryTerms; }
    public void setDefaultDeliveryTerms(String defaultDeliveryTerms) { this.defaultDeliveryTerms = defaultDeliveryTerms; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isFollowUpEnabled() { return followUpEnabled; }
    public void setFollowUpEnabled(boolean followUpEnabled) { this.followUpEnabled = followUpEnabled; }
}
