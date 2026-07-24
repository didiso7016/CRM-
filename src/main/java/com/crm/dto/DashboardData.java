package com.crm.dto;

import com.crm.entity.Customer;
import com.crm.entity.Quotation;

import java.util.List;

/**
 * 首頁 Dashboard 統計資料集合。
 */
public class DashboardData {

    private long customerCount;
    private long monthlyQuotationCount;
    private long inProgressCount;
    private long acceptedCount;

    private List<Quotation> recentQuotations;
    private List<Quotation> expiringQuotations;
    private List<Customer> recentlyUpdatedCustomers;

    /** 需要關懷的客戶(超過提醒天數未聯絡) */
    private List<Customer> followUpCustomers;
    private int reminderDays;

    // ===== Getter / Setter =====
    public long getCustomerCount() { return customerCount; }
    public void setCustomerCount(long customerCount) { this.customerCount = customerCount; }

    public long getMonthlyQuotationCount() { return monthlyQuotationCount; }
    public void setMonthlyQuotationCount(long monthlyQuotationCount) { this.monthlyQuotationCount = monthlyQuotationCount; }

    public long getInProgressCount() { return inProgressCount; }
    public void setInProgressCount(long inProgressCount) { this.inProgressCount = inProgressCount; }

    public long getAcceptedCount() { return acceptedCount; }
    public void setAcceptedCount(long acceptedCount) { this.acceptedCount = acceptedCount; }

    public List<Quotation> getRecentQuotations() { return recentQuotations; }
    public void setRecentQuotations(List<Quotation> recentQuotations) { this.recentQuotations = recentQuotations; }

    public List<Quotation> getExpiringQuotations() { return expiringQuotations; }
    public void setExpiringQuotations(List<Quotation> expiringQuotations) { this.expiringQuotations = expiringQuotations; }

    public List<Customer> getRecentlyUpdatedCustomers() { return recentlyUpdatedCustomers; }
    public void setRecentlyUpdatedCustomers(List<Customer> recentlyUpdatedCustomers) { this.recentlyUpdatedCustomers = recentlyUpdatedCustomers; }

    public List<Customer> getFollowUpCustomers() { return followUpCustomers; }
    public void setFollowUpCustomers(List<Customer> followUpCustomers) { this.followUpCustomers = followUpCustomers; }

    public int getReminderDays() { return reminderDays; }
    public void setReminderDays(int reminderDays) { this.reminderDays = reminderDays; }
}
