package com.crm.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 零件品項主檔。常用零件可直接帶入報價單;報價品項會複製一份快照,
 * 因此日後刪除或修改零件主檔不影響既有報價。
 */
@Entity
@Table(name = "products",
        uniqueConstraints = @UniqueConstraint(name = "uk_internal_part_number", columnNames = "internal_part_number"),
        indexes = {
                @Index(name = "idx_product_name", columnList = "name"),
                @Index(name = "idx_product_active", columnList = "active")
        })
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 內部料號:必填、不可重複 */
    @Column(name = "internal_part_number", nullable = false, length = 80)
    private String internalPartNumber;

    @Column(name = "customer_part_number", length = 80)
    private String customerPartNumber;

    /** 零件名稱:必填 */
    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 300)
    private String specification;

    @Column(length = 100)
    private String material;

    @Column(name = "surface_treatment", length = 100)
    private String surfaceTreatment;

    /** 單位(字串,預設 PCS) */
    @Column(length = 20)
    private String unit = "PCS";

    /** 預設單價 */
    @Column(name = "default_unit_price", precision = 18, scale = 4)
    private BigDecimal defaultUnitPrice = BigDecimal.ZERO;

    /** 最低訂購量 */
    @Column
    private Integer moq;

    /** 預設交期天數 */
    @Column(name = "default_lead_time_days")
    private Integer defaultLeadTimeDays;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private boolean active = true;

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

    public String getInternalPartNumber() { return internalPartNumber; }
    public void setInternalPartNumber(String internalPartNumber) { this.internalPartNumber = internalPartNumber; }

    public String getCustomerPartNumber() { return customerPartNumber; }
    public void setCustomerPartNumber(String customerPartNumber) { this.customerPartNumber = customerPartNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getSurfaceTreatment() { return surfaceTreatment; }
    public void setSurfaceTreatment(String surfaceTreatment) { this.surfaceTreatment = surfaceTreatment; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getDefaultUnitPrice() { return defaultUnitPrice; }
    public void setDefaultUnitPrice(BigDecimal defaultUnitPrice) { this.defaultUnitPrice = defaultUnitPrice; }

    public Integer getMoq() { return moq; }
    public void setMoq(Integer moq) { this.moq = moq; }

    public Integer getDefaultLeadTimeDays() { return defaultLeadTimeDays; }
    public void setDefaultLeadTimeDays(Integer defaultLeadTimeDays) { this.defaultLeadTimeDays = defaultLeadTimeDays; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
