package com.crm.repository;

import com.crm.entity.CompanySettings;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 公司設定資料存取(單列)。
 */
public interface CompanySettingsRepository extends JpaRepository<CompanySettings, Long> {
}
