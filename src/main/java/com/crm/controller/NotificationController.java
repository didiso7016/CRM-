package com.crm.controller;

import com.crm.dto.ContactLogForm;
import com.crm.entity.Customer;
import com.crm.enums.ContactType;
import com.crm.service.CompanySettingsService;
import com.crm.service.ContactLogService;
import com.crm.service.CustomerService;
import com.crm.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 通知中心:顯示需跟進的提醒,並可記錄一筆聯絡 / 通知。
 */
@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final ContactLogService contactLogService;
    private final CustomerService customerService;
    private final CompanySettingsService companySettingsService;

    public NotificationController(NotificationService notificationService,
                                 ContactLogService contactLogService,
                                 CustomerService customerService,
                                 CompanySettingsService companySettingsService) {
        this.notificationService = notificationService;
        this.contactLogService = contactLogService;
        this.customerService = customerService;
        this.companySettingsService = companySettingsService;
    }

    /** 目前設定的「客戶未聯絡」提醒天數(供畫面顯示「依預設(N 天)」) */
    private int reminderDays() {
        Integer d = companySettingsService.getOrCreate().getContactReminderDays();
        return d == null ? 30 : d;
    }

    @GetMapping("/notifications")
    public String index(@RequestParam(required = false) Long customerId, Model model) {
        model.addAttribute("activeMenu", "notifications");
        model.addAttribute("items", notificationService.load());
        model.addAttribute("recentLogs", contactLogService.recent());
        model.addAttribute("activeCustomers", customerService.listActiveForSelect());
        model.addAttribute("contactTypes", ContactType.values());
        model.addAttribute("reminderDays", reminderDays());
        if (!model.containsAttribute("logForm")) {
            ContactLogForm form = new ContactLogForm();
            form.setCustomerId(customerId);
            model.addAttribute("logForm", form);
        }
        return "notifications/index";
    }

    /** 刪除一筆聯絡紀錄 */
    @PostMapping("/notifications/log/{id}/delete")
    public String deleteLog(@PathVariable Long id,
                            @RequestParam(required = false) String redirect, RedirectAttributes ra) {
        contactLogService.delete(id);
        ra.addFlashAttribute("flashSuccess", "已刪除聯絡紀錄");
        return "redirect:" + (redirect != null && !redirect.isBlank() ? redirect : "/notifications");
    }

    /** 記錄一筆通知(例如:7/23 已發信給某客戶) */
    @PostMapping("/notifications/log")
    public String record(@Valid @ModelAttribute("logForm") ContactLogForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "notifications");
            model.addAttribute("items", notificationService.load());
            model.addAttribute("recentLogs", contactLogService.recent());
            model.addAttribute("activeCustomers", customerService.listActiveForSelect());
            model.addAttribute("contactTypes", ContactType.values());
            model.addAttribute("reminderDays", reminderDays());
            return "notifications/index";
        }
        Customer c = contactLogService.record(form);
        ra.addFlashAttribute("flashSuccess", "已通知「" + c.getCompanyName() + "」,已記錄於 " + form.getLogDate());
        return "redirect:/notifications";
    }
}
