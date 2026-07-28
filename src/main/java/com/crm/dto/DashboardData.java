package com.crm.dto;

import com.crm.entity.Customer;
import com.crm.entity.Quotation;
import com.crm.entity.Task;

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
    private List<Quotation> expiringQuotations;   // 即將到期(有效期限 N 天內)
    private List<Quotation> expiredQuotations;    // 已過期
    private List<Quotation> awaitingReplyQuotations; // 送出後尚未回覆
    private List<Quotation> draftQuotations;      // 尚未完成(草稿)
    private List<Customer> recentlyUpdatedCustomers;

    /** 需要關懷的客戶(超過提醒天數未聯絡) */
    private List<Customer> followUpCustomers;
    private int reminderDays;

    /** 成交金額(基準=付清尾款):近 6 個月每月 + 本年度合計 */
    private List<ChartBar> monthlyRevenue;
    private java.math.BigDecimal yearlyRevenue;

    /** 收訂中的公司(已收訂、尚未付清尾款) */
    private List<Quotation> depositQuotations;

    /** 待辦 */
    private List<Task> upcomingTasks;
    private long overdueTaskCount;

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

    public List<Quotation> getExpiredQuotations() { return expiredQuotations; }
    public void setExpiredQuotations(List<Quotation> expiredQuotations) { this.expiredQuotations = expiredQuotations; }

    public List<Quotation> getAwaitingReplyQuotations() { return awaitingReplyQuotations; }
    public void setAwaitingReplyQuotations(List<Quotation> awaitingReplyQuotations) { this.awaitingReplyQuotations = awaitingReplyQuotations; }

    public List<Quotation> getDraftQuotations() { return draftQuotations; }
    public void setDraftQuotations(List<Quotation> draftQuotations) { this.draftQuotations = draftQuotations; }

    public List<Customer> getRecentlyUpdatedCustomers() { return recentlyUpdatedCustomers; }
    public void setRecentlyUpdatedCustomers(List<Customer> recentlyUpdatedCustomers) { this.recentlyUpdatedCustomers = recentlyUpdatedCustomers; }

    public List<Customer> getFollowUpCustomers() { return followUpCustomers; }
    public void setFollowUpCustomers(List<Customer> followUpCustomers) { this.followUpCustomers = followUpCustomers; }

    public int getReminderDays() { return reminderDays; }
    public void setReminderDays(int reminderDays) { this.reminderDays = reminderDays; }

    public List<ChartBar> getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(List<ChartBar> monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }

    public java.math.BigDecimal getYearlyRevenue() { return yearlyRevenue; }
    public void setYearlyRevenue(java.math.BigDecimal yearlyRevenue) { this.yearlyRevenue = yearlyRevenue; }

    public List<Quotation> getDepositQuotations() { return depositQuotations; }
    public void setDepositQuotations(List<Quotation> depositQuotations) { this.depositQuotations = depositQuotations; }

    public List<Task> getUpcomingTasks() { return upcomingTasks; }
    public void setUpcomingTasks(List<Task> upcomingTasks) { this.upcomingTasks = upcomingTasks; }

    public long getOverdueTaskCount() { return overdueTaskCount; }
    public void setOverdueTaskCount(long overdueTaskCount) { this.overdueTaskCount = overdueTaskCount; }
}
