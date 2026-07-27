package com.crm.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 一般待辦事項。可選擇連結某客戶,可設定到期日;完成後打勾。
 */
@Entity
@Table(name = "tasks",
        indexes = {
                @Index(name = "idx_task_done", columnList = "done"),
                @Index(name = "idx_task_due", columnList = "due_date")
        })
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 事項內容:必填 */
    @Column(nullable = false, length = 300)
    private String title;

    /** 到期日(可為空) */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /** 是否完成 */
    @Column(nullable = false, columnDefinition = "integer not null default 0")
    private boolean done = false;

    /** 可選:連結的客戶 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "fk_task_customer"))
    private Customer customer;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
