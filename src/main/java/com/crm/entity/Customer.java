package com.crm.entity;

import com.crm.enums.CustomerType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 客戶主檔。
 * 不使用實體刪除,改以 active 欄位軟刪除(停用),停用後歷史報價仍可查詢。
 */
@Entity
@Table(name = "customers",
        uniqueConstraints = @UniqueConstraint(name = "uk_customer_code", columnNames = "customer_code"),
        indexes = {
                @Index(name = "idx_customer_company_name", columnList = "company_name"),
                @Index(name = "idx_customer_active", columnList = "active")
        })
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 客戶編號:必填、不可重複 */
    @Column(name = "customer_code", nullable = false, length = 50)
    private String customerCode;

    /** 公司名稱:必填 */
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "tax_id", length = 20)
    private String taxId;

    @Column(length = 50)
    private String phone;

    @Column(length = 50)
    private String fax;

    @Column(length = 120)
    private String email;

    @Column(length = 300)
    private String address;

    /** 國家(許多客戶為外國公司) */
    @Column(length = 60)
    private String country;

    /** 城市 */
    @Column(length = 60)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", length = 20)
    private CustomerType customerType = CustomerType.GENERAL;

    @Column(length = 100)
    private String industry;

    /** 客戶來源 */
    @Column(length = 100)
    private String source;

    @Column(length = 1000)
    private String notes;

    /** 啟用狀態:true 啟用、false 停用(軟刪除) */
    @Column(nullable = false)
    private boolean active = true;

    /** 最後聯絡時間:供「幾天未聯絡」提醒使用,可為空 */
    @Column(name = "last_contacted_at")
    private LocalDateTime lastContactedAt;

    /** 是否納入跟進提醒:預設 false,只有標記「要跟進」的客戶才會出現在通知 */
    // columnDefinition 帶預設值,SQLite 才能對既有資料表順利新增此 NOT NULL 欄位
    @Column(name = "follow_up_enabled", nullable = false, columnDefinition = "integer not null default 0")
    private boolean followUpEnabled = false;

    /** 提醒延後至此日期(含)之前不提醒,可為空 */
    @Column(name = "follow_up_snooze_until")
    private java.time.LocalDate followUpSnoozeUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getLastContactedAt() { return lastContactedAt; }
    public void setLastContactedAt(LocalDateTime lastContactedAt) { this.lastContactedAt = lastContactedAt; }

    public boolean isFollowUpEnabled() { return followUpEnabled; }
    public void setFollowUpEnabled(boolean followUpEnabled) { this.followUpEnabled = followUpEnabled; }

    public java.time.LocalDate getFollowUpSnoozeUntil() { return followUpSnoozeUntil; }
    public void setFollowUpSnoozeUntil(java.time.LocalDate followUpSnoozeUntil) { this.followUpSnoozeUntil = followUpSnoozeUntil; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
