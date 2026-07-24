package com.crm.dto;

import java.math.BigDecimal;

/**
 * 供報價單表單前端 JS 使用的零件精簡資料(選擇零件時帶入品項欄位)。
 */
public class ProductOption {
    private final Long id;
    private final String internalPartNumber;
    private final String customerPartNumber;
    private final String name;
    private final String specification;
    private final String material;
    private final String surfaceTreatment;
    private final String unit;
    private final BigDecimal unitPrice;
    private final String leadTime;

    public ProductOption(Long id, String internalPartNumber, String customerPartNumber, String name,
                         String specification, String material, String surfaceTreatment,
                         String unit, BigDecimal unitPrice, String leadTime) {
        this.id = id;
        this.internalPartNumber = internalPartNumber;
        this.customerPartNumber = customerPartNumber;
        this.name = name;
        this.specification = specification;
        this.material = material;
        this.surfaceTreatment = surfaceTreatment;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.leadTime = leadTime;
    }

    public Long getId() { return id; }
    public String getInternalPartNumber() { return internalPartNumber; }
    public String getCustomerPartNumber() { return customerPartNumber; }
    public String getName() { return name; }
    public String getSpecification() { return specification; }
    public String getMaterial() { return material; }
    public String getSurfaceTreatment() { return surfaceTreatment; }
    public String getUnit() { return unit; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getLeadTime() { return leadTime; }
}
