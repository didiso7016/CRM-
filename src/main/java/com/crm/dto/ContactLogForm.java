package com.crm.dto;

import com.crm.enums.ContactType;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 記錄一筆聯絡 / 通知的表單。
 */
public class ContactLogForm {

    @NotNull(message = "請選擇客戶")
    private Long customerId;

    @NotNull(message = "請填聯絡日期")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate logDate = LocalDate.now();

    @NotNull(message = "請選擇聯絡方式")
    private ContactType type = ContactType.EMAIL;

    private String note;

    // ===== Getter / Setter =====
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }

    public ContactType getType() { return type; }
    public void setType(ContactType type) { this.type = type; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
