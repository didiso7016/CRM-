package com.crm.config;

import com.crm.service.ContactLogService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 啟動時自我對齊:確保每個客戶的「最後聯絡時間」= 其最新聯絡紀錄
 * (紀錄被刪除或早期資料不一致時自動修正)。第一次對齊後即為無異動的空操作。
 */
@Component
@Order(100) // 在示範資料灌入(若有)之後執行
public class StartupReconciler implements ApplicationRunner {

    private final ContactLogService contactLogService;

    public StartupReconciler(ContactLogService contactLogService) {
        this.contactLogService = contactLogService;
    }

    @Override
    public void run(ApplicationArguments args) {
        contactLogService.reconcileAll();
    }
}
