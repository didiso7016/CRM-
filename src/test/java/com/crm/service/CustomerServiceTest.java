package com.crm.service;

import com.crm.entity.Customer;
import com.crm.repository.CustomerRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
}
