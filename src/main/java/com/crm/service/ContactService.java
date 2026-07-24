package com.crm.service;

import com.crm.dto.ContactForm;
import com.crm.entity.Contact;
import com.crm.entity.Customer;
import com.crm.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 聯絡人商業邏輯。維持「同一客戶僅一位主要聯絡人」的規則。
 */
@Service
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;
    private final CustomerService customerService;

    public ContactService(ContactRepository contactRepository, CustomerService customerService) {
        this.contactRepository = contactRepository;
        this.customerService = customerService;
    }

    @Transactional(readOnly = true)
    public List<Contact> listByCustomer(Long customerId) {
        return contactRepository.findByCustomerIdOrderByPrimaryContactDescNameAsc(customerId);
    }

    @Transactional(readOnly = true)
    public Contact getById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到聯絡人(id=" + id + ")"));
    }

    public Contact create(ContactForm form) {
        Customer customer = customerService.getById(form.getCustomerId());
        Contact c = new Contact();
        c.setCustomer(customer);
        apply(form, c);
        // 若設為主要聯絡人,先清除同客戶其他人的主要標記
        if (form.isPrimaryContact()) {
            contactRepository.clearPrimaryFlag(customer.getId());
        }
        return contactRepository.save(c);
    }

    public Contact update(Long id, ContactForm form) {
        Contact c = getById(id);
        // 確保聯絡人仍屬於原客戶,避免跨客戶竄改
        if (!c.getCustomer().getId().equals(form.getCustomerId())) {
            throw new IllegalArgumentException("聯絡人不屬於所選客戶");
        }
        apply(form, c);
        if (form.isPrimaryContact()) {
            contactRepository.clearPrimaryFlag(c.getCustomer().getId());
            c.setPrimaryContact(true);
        }
        return contactRepository.save(c);
    }

    public void delete(Long id) {
        Contact c = getById(id);
        contactRepository.delete(c);
    }

    /** 設定指定聯絡人為主要聯絡人(同客戶其他人取消) */
    public void setPrimary(Long id) {
        Contact c = getById(id);
        contactRepository.clearPrimaryFlag(c.getCustomer().getId());
        c.setPrimaryContact(true);
        contactRepository.save(c);
    }

    private void apply(ContactForm form, Contact c) {
        c.setName(trim(form.getName()));
        c.setDepartment(trim(form.getDepartment()));
        c.setJobTitle(trim(form.getJobTitle()));
        c.setPhone(trim(form.getPhone()));
        c.setExtensionNumber(trim(form.getExtensionNumber()));
        c.setMobile(trim(form.getMobile()));
        c.setEmail(trim(form.getEmail()));
        c.setPrimaryContact(form.isPrimaryContact());
        c.setNotes(form.getNotes());
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
