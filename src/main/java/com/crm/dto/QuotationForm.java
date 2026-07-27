package com.crm.dto;

import com.crm.enums.QuotationStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 報價單新增/編輯表單。
 */
public class QuotationForm {

    private Long id;

    @NotNull(message = "請選擇客戶")
    private Long customerId;

    private Long contactId;

    @NotNull(message = "報價日期為必填")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate quotationDate = LocalDate.now();

    private String customerInquiryNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validUntil;

    private String currency = "USD";
    private String taxType = "應稅";
    private BigDecimal taxRate = new BigDecimal("5");
    private String paymentTerms;
    private String deliveryTerms;
    private String estimatedDelivery;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate customerReplyDueDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deliveryDueDate;

    private BigDecimal overallDiscount = BigDecimal.ZERO;
    private BigDecimal freight = BigDecimal.ZERO;
    private BigDecimal otherFee = BigDecimal.ZERO;

    private String quotationNotes;
    private String internalNotes;

    /** 品項清單 */
    private List<QuotationItemForm> items = new ArrayList<>();

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getContactId() { return contactId; }
    public void setContactId(Long contactId) { this.contactId = contactId; }

    public LocalDate getQuotationDate() { return quotationDate; }
    public void setQuotationDate(LocalDate quotationDate) { this.quotationDate = quotationDate; }

    public String getCustomerInquiryNumber() { return customerInquiryNumber; }
    public void setCustomerInquiryNumber(String customerInquiryNumber) { this.customerInquiryNumber = customerInquiryNumber; }

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

    public LocalDate getCustomerReplyDueDate() { return customerReplyDueDate; }
    public void setCustomerReplyDueDate(LocalDate customerReplyDueDate) { this.customerReplyDueDate = customerReplyDueDate; }

    public LocalDate getDeliveryDueDate() { return deliveryDueDate; }
    public void setDeliveryDueDate(LocalDate deliveryDueDate) { this.deliveryDueDate = deliveryDueDate; }

    public BigDecimal getOverallDiscount() { return overallDiscount; }
    public void setOverallDiscount(BigDecimal overallDiscount) { this.overallDiscount = overallDiscount; }

    public BigDecimal getFreight() { return freight; }
    public void setFreight(BigDecimal freight) { this.freight = freight; }

    public BigDecimal getOtherFee() { return otherFee; }
    public void setOtherFee(BigDecimal otherFee) { this.otherFee = otherFee; }

    public String getQuotationNotes() { return quotationNotes; }
    public void setQuotationNotes(String quotationNotes) { this.quotationNotes = quotationNotes; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }

    public List<QuotationItemForm> getItems() { return items; }
    public void setItems(List<QuotationItemForm> items) { this.items = items; }
}
