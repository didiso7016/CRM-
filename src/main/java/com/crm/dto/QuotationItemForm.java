package com.crm.dto;

import java.math.BigDecimal;

/**
 * 報價品項表單列。金額不由前端決定,後端一律重新計算。
 */
public class QuotationItemForm {

    private Long productId;
    private String internalPartNumber;
    private String customerPartNumber;
    private String productName;
    private String specification;
    private String material;
    private String surfaceTreatment;
    private BigDecimal quantity;
    private String unit = "PCS";
    private BigDecimal unitPrice;
    private BigDecimal discountRate = BigDecimal.ZERO;
    private String leadTime;
    private String notes;

    /** 是否為空白列(品名與數量皆未填),用於過濾未填寫的列 */
    public boolean isBlank() {
        boolean noName = productName == null || productName.isBlank();
        boolean noQty = quantity == null || quantity.signum() == 0;
        boolean noPartNo = internalPartNumber == null || internalPartNumber.isBlank();
        return noName && noQty && noPartNo;
    }

    // ===== Getter / Setter =====
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

    public String getLeadTime() { return leadTime; }
    public void setLeadTime(String leadTime) { this.leadTime = leadTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
