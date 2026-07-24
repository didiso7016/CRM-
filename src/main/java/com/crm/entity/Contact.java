package com.crm.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 客戶聯絡人。一間客戶可有多位聯絡人,原則上僅一位為主要聯絡人。
 */
@Entity
@Table(name = "contacts",
        indexes = {
                @Index(name = "idx_contact_customer", columnList = "customer_id"),
                @Index(name = "idx_contact_name", columnList = "name")
        })
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所屬客戶 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_contact_customer"))
    private Customer customer;

    /** 姓名:必填 */
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String department;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(length = 50)
    private String phone;

    @Column(name = "extension_number", length = 20)
    private String extensionNumber;

    @Column(length = 50)
    private String mobile;

    @Column(length = 120)
    private String email;

    /** 是否為主要聯絡人 */
    @Column(name = "primary_contact", nullable = false)
    private boolean primaryContact = false;

    @Column(length = 500)
    private String notes;

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

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
