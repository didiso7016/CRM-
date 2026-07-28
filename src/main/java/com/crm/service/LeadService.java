package com.crm.service;

import com.crm.dto.CustomerForm;
import com.crm.dto.LeadForm;
import com.crm.entity.Customer;
import com.crm.entity.Lead;
import com.crm.repository.LeadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客戶開發(Lead)商業邏輯:新增/編輯/停用、搜尋,以及「轉為正式客戶」。
 */
@Service
@Transactional
public class LeadService {

    private final LeadRepository leadRepository;
    private final CustomerService customerService;

    public LeadService(LeadRepository leadRepository, CustomerService customerService) {
        this.leadRepository = leadRepository;
        this.customerService = customerService;
    }

    @Transactional(readOnly = true)
    public Page<Lead> search(String keyword, String status, String priority, Pageable pageable) {
        return leadRepository.search(keyword == null ? "" : keyword.trim(),
                status == null ? "" : status,
                priority == null ? "" : priority, pageable);
    }

    @Transactional(readOnly = true)
    public Lead getById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到客戶開發資料(id=" + id + ")"));
    }

    public Lead create(LeadForm form) {
        Lead l = new Lead();
        apply(form, l);
        return leadRepository.save(l);
    }

    public Lead update(Long id, LeadForm form) {
        Lead l = getById(id);
        apply(form, l);
        return leadRepository.save(l);
    }

    public void deactivate(Long id) {
        Lead l = getById(id);
        l.setActive(false);
        leadRepository.save(l);
    }

    /**
     * 轉為正式客戶:以開發資料建立一筆客戶,帶入
     * 公司名稱 / 國家 / 城市 / Email / 電話 / 官網 / 備註,並記錄關聯。
     * 已轉入者不重複建立。
     */
    public Customer convertToCustomer(Long id) {
        Lead l = getById(id);
        if (l.isConverted()) {
            return customerService.getById(l.getConvertedCustomerId());
        }
        CustomerForm f = new CustomerForm();
        f.setCompanyName(l.getCompanyName());
        f.setCountry(l.getCountry());
        f.setCity(l.getCity());
        f.setEmail(l.getEmail());
        f.setPhone(l.getPhoneZalo());
        f.setWebsite(l.getWebsite());
        f.setNotes(l.getNotes());
        f.setActive(true);
        Customer c = customerService.create(f);
        l.setConvertedCustomerId(c.getId());
        leadRepository.save(l);
        return c;
    }

    /** 表單套用到 Entity(去前後空白) */
    private void apply(LeadForm form, Lead l) {
        l.setCompanyName(trim(form.getCompanyName()));
        l.setCountry(trim(form.getCountry()));
        l.setCity(trim(form.getCity()));
        l.setBusinessType(trim(form.getBusinessType()));
        l.setWebsite(trim(form.getWebsite()));
        l.setEmail(trim(form.getEmail()));
        l.setPhoneZalo(trim(form.getPhoneZalo()));
        l.setContactRoute(trim(form.getContactRoute()));
        l.setSourceUrl(trim(form.getSourceUrl()));
        l.setLeadType(trim(form.getLeadType()));
        l.setScore(form.getScore());
        l.setPriority(trim(form.getPriority()));
        l.setFastDeal(trim(form.getFastDeal()));
        l.setVerification(trim(form.getVerification()));
        l.setBrandConflict(trim(form.getBrandConflict()));
        l.setTargetProduct(trim(form.getTargetProduct()));
        l.setCustomAngle(trim(form.getCustomAngle()));
        l.setSuggestedSubject(trim(form.getSuggestedSubject()));
        l.setStatus(trim(form.getStatus()));
        l.setFirstContactDate(form.getFirstContactDate());
        l.setLatestContactDate(form.getLatestContactDate());
        l.setNextStep(trim(form.getNextStep()));
        l.setNotes(form.getNotes());
        // active 不由表單控制:新增預設列入清單(Entity 預設 true),移除改用「移除」按鈕
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
