package com.crm.controller;

import com.crm.service.NotificationService;
import com.crm.service.TaskService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 全域模型屬性:讓每個頁面的導覽列都能顯示通知與逾期待辦的數量徽章。
 */
@ControllerAdvice(basePackages = "com.crm.controller")
public class GlobalModelAttributes {

    private final NotificationService notificationService;
    private final TaskService taskService;

    public GlobalModelAttributes(NotificationService notificationService, TaskService taskService) {
        this.notificationService = notificationService;
        this.taskService = taskService;
    }

    @ModelAttribute("navNotificationCount")
    public int navNotificationCount() {
        try {
            return notificationService.count();
        } catch (RuntimeException e) {
            return 0; // 導覽列徽章不應影響頁面顯示
        }
    }

    @ModelAttribute("navOverdueTasks")
    public long navOverdueTasks() {
        try {
            return taskService.countOverdue();
        } catch (RuntimeException e) {
            return 0;
        }
    }
}
