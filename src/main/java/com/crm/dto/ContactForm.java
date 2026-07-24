package com.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 聯絡人新增/編輯表單。
 */
public class ContactForm {

    private Long id;

    /** 所屬客戶 id */
    @NotNull(message = "缺少所屬客戶")
    private Long customerId;

    @NotBlank(message = "聯絡人姓名為必填")
    @Size(max = 100)
    private String name;

    private String department;
    private String jobTitle;
    private String phone;
    private String extensionNumber;
    private String mobile;

    @Email(message = "Email 格式不正確")
    @Size(max = 120)
    private String email;

    private boolean primaryContact = false;
    private String notes;

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getExtensionNumber() { return extensionNumber; }
    public void setExtensionNumber(String extensionNumber) { this.extensionNumber = extensionNumber; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isPrimaryContact() { return primaryContact; }
    public void setPrimaryContact(boolean primaryContact) { this.primaryContact = primaryContact; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
