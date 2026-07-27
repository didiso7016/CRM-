package com.crm.service;

import com.crm.dto.CustomerForm;
import com.crm.entity.Customer;
import com.crm.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客戶商業邏輯:新增/編輯/停用、搜尋、關懷提醒與客戶編號重複檢查。
 */
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> search(String keyword, boolean onlyActive) {
        return customerRepository.search(keyword == null ? "" : keyword.trim(), onlyActive);
    }

    @Transactional(readOnly = true)
    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到客戶(id=" + id + ")"));
    }

    /** 客戶編號是否重複。excludeId 為編輯時排除自己。 */
    @Transactional(readOnly = true)
    public boolean isCodeDuplicate(String customerCode, Long excludeId) {
        if (customerCode == null || customerCode.isBlank()) {
            return false;
        }
        return customerRepository.findByCustomerCode(customerCode.trim())
                .filter(c -> !c.getId().equals(excludeId))
                .isPresent();
    }

    public Customer create(CustomerForm form) {
        Customer c = new Customer();
        apply(form, c);
        return customerRepository.save(c);
    }

    public Customer update(Long id, CustomerForm form) {
        Customer c = getById(id);
        apply(form, c);
        return customerRepository.save(c);
    }

    /** 停用(軟刪除),歷史報價仍可查詢 */
    public void deactivate(Long id) {
        Customer c = getById(id);
        c.setActive(false);
        customerRepository.save(c);
    }

    /** 重新啟用 */
    public void activate(Long id) {
        Customer c = getById(id);
        c.setActive(true);
        customerRepository.save(c);
    }

    /** 記錄一次聯絡:更新最後聯絡時間(供未聯絡提醒) */
    public void recordContact(Long id) {
        Customer c = getById(id);
        c.setLastContactedAt(LocalDateTime.now());
        customerRepository.save(c);
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return customerRepository.countByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Customer> recentlyUpdated() {
        return customerRepository.findTop5ByOrderByUpdatedAtDesc();
    }

    /** 已標記跟進、且超過 days 天未聯絡的啟用客戶(排除延後中的) */
    @Transactional(readOnly = true)
    public List<Customer> needFollowUp(int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        return customerRepository.findNeedFollowUp(threshold, LocalDate.now());
    }

    /** 設定是否納入跟進提醒;重新啟用時清除延後 */
    public void setFollowUp(Long id, boolean enabled) {
        Customer c = getById(id);
        c.setFollowUpEnabled(enabled);
        if (enabled) {
            c.setFollowUpSnoozeUntil(null);
        }
        customerRepository.save(c);
    }

    /** 延後提醒指定天數 */
    public void snoozeFollowUp(Long id, int days) {
        Customer c = getById(id);
        c.setFollowUpSnoozeUntil(LocalDate.now().plusDays(days));
        customerRepository.save(c);
    }

    /** 將表單資料套用到 Entity(去除前後空白) */
    private void apply(CustomerForm form, Customer c) {
        c.setCustomerCode(trim(form.getCustomerCode()));
        c.setCompanyName(trim(form.getCompanyName()));
        c.setTaxId(trim(form.getTaxId()));
        c.setPhone(trim(form.getPhone()));
        c.setFax(trim(form.getFax()));
        c.setEmail(trim(form.getEmail()));
        c.setAddress(trim(form.getAddress()));
        c.setCountry(trim(form.getCountry()));
        c.setCity(trim(form.getCity()));
        c.setCustomerType(form.getCustomerType());
        c.setIndustry(trim(form.getIndustry()));
        c.setSource(trim(form.getSource()));
        c.setNotes(form.getNotes());
        c.setActive(form.isActive());
        c.setFollowUpEnabled(form.isFollowUpEnabled());
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
