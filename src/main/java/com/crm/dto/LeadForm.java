package com.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 客戶開發(Lead)新增/編輯表單。
 */
public class LeadForm {

    private Long id;

    @NotBlank(message = "公司名稱為必填")
    @Size(max = 200)
    private String companyName;

    private String country;
    private String city;
    private String businessType;
    private String website;

    @Email(message = "Email 格式不正確")
    @Size(max = 120)
    private String email;

    private String phoneZalo;
    private String contactRoute;
    private String sourceUrl;

    private String leadType;

    @Min(value = 0, message = "評分需介於 0–100")
    @Max(value = 100, message = "評分需介於 0–100")
    private Integer score;

    private String priority;
    private String fastDeal;
    private String verification;
    private String brandConflict;

    private String targetProduct;
    private String customAngle;
    private String suggestedSubject;

    private String status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate firstContactDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate latestContactDate;

    private String nextStep;
    private String notes;
    private boolean active = true;

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
}
