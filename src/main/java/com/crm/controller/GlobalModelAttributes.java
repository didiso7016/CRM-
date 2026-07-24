package com.crm.controller;

import com.crm.service.NotificationService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 全域模型屬性:讓每個頁面的導覽列都能顯示通知數量徽章。
 */
@ControllerAdvice(basePackages = "com.crm.controller")
public class GlobalModelAttributes {

    private final NotificationService notificationService;

    public GlobalModelAttributes(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute("navNotificationCount")
    public int navNotificationCount() {
        try {
            return notificationService.count();
        } catch (RuntimeException e) {
            // 導覽列徽章不應影響頁面顯示
            return 0;
        }
    }
}
