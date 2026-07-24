package com.crm.service;

import com.crm.dto.NotificationItem;
import com.crm.entity.CompanySettings;
import com.crm.entity.Customer;
import com.crm.entity.Quotation;
import com.crm.enums.QuotationStatus;
import com.crm.repository.QuotationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知中心:彙整需要跟進的提醒。
 *  1. 超過設定天數未聯絡的客戶
 *  2. 報價後超過設定天數仍未成交的報價單
 */
@Service
public class NotificationService {

    /** 視為「進行中、需跟進」的報價狀態 */
    private static final List<QuotationStatus> OPEN_STATUSES =
            List.of(QuotationStatus.DRAFT, QuotationStatus.SENT, QuotationStatus.CONFIRMING);

    private final CustomerService customerService;
    private final QuotationRepository quotationRepository;
    private final CompanySettingsService companySettingsService;

    public NotificationService(CustomerService customerService,
                               QuotationRepository quotationRepository,
                               CompanySettingsService companySettingsService) {
        this.customerService = customerService;
        this.quotationRepository = quotationRepository;
        this.companySettingsService = companySettingsService;
    }

    /** 產生所有通知項目 */
    @Transactional(readOnly = true)
    public List<NotificationItem> load() {
        CompanySettings s = companySettingsService.getOrCreate();
        int reminderDays = s.getContactReminderDays() == null ? 30 : s.getContactReminderDays();
        int followupDays = s.getQuotationFollowupDays() == null ? 14 : s.getQuotationFollowupDays();
        LocalDate today = LocalDate.now();

        List<NotificationItem> items = new ArrayList<>();

        // 1. 客戶未聯絡
        for (Customer c : customerService.needFollowUp(reminderDays)) {
            long days = daysSinceContact(c, today);
            String msg = "「" + c.getCompanyName() + "」已經 " + days + " 天沒聯絡";
            items.add(new NotificationItem("CUSTOMER", msg, "/customers/" + c.getId(), c.getId(), days));
        }

        // 2. 報價後未成交
        LocalDate cutoff = today.minusDays(followupDays);
        for (Quotation q : quotationRepository.findOpenBefore(OPEN_STATUSES, cutoff)) {
            long days = ChronoUnit.DAYS.between(q.getQuotationDate(), today);
            String msg = "報價單 " + q.getQuotationNumber() + "(" + q.getCustomer().getCompanyName()
                    + ")報價後已經 " + days + " 天未成交";
            items.add(new NotificationItem("QUOTATION", msg, "/quotations/" + q.getId(), null, days));
        }

        // 天數多的排前面
        items.sort((a, b) -> Long.compare(b.getDays(), a.getDays()));
        return items;
    }

    /** 通知總數(導覽列徽章用) */
    @Transactional(readOnly = true)
    public int count() {
        return load().size();
    }

    /** 距最後聯絡的天數;從未聯絡則以建立日計算 */
    private long daysSinceContact(Customer c, LocalDate today) {
        LocalDateTime base = c.getLastContactedAt() != null ? c.getLastContactedAt()
                : (c.getCreatedAt() != null ? c.getCreatedAt() : today.atStartOfDay());
        long days = ChronoUnit.DAYS.between(base.toLocalDate(), today);
        return Math.max(days, 0);
    }
}
