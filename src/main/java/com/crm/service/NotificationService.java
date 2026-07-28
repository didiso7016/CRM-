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
 * 通知中心:彙整需要跟進的提醒。涵蓋:
 *  1. 客戶長時間未聯絡
 *  2. 報價尚未完成(草稿放太久)
 *  3. 報價送出後尚未回覆
 *  4. 報價即將到期
 *  5. 報價已過期
 *  6. 客戶要求回覆日期(將至/已過)
 * (交期即將到期改於首頁「交期」卡呈現,不在此。)
 */
@Service
public class NotificationService {

    /** 進行中(尚未成交)的報價狀態 */
    private static final List<QuotationStatus> OPEN_STATUSES =
            List.of(QuotationStatus.DRAFT, QuotationStatus.SENT, QuotationStatus.CONFIRMING);
    /** 已送出、等待客戶回覆的狀態 */
    private static final List<QuotationStatus> SENT_STATUSES =
            List.of(QuotationStatus.SENT, QuotationStatus.CONFIRMING);

    /** 到期/回覆提醒的提前天數 */
    private static final int APPROACH_DAYS = 7;

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
        LocalDate cutoff = today.minusDays(followupDays);
        LocalDate approach = today.plusDays(APPROACH_DAYS);

        List<NotificationItem> items = new ArrayList<>();

        // 1. 客戶長時間未聯絡
        for (Customer c : customerService.needFollowUp(reminderDays)) {
            long days = daysSinceContact(c, today);
            items.add(new NotificationItem("CUSTOMER",
                    "「" + c.getCompanyName() + "」已經 " + days + " 天沒聯絡",
                    "/customers/" + c.getId(), c.getId(), days));
        }

        // 2. 報價尚未完成(草稿放太久)
        for (Quotation q : quotationRepository.findOpenBefore(List.of(QuotationStatus.DRAFT), cutoff)) {
            long days = ChronoUnit.DAYS.between(q.getQuotationDate(), today);
            items.add(new NotificationItem("QUOTE_DRAFT",
                    "報價單 " + q.getQuotationNumber() + "(" + name(q) + ")仍是草稿,已 " + days + " 天未完成",
                    link(q), null, days));
        }

        // 3. 報價送出後尚未回覆
        for (Quotation q : quotationRepository.findOpenBefore(SENT_STATUSES, cutoff)) {
            long days = ChronoUnit.DAYS.between(q.getQuotationDate(), today);
            items.add(new NotificationItem("QUOTE_NOREPLY",
                    "報價單 " + q.getQuotationNumber() + "(" + name(q) + ")送出後 " + days + " 天客戶尚未回覆",
                    link(q), null, days));
        }

        // 4. 報價即將到期
        for (Quotation q : quotationRepository.findExpiringSoon(today, approach, OPEN_STATUSES)) {
            long remaining = ChronoUnit.DAYS.between(today, q.getValidUntil());
            items.add(new NotificationItem("QUOTE_EXPIRING",
                    "報價單 " + q.getQuotationNumber() + "(" + name(q) + ")有效期限剩 " + remaining + " 天(" + q.getValidUntil() + ")",
                    link(q), null, -remaining));
        }

        // 5. 報價已過期
        for (Quotation q : quotationRepository.findExpiredOpen(today, OPEN_STATUSES)) {
            long overdue = ChronoUnit.DAYS.between(q.getValidUntil(), today);
            items.add(new NotificationItem("QUOTE_EXPIRED",
                    "報價單 " + q.getQuotationNumber() + "(" + name(q) + ")已過期 " + overdue + " 天(有效期限 " + q.getValidUntil() + ")",
                    link(q), null, overdue));
        }

        // 6. 客戶要求回覆日期(將至/已過)
        for (Quotation q : quotationRepository.findReplyDueBefore(approach, OPEN_STATUSES)) {
            long signed = ChronoUnit.DAYS.between(q.getCustomerReplyDueDate(), today);
            String msg = signed >= 0
                    ? "報價單 " + q.getQuotationNumber() + "(" + name(q) + ")客戶要求回覆日已過 " + signed + " 天(" + q.getCustomerReplyDueDate() + ")"
                    : "報價單 " + q.getQuotationNumber() + "(" + name(q) + ")客戶要求 " + q.getCustomerReplyDueDate() + " 前回覆(剩 " + (-signed) + " 天)";
            items.add(new NotificationItem("REPLY_DUE", msg, link(q), null, signed));
        }

        // 註:「交期即將到期」不放通知中心,改於首頁「交期」卡以倒數方式呈現。

        // 天數多(越逾期)的排前面
        items.sort((a, b) -> Long.compare(b.getDays(), a.getDays()));
        return items;
    }

    /** 通知總數(導覽列徽章用) */
    @Transactional(readOnly = true)
    public int count() {
        return load().size();
    }

    private String name(Quotation q) {
        return q.getCustomer() != null ? q.getCustomer().getCompanyName() : "";
    }

    private String link(Quotation q) {
        return "/quotations/" + q.getId();
    }

    /** 距最後聯絡的天數;從未聯絡則以建立日計算 */
    private long daysSinceContact(Customer c, LocalDate today) {
        LocalDateTime base = c.getLastContactedAt() != null ? c.getLastContactedAt()
                : (c.getCreatedAt() != null ? c.getCreatedAt() : today.atStartOfDay());
        long days = ChronoUnit.DAYS.between(base.toLocalDate(), today);
        return Math.max(days, 0);
    }
}
