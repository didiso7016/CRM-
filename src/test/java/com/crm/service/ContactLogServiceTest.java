package com.crm.service;

import com.crm.dto.ContactLogForm;
import com.crm.entity.Customer;
import com.crm.enums.ContactType;
import com.crm.repository.ContactLogRepository;
import com.crm.repository.CustomerRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 聯絡紀錄:記錄一筆通知會同步更新客戶最後聯絡時間(且不會被較舊的日期往回調)。
 */
class ContactLogServiceTest {

    private final ContactLogRepository logRepo = mock(ContactLogRepository.class);
    private final CustomerRepository customerRepo = mock(CustomerRepository.class);
    private final ContactLogService service = new ContactLogService(logRepo, customerRepo);

    private ContactLogForm form(LocalDate date) {
        ContactLogForm f = new ContactLogForm();
        f.setCustomerId(1L);
        f.setLogDate(date);
        f.setType(ContactType.EMAIL);
        f.setNote("test");
        return f;
    }

    @Test
    void 記錄後更新最後聯絡時間() {
        Customer c = new Customer();
        c.setId(1L);
        when(customerRepo.findById(1L)).thenReturn(Optional.of(c));

        service.record(form(LocalDate.of(2026, 7, 20)));

        assertThat(c.getLastContactedAt()).isEqualTo(LocalDate.of(2026, 7, 20).atStartOfDay());
        verify(logRepo).save(any());
        verify(customerRepo).save(c);
    }

    @Test
    void 補記較舊日期不會把最後聯絡時間往回調() {
        Customer c = new Customer();
        c.setId(1L);
        c.setLastContactedAt(LocalDateTime.of(2026, 7, 25, 0, 0));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(c));

        service.record(form(LocalDate.of(2026, 7, 10))); // 較舊

        // 仍維持較新的 7/25,不被 7/10 覆蓋(最後聯絡時間不往回調)
        assertThat(c.getLastContactedAt()).isEqualTo(LocalDateTime.of(2026, 7, 25, 0, 0));
        verify(logRepo).save(any());
    }
}
