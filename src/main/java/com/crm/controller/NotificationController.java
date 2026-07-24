package com.crm.controller;

import com.crm.dto.ContactLogForm;
import com.crm.entity.Customer;
import com.crm.enums.ContactType;
import com.crm.service.ContactLogService;
import com.crm.service.CustomerService;
import com.crm.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    public NotificationController(NotificationService notificationService,
                                 ContactLogService contactLogService,
                                 CustomerService customerService) {
        this.notificationService = notificationService;
        this.contactLogService = contactLogService;
        this.customerService = customerService;
    }

    @GetMapping("/notifications")
    public String index(@RequestParam(required = false) Long customerId, Model model) {
        model.addAttribute("activeMenu", "notifications");
        model.addAttribute("items", notificationService.load());
        model.addAttribute("recentLogs", contactLogService.recent());
        model.addAttribute("activeCustomers", customerService.search("", true));
        model.addAttribute("contactTypes", ContactType.values());
        if (!model.containsAttribute("logForm")) {
            ContactLogForm form = new ContactLogForm();
            form.setCustomerId(customerId);
            model.addAttribute("logForm", form);
        }
        return "notifications/index";
    }

    /** 記錄一筆通知(例如:7/23 已發信給某客戶) */
    @PostMapping("/notifications/log")
    public String record(@Valid @ModelAttribute("logForm") ContactLogForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "notifications");
            model.addAttribute("items", notificationService.load());
            model.addAttribute("recentLogs", contactLogService.recent());
            model.addAttribute("activeCustomers", customerService.search("", true));
            model.addAttribute("contactTypes", ContactType.values());
            return "notifications/index";
        }
        Customer c = contactLogService.record(form);
        ra.addFlashAttribute("flashSuccess", "已通知「" + c.getCompanyName() + "」,已記錄於 " + form.getLogDate());
        return "redirect:/notifications";
    }
}
