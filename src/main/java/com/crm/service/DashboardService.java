package com.crm.service;

import com.crm.dto.ChartBar;
import com.crm.dto.DashboardData;
import com.crm.entity.CompanySettings;
import com.crm.entity.Quotation;
import com.crm.enums.QuotationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 首頁 Dashboard 資料組裝。統計數據與清單皆來自各 Service,集中於此。
 */
@Service
public class DashboardService {

    private static final int EXPIRING_DAYS = 7; // 「即將到期」的天數門檻

    private final CustomerService customerService;
    private final QuotationService quotationService;
    private final CompanySettingsService companySettingsService;
    private final TaskService taskService;

    public DashboardService(CustomerService customerService,
                            QuotationService quotationService,
                            CompanySettingsService companySettingsService,
                            TaskService taskService) {
        this.customerService = customerService;
        this.quotationService = quotationService;
        this.companySettingsService = companySettingsService;
        this.taskService = taskService;
    }

    @Transactional(readOnly = true)
    public DashboardData load() {
        CompanySettings settings = companySettingsService.getOrCreate();
        int reminderDays = settings.getContactReminderDays() == null ? 30 : settings.getContactReminderDays();

        DashboardData d = new DashboardData();
        d.setCustomerCount(customerService.countActive());
        d.setMonthlyQuotationCount(quotationService.countThisMonth());
        // 報價中 = 草稿 + 已送出 + 客戶確認中(僅計最新版本,舊版不算)
        d.setInProgressCount(quotationService.countInProgress());
        d.setAcceptedCount(quotationService.countByStatus(QuotationStatus.ACCEPTED));

        d.setRecentQuotations(quotationService.recent());
        d.setExpiringQuotations(quotationService.expiringSoon(EXPIRING_DAYS));
        d.setRecentlyUpdatedCustomers(customerService.recentlyUpdated());

        // 客戶關懷提醒:超過設定天數未聯絡的客戶
        d.setFollowUpCustomers(customerService.needFollowUp(reminderDays));
        d.setReminderDays(reminderDays);

        // ===== 成交金額(基準 = 付清尾款)=====
        // 幣別統一:各報價幣別不同,直接相加會失真;此處只統計 USD 報價,以美金呈現。
        List<Quotation> paid = quotationService.paidQuotations().stream()
                .filter(q -> "USD".equalsIgnoreCase(q.getCurrency()))
                .toList();
        d.setMonthlyRevenue(buildMonthlyRevenue(paid));
        d.setYearlyRevenue(buildYearlyRevenue(paid));
        // 收訂中的公司(已收訂,尚未付清尾款)
        d.setDepositQuotations(quotationService.depositQuotations());

        // ===== 待辦 =====
        List<com.crm.entity.Task> openTasks = taskService.listOpen();
        d.setUpcomingTasks(openTasks.size() > 6 ? openTasks.subList(0, 6) : openTasks);
        d.setOverdueTaskCount(taskService.countOverdue());
        return d;
    }

    /** 近 6 個月的成交營業額(依報價日期彙總已接受報價的含稅總計) */
    private List<ChartBar> buildMonthlyRevenue(List<Quotation> accepted) {
        YearMonth now = YearMonth.now();
        Map<YearMonth, BigDecimal> byMonth = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            byMonth.put(now.minusMonths(i), BigDecimal.ZERO);
        }
        for (Quotation q : accepted) {
            if (q.getQuotationDate() == null) continue;
            YearMonth ym = YearMonth.from(q.getQuotationDate());
            if (byMonth.containsKey(ym)) {
                byMonth.merge(ym, nz(q.getTotalAmount()), BigDecimal::add);
            }
        }
        BigDecimal max = byMonth.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        List<ChartBar> bars = new ArrayList<>();
        for (Map.Entry<YearMonth, BigDecimal> e : byMonth.entrySet()) {
            bars.add(new ChartBar(e.getKey().getMonthValue() + "月", e.getValue(), pct(e.getValue(), max)));
        }
        return bars;
    }

    /** 本年度成交金額合計(已收訂報價) */
    private BigDecimal buildYearlyRevenue(List<Quotation> won) {
        int year = LocalDate.now().getYear();
        BigDecimal total = BigDecimal.ZERO;
        for (Quotation q : won) {
            if (q.getQuotationDate() != null && q.getQuotationDate().getYear() == year) {
                total = total.add(nz(q.getTotalAmount()));
            }
        }
        return total;
    }

    private int pct(BigDecimal v, BigDecimal max) {
        if (max == null || max.signum() == 0) return 0;
        return v.multiply(new BigDecimal("100")).divide(max, 0, java.math.RoundingMode.HALF_UP).intValue();
    }

    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
