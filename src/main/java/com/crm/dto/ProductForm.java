package com.crm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 產品新增/編輯表單。
 */
public class ProductForm {

    private Long id;

    @NotBlank(message = "內部料號為必填")
    @Size(max = 80)
    private String internalPartNumber;

    @Size(max = 80)
    private String customerPartNumber;

    @NotBlank(message = "產品名稱為必填")
    @Size(max = 200)
    private String name;

    private String specification;
    private String material;
    private String surfaceTreatment;
    private String unit = "PCS";

    @DecimalMin(value = "0", message = "預設單價不得小於 0")
    private BigDecimal defaultUnitPrice = BigDecimal.ZERO;

    @Min(value = 0, message = "MOQ 不得小於 0")
    private Integer moq;

    @Min(value = 0, message = "交期天數不得小於 0")
    private Integer defaultLeadTimeDays;

    private String notes;
    private boolean active = true;

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
}
