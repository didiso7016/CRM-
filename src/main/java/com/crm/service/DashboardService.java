package com.crm.service;

import com.crm.dto.DashboardData;
import com.crm.entity.CompanySettings;
import com.crm.enums.QuotationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 首頁 Dashboard 資料組裝。統計數據與清單皆來自各 Service,集中於此。
 */
@Service
public class DashboardService {

    private static final int EXPIRING_DAYS = 7; // 「即將到期」的天數門檻

    private final CustomerService customerService;
    private final QuotationService quotationService;
    private final CompanySettingsService companySettingsService;

    public DashboardService(CustomerService customerService,
                            QuotationService quotationService,
                            CompanySettingsService companySettingsService) {
        this.customerService = customerService;
        this.quotationService = quotationService;
        this.companySettingsService = companySettingsService;
    }

    @Transactional(readOnly = true)
    public DashboardData load() {
        CompanySettings settings = companySettingsService.getOrCreate();
        int reminderDays = settings.getContactReminderDays() == null ? 30 : settings.getContactReminderDays();

        DashboardData d = new DashboardData();
        d.setCustomerCount(customerService.countActive());
        d.setMonthlyQuotationCount(quotationService.countThisMonth());
        // 報價中 = 草稿 + 已送出 + 客戶確認中
        d.setInProgressCount(quotationService.countByStatus(QuotationStatus.DRAFT)
                + quotationService.countByStatus(QuotationStatus.SENT)
                + quotationService.countByStatus(QuotationStatus.CONFIRMING));
        d.setAcceptedCount(quotationService.countByStatus(QuotationStatus.ACCEPTED));

        d.setRecentQuotations(quotationService.recent());
        d.setExpiringQuotations(quotationService.expiringSoon(EXPIRING_DAYS));
        d.setRecentlyUpdatedCustomers(customerService.recentlyUpdated());

        // 客戶關懷提醒:超過設定天數未聯絡的客戶
        d.setFollowUpCustomers(customerService.needFollowUp(reminderDays));
        d.setReminderDays(reminderDays);
        return d;
    }
}
