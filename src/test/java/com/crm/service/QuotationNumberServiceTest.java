package com.crm.service;

import com.crm.repository.QuotationRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 報價單號產生測試:格式 QT-YYYYMM-0001,每月流水號、遞增。
 */
class QuotationNumberServiceTest {

    private final QuotationRepository repo = mock(QuotationRepository.class);
    private final QuotationNumberService service = new QuotationNumberService(repo);

    @Test
    void 當月無資料時從0001開始() {
        when(repo.findNumbersByPrefix(anyString())).thenReturn(List.of());
        String number = service.generate(LocalDate.of(2026, 7, 15));
        assertThat(number).isEqualTo("QT-202607-0001");
    }

    @Test
    void 依現有最大流水號遞增() {
        when(repo.findNumbersByPrefix(anyString()))
                .thenReturn(List.of("QT-202607-0001", "QT-202607-0002", "QT-202607-0003"));
        String number = service.generate(LocalDate.of(2026, 7, 20));
        assertThat(number).isEqualTo("QT-202607-0004");
    }

    @Test
    void 不同月份重新從0001計算() {
        // 八月查詢前綴為 QT-202608-,回傳空清單
        when(repo.findNumbersByPrefix("QT-202608-%")).thenReturn(List.of());
        String number = service.generate(LocalDate.of(2026, 8, 1));
        assertThat(number).isEqualTo("QT-202608-0001");
    }
}
