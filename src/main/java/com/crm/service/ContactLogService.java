package com.crm.service;

import com.crm.dto.ContactLogForm;
import com.crm.entity.ContactLog;
import com.crm.entity.Customer;
import com.crm.repository.ContactLogRepository;
import com.crm.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聯絡 / 通知紀錄商業邏輯。
 * 記錄一筆通知後,同步更新客戶的最後聯絡時間(讓未聯絡提醒消失)。
 */
@Service
@Transactional
public class ContactLogService {

    private final ContactLogRepository contactLogRepository;
    private final CustomerRepository customerRepository;

    public ContactLogService(ContactLogRepository contactLogRepository,
                             CustomerRepository customerRepository) {
        this.contactLogRepository = contactLogRepository;
        this.customerRepository = customerRepository;
    }

    /** 記錄一筆通知,並更新客戶最後聯絡時間。回傳所屬客戶。 */
    public Customer record(ContactLogForm form) {
        Customer customer = customerRepository.findById(form.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到客戶"));

        ContactLog log = new ContactLog();
        log.setCustomer(customer);
        log.setLogDate(form.getLogDate());
        log.setType(form.getType());
        log.setNote(form.getNote() == null ? null : form.getNote().trim());
        contactLogRepository.save(log);

        // 更新最後聯絡時間:取較新的時間,避免補記舊日期時把時間往回調
        LocalDateTime logTime = form.getLogDate().atStartOfDay();
        if (customer.getLastContactedAt() == null || logTime.isAfter(customer.getLastContactedAt())) {
            customer.setLastContactedAt(logTime);
        }

        // 下次追蹤:數字=幾天後(並自動納入提醒)、stop=不用再追、空=依全域規則
        String nf = form.getNextFollowUp();
        if ("stop".equals(nf)) {
            customer.setFollowUpEnabled(false);
            customer.setNextFollowUpDate(null);
        } else if (nf != null && nf.matches("\\d+")) {
            customer.setFollowUpEnabled(true);
            customer.setNextFollowUpDate(form.getLogDate().plusDays(Integer.parseInt(nf)));
        } else {
            customer.setNextFollowUpDate(null); // 依全域「N 天沒聯絡」規則
        }

        customerRepository.save(customer);
        return customer;
    }

    /** 刪除一筆聯絡紀錄(不影響客戶的最後聯絡時間) */
    public void delete(Long id) {
        contactLogRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ContactLog> recent() {
        return contactLogRepository.findTop15ByOrderByLogDateDescIdDesc();
    }

    @Transactional(readOnly = true)
    public List<ContactLog> listByCustomer(Long customerId) {
        return contactLogRepository.findByCustomerIdOrderByLogDateDescIdDesc(customerId);
    }
}
