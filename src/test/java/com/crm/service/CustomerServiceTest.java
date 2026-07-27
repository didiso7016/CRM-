package com.crm.service;

import com.crm.entity.Customer;
import com.crm.repository.CustomerRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 客戶編號重複驗證測試。
 */
class CustomerServiceTest {

    private final CustomerRepository repo = mock(CustomerRepository.class);
    private final CustomerService service = new CustomerService(repo);

    private Customer withId(Long id) {
        Customer c = new Customer();
        c.setId(id);
        c.setCustomerCode("C001");
        return c;
    }

    @Test
    void 編號不存在時不算重複() {
        when(repo.findByCustomerCode("C001")).thenReturn(Optional.empty());
        assertThat(service.isCodeDuplicate("C001", null)).isFalse();
    }

    @Test
    void 編號被其他客戶使用時算重複() {
        when(repo.findByCustomerCode("C001")).thenReturn(Optional.of(withId(5L)));
        assertThat(service.isCodeDuplicate("C001", null)).isTrue();
    }

    @Test
    void 編輯自己時不算重複() {
        // 編號屬於 id=5 的客戶,排除 id=5 → 非重複
        when(repo.findByCustomerCode("C001")).thenReturn(Optional.of(withId(5L)));
        assertThat(service.isCodeDuplicate("C001", 5L)).isFalse();
    }

    @Test
    void 空白編號視為不重複() {
        assertThat(service.isCodeDuplicate("  ", null)).isFalse();
    }

    @Test
    void 啟用跟進提醒會清除延後() {
        Customer c = new Customer();
        c.setId(5L);
        c.setFollowUpSnoozeUntil(LocalDate.of(2026, 8, 1));
        when(repo.findById(5L)).thenReturn(Optional.of(c));

        service.setFollowUp(5L, true);

        assertThat(c.isFollowUpEnabled()).isTrue();
        assertThat(c.getFollowUpSnoozeUntil()).isNull();
    }

    @Test
    void 停止跟進提醒() {
        Customer c = new Customer();
        c.setId(5L);
        c.setFollowUpEnabled(true);
        when(repo.findById(5L)).thenReturn(Optional.of(c));

        service.setFollowUp(5L, false);

        assertThat(c.isFollowUpEnabled()).isFalse();
    }

    @Test
    void 延後提醒會設定延後日期() {
        Customer c = new Customer();
        c.setId(5L);
        when(repo.findById(5L)).thenReturn(Optional.of(c));

        service.snoozeFollowUp(5L, 30);

        assertThat(c.getFollowUpSnoozeUntil()).isEqualTo(LocalDate.now().plusDays(30));
    }
}
