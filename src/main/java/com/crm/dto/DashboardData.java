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

    /** 圖表:近 6 個月成交營業額 / Top 客戶 / 成交率 */
    private List<ChartBar> monthlyRevenue;
    private List<ChartBar> topCustomers;
    private int winRatePercent;
    private long wonCount;
    private long lostCount;

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

    public List<ChartBar> getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(List<ChartBar> monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }

    public List<ChartBar> getTopCustomers() { return topCustomers; }
    public void setTopCustomers(List<ChartBar> topCustomers) { this.topCustomers = topCustomers; }

    public int getWinRatePercent() { return winRatePercent; }
    public void setWinRatePercent(int winRatePercent) { this.winRatePercent = winRatePercent; }

    public long getWonCount() { return wonCount; }
    public void setWonCount(long wonCount) { this.wonCount = wonCount; }

    public long getLostCount() { return lostCount; }
    public void setLostCount(long lostCount) { this.lostCount = lostCount; }
}
