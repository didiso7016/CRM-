package com.crm.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客戶開發(潛在客戶)。成交前的開發追蹤,成交後可「轉為正式客戶」。
 * 對應越南經銷商開發表。
 */
@Entity
@Table(name = "leads",
        indexes = {
                @Index(name = "idx_lead_company", columnList = "company_name"),
                @Index(name = "idx_lead_status", columnList = "status")
        })
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== 基本資料 =====
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(length = 60)
    private String country;

    @Column(length = 60)
    private String city;

    /** 商業型態(自由打字) */
    @Column(name = "business_type", length = 100)
    private String businessType;

    @Column(length = 200)
    private String website;

    @Column(length = 120)
    private String email;

    /** 電話 / Zalo */
    @Column(name = "phone_zalo", length = 100)
    private String phoneZalo;

    /** 聯絡方式(自由打字) */
    @Column(name = "contact_route", length = 200)
    private String contactRoute;

    /** 來源 URL(自由打字) */
    @Column(name = "source_url", length = 300)
    private String sourceUrl;

    // ===== 評分與分級 =====
    /** 客戶類型(下拉) */
    @Column(name = "lead_type", length = 60)
    private String leadType;

    /** 評分 0–100(手動填) */
    @Column
    private Integer score;

    /** 優先級 A/B/C */
    @Column(length = 5)
    private String priority;

    /** 快速成交 高/中/低 */
    @Column(name = "fast_deal", length = 10)
    private String fastDeal;

    /** 查核程度(自由打字) */
    @Column(length = 100)
    private String verification;

    /** 品牌衝突(下拉) */
    @Column(name = "brand_conflict", length = 200)
    private String brandConflict;

    // ===== 開發策略 =====
    /** 切入產品(下拉) */
    @Column(name = "target_product", length = 100)
    private String targetProduct;

    /** 客製化切入點(下拉) */
    @Column(name = "custom_angle", length = 300)
    private String customAngle;

    /** 建議主旨(寄信用) */
    @Column(name = "suggested_subject", length = 300)
    private String suggestedSubject;

    // ===== 開發進度 =====
    /** 開發進度(下拉):已寄信 / 暫緩 / 不適合 */
    @Column(length = 20)
    private String status;

    @Column(name = "first_contact_date")
    private LocalDate firstContactDate;

    @Column(name = "latest_contact_date")
    private LocalDate latestContactDate;

    /** 下一步(下拉) */
    @Column(name = "next_step", length = 200)
    private String nextStep;

    @Column(length = 1000)
    private String notes;

    // ===== 狀態 =====
    @Column(nullable = false)
    private boolean active = true;

    /** 轉為正式客戶後,對應的客戶 id(非空代表已轉入) */
    @Column(name = "converted_customer_id")
    private Long convertedCustomerId;

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

    public boolean isConverted() {
        return convertedCustomerId != null;
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneZalo() { return phoneZalo; }
    public void setPhoneZalo(String phoneZalo) { this.phoneZalo = phoneZalo; }

    public String getContactRoute() { return contactRoute; }
    public void setContactRoute(String contactRoute) { this.contactRoute = contactRoute; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getLeadType() { return leadType; }
    public void setLeadType(String leadType) { this.leadType = leadType; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getFastDeal() { return fastDeal; }
    public void setFastDeal(String fastDeal) { this.fastDeal = fastDeal; }

    public String getVerification() { return verification; }
    public void setVerification(String verification) { this.verification = verification; }

    public String getBrandConflict() { return brandConflict; }
    public void setBrandConflict(String brandConflict) { this.brandConflict = brandConflict; }

    public String getTargetProduct() { return targetProduct; }
    public void setTargetProduct(String targetProduct) { this.targetProduct = targetProduct; }

    public String getCustomAngle() { return customAngle; }
    public void setCustomAngle(String customAngle) { this.customAngle = customAngle; }

    public String getSuggestedSubject() { return suggestedSubject; }
    public void setSuggestedSubject(String suggestedSubject) { this.suggestedSubject = suggestedSubject; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getFirstContactDate() { return firstContactDate; }
    public void setFirstContactDate(LocalDate firstContactDate) { this.firstContactDate = firstContactDate; }

    public LocalDate getLatestContactDate() { return latestContactDate; }
    public void setLatestContactDate(LocalDate latestContactDate) { this.latestContactDate = latestContactDate; }

    public String getNextStep() { return nextStep; }
    public void setNextStep(String nextStep) { this.nextStep = nextStep; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Long getConvertedCustomerId() { return convertedCustomerId; }
    public void setConvertedCustomerId(Long convertedCustomerId) { this.convertedCustomerId = convertedCustomerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
