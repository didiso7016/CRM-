package com.crm.service;

import com.crm.entity.CompanySettings;
import com.crm.repository.CompanySettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司設定商業邏輯。系統僅維護單一設定列(id=1),不存在時自動建立預設值。
 */
@Service
@Transactional
public class CompanySettingsService {

    private static final Long SETTINGS_ID = 1L;
    private final CompanySettingsRepository repository;

    public CompanySettingsService(CompanySettingsRepository repository) {
        this.repository = repository;
    }

    /** 取得設定,若無則建立預設 */
    @Transactional
    public CompanySettings getOrCreate() {
        return repository.findById(SETTINGS_ID).orElseGet(() -> {
            CompanySettings s = new CompanySettings();
            s.setId(SETTINGS_ID);
            return repository.save(s);
        });
    }

    public CompanySettings save(CompanySettings settings) {
        settings.setId(SETTINGS_ID);
        return repository.save(settings);
    }
}
