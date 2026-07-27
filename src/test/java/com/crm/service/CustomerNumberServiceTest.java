package com.crm.service;

import com.crm.repository.CustomerRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 客戶編號自動產生:格式 CUS-YYYY-NNN,每年重新起算。
 */
class CustomerNumberServiceTest {

    private final CustomerRepository repo = mock(CustomerRepository.class);
    private final CustomerNumberService service = new CustomerNumberService(repo);

    @Test
    void 當年度尚無編號時從001起算() {
        when(repo.findCodesByPrefix(anyString())).thenReturn(List.of());
        assertThat(service.generate(2026)).isEqualTo("CUS-2026-001");
    }

    @Test
    void 取當年度最大流水號加一() {
        when(repo.findCodesByPrefix(anyString()))
                .thenReturn(List.of("CUS-2026-001", "CUS-2026-003", "CUS-2026-002"));
        assertThat(service.generate(2026)).isEqualTo("CUS-2026-004");
    }

    @Test
    void 忽略格式不符的舊編號() {
        when(repo.findCodesByPrefix(anyString()))
                .thenReturn(java.util.Arrays.asList("CUS-2026-001", "C001", "舊編號", null));
        assertThat(service.generate(2026)).isEqualTo("CUS-2026-002");
    }
}
