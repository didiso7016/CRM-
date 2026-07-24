package com.crm.entity;

import com.crm.enums.ContactType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 聯絡 / 通知紀錄。記錄「哪一天以何種方式通知了哪個客戶」,
 * 建立後會同步更新客戶的最後聯絡時間(供未聯絡提醒)。
 */
@Entity
@Table(name = "contact_logs",
        indexes = {
                @Index(name = "idx_log_customer", columnList = "customer_id"),
                @Index(name = "idx_log_date", columnList = "log_date")
        })
public class ContactLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_log_customer"))
    private Customer customer;

    /** 聯絡日期 */
    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContactType type = ContactType.EMAIL;

    /** 內容摘要,例如「已寄報價單 QT-202607-0001」 */
    @Column(length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }

    public ContactType getType() { return type; }
    public void setType(ContactType type) { this.type = type; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
