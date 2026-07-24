package com.crm.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * 報價品項。建立時從零件主檔複製一份快照(料號、品名、規格等),
 * 因此日後零件主檔異動不影響既有報價;productId 僅為來源參考,無外鍵約束。
 */
@Entity
@Table(name = "quotation_items",
        indexes = @Index(name = "idx_item_quotation", columnList = "quotation_id"))
public class QuotationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quotation_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_quotation"))
    private Quotation quotation;

    /** 項次 */
    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    /** 來源零件 id(參考用,可為空;刪除品項不影響零件主檔) */
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "internal_part_number", length = 80)
    private String internalPartNumber;

    @Column(name = "customer_part_number", length = 80)
    private String customerPartNumber;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(length = 300)
    private String specification;

    @Column(length = 100)
    private String material;

    @Column(name = "surface_treatment", length = 100)
    private String surfaceTreatment;

    /** 數量(允許小數,如以 KG 計) */
    @Column(precision = 18, scale = 4)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(length = 20)
    private String unit = "PCS";

    @Column(name = "unit_price", precision = 18, scale = 4)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    /** 折扣百分比 0~100 */
    @Column(name = "discount_rate", precision = 6, scale = 2)
    private BigDecimal discountRate = BigDecimal.ZERO;

    /** 品項金額(後端計算) */
    @Column(precision = 18, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "lead_time", length = 60)
    private String leadTime;

    @Column(length = 300)
    private String notes;

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Quotation getQuotation() { return quotation; }
    public void setQuotation(Quotation quotation) { this.quotation = quotation; }

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getInternalPartNumber() { return internalPartNumber; }
    public void setInternalPartNumber(String internalPartNumber) { this.internalPartNumber = internalPartNumber; }

    public String getCustomerPartNumber() { return customerPartNumber; }
    public void setCustomerPartNumber(String customerPartNumber) { this.customerPartNumber = customerPartNumber; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getSurfaceTreatment() { return surfaceTreatment; }
    public void setSurfaceTreatment(String surfaceTreatment) { this.surfaceTreatment = surfaceTreatment; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getLeadTime() { return leadTime; }
    public void setLeadTime(String leadTime) { this.leadTime = leadTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
