package com.crm.entity;

import com.crm.enums.QuotationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 報價單主檔。
 * 報價單號 + 版本 為唯一;建立新版時沿用同一單號、版本加一,保留原始版本。
 */
@Entity
@Table(name = "quotations",
        uniqueConstraints = @UniqueConstraint(name = "uk_quotation_number_version",
                columnNames = {"quotation_number", "version"}),
        indexes = {
                @Index(name = "idx_quotation_customer", columnList = "customer_id"),
                @Index(name = "idx_quotation_number", columnList = "quotation_number"),
                @Index(name = "idx_quotation_status", columnList = "status"),
                @Index(name = "idx_quotation_date", columnList = "quotation_date")
        })
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 報價單號,格式 QT-YYYYMM-0001 */
    @Column(name = "quotation_number", nullable = false, length = 30)
    private String quotationNumber;

    /** 報價版本,自 1 起 */
    @Column(nullable = false)
    private Integer version = 1;

    @Column(name = "quotation_date", nullable = false)
    private LocalDate quotationDate;

    /** 所屬客戶(停用後仍可查詢此報價) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_quotation_customer"))
    private Customer customer;

    /** 聯絡人(可為空,但若有必須屬於同一客戶) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id",
            foreignKey = @ForeignKey(name = "fk_quotation_contact"))
    private Contact contact;

    @Column(name = "customer_inquiry_number", length = 60)
    private String customerInquiryNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuotationStatus status = QuotationStatus.DRAFT;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(length = 10)
    private String currency = "TWD";

    @Column(name = "tax_type", length = 20)
    private String taxType = "應稅";

    /** 稅率(百分比,如 5 代表 5%) */
    @Column(name = "tax_rate", precision = 6, scale = 2)
    private BigDecimal taxRate = new BigDecimal("5");

    @Column(name = "payment_terms", length = 200)
    private String paymentTerms;

    @Column(name = "delivery_terms", length = 200)
    private String deliveryTerms;

    @Column(name = "estimated_delivery", length = 100)
    private String estimatedDelivery;

    // ===== 金額欄位:一律 BigDecimal,由 PricingService 於後端重新計算 =====
    @Column(precision = 18, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "overall_discount", precision = 18, scale = 2)
    private BigDecimal overallDiscount = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    private BigDecimal freight = BigDecimal.ZERO;

    @Column(name = "other_fee", precision = 18, scale = 2)
    private BigDecimal otherFee = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 18, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "quotation_notes", length = 1000)
    private String quotationNotes;

    @Column(name = "internal_notes", length = 1000)
    private String internalNotes;

    /** 品項:隨報價單一併儲存與刪除,不影響零件主檔 */
    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNumber asc")
    private List<QuotationItem> items = new ArrayList<>();

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

    /** 加入品項並維護雙向關聯 */
    public void addItem(QuotationItem item) {
        item.setQuotation(this);
        this.items.add(item);
    }

    public void clearItems() {
        this.items.clear();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuotationNumber() { return quotationNumber; }
    public void setQuotationNumber(String quotationNumber) { this.quotationNumber = quotationNumber; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public LocalDate getQuotationDate() { return quotationDate; }
    public void setQuotationDate(LocalDate quotationDate) { this.quotationDate = quotationDate; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Contact getContact() { return contact; }
    public void setContact(Contact contact) { this.contact = contact; }

    public String getCustomerInquiryNumber() { return customerInquiryNumber; }
    public void setCustomerInquiryNumber(String customerInquiryNumber) { this.customerInquiryNumber = customerInquiryNumber; }

    public QuotationStatus getStatus() { return status; }
    public void setStatus(QuotationStatus status) { this.status = status; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getTaxType() { return taxType; }
    public void setTaxType(String taxType) { this.taxType = taxType; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getDeliveryTerms() { return deliveryTerms; }
    public void setDeliveryTerms(String deliveryTerms) { this.deliveryTerms = deliveryTerms; }

    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getOverallDiscount() { return overallDiscount; }
    public void setOverallDiscount(BigDecimal overallDiscount) { this.overallDiscount = overallDiscount; }

    public BigDecimal getFreight() { return freight; }
    public void setFreight(BigDecimal freight) { this.freight = freight; }

    public BigDecimal getOtherFee() { return otherFee; }
    public void setOtherFee(BigDecimal otherFee) { this.otherFee = otherFee; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getQuotationNotes() { return quotationNotes; }
    public void setQuotationNotes(String quotationNotes) { this.quotationNotes = quotationNotes; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }

    public List<QuotationItem> getItems() { return items; }
    public void setItems(List<QuotationItem> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
