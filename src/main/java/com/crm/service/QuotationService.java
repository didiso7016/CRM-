package com.crm.service;

import com.crm.dto.QuotationForm;
import com.crm.dto.QuotationItemForm;
import com.crm.entity.*;
import com.crm.enums.QuotationStatus;
import com.crm.repository.ContactRepository;
import com.crm.repository.CustomerRepository;
import com.crm.repository.ProductRepository;
import com.crm.repository.QuotationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 報價單商業邏輯:建立、編輯、複製、建立新版、狀態變更、搜尋與驗證。
 * 金額計算一律委由 PricingService,後端重新計算,不信任前端金額。
 */
@Service
@Transactional
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationNumberService numberService;
    private final PricingService pricingService;
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;

    public QuotationService(QuotationRepository quotationRepository,
                            QuotationNumberService numberService,
                            PricingService pricingService,
                            CustomerRepository customerRepository,
                            ContactRepository contactRepository,
                            ProductRepository productRepository) {
        this.quotationRepository = quotationRepository;
        this.numberService = numberService;
        this.pricingService = pricingService;
        this.customerRepository = customerRepository;
        this.contactRepository = contactRepository;
        this.productRepository = productRepository;
    }

    // ===================== 查詢 =====================

    @Transactional(readOnly = true)
    public Quotation getById(Long id) {
        return quotationRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到報價單(id=" + id + ")"));
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Quotation> search(Long customerId, String number, String keyword,
                                  QuotationStatus status, LocalDate from, LocalDate to,
                                  java.math.BigDecimal minAmount, java.math.BigDecimal maxAmount,
                                  org.springframework.data.domain.Pageable pageable) {
        return quotationRepository.search(customerId,
                number == null ? "" : number.trim(),
                keyword == null ? "" : keyword.trim(),
                status, from, to, minAmount, maxAmount, pageable);
    }

    @Transactional(readOnly = true)
    public List<Quotation> historyByCustomer(Long customerId) {
        return quotationRepository.findByCustomerIdOrderByQuotationNumberDescVersionDesc(customerId);
    }

    /** 同一報價單號的所有版本(新到舊),供明細頁的版本歷史 */
    @Transactional(readOnly = true)
    public List<Quotation> getVersions(String quotationNumber) {
        return quotationRepository.findByQuotationNumberOrderByVersionDesc(quotationNumber);
    }

    /** 報價中(草稿/已送出/確認中)且為最新版本的數量 */
    @Transactional(readOnly = true)
    public long countInProgress() {
        return quotationRepository.countByStatusInLatest(
                List.of(QuotationStatus.DRAFT, QuotationStatus.SENT, QuotationStatus.CONFIRMING));
    }

    // ===================== 驗證 =====================

    /**
     * 業務驗證,回傳錯誤訊息清單(空清單代表通過)。
     * 涵蓋:至少一筆品項、數量>0、單價>=0、折扣0~100、稅率>=0、
     *       有效期限不早於報價日期、聯絡人須屬於所選客戶。
     */
    @Transactional(readOnly = true)
    public List<String> validate(QuotationForm form) {
        List<String> errors = new ArrayList<>();

        List<QuotationItemForm> validItems = nonBlankItems(form);
        if (validItems.isEmpty()) {
            errors.add("報價單至少要有一筆品項");
        }
        int row = 1;
        for (QuotationItemForm item : validItems) {
            if (item.getQuantity() == null || item.getQuantity().signum() <= 0) {
                errors.add("第 " + row + " 列:數量必須大於 0");
            } else if (item.getQuantity().remainder(BigDecimal.ONE).signum() != 0) {
                errors.add("第 " + row + " 列:數量必須是整數(不可有小數)");
            }
            if (item.getUnitPrice() != null && item.getUnitPrice().signum() < 0) {
                errors.add("第 " + row + " 列:單價不得小於 0");
            } else if (item.getUnitPrice() != null && item.getUnitPrice().stripTrailingZeros().scale() > 1) {
                errors.add("第 " + row + " 列:單價最多只能到小數點後 1 位");
            }
            BigDecimal disc = item.getDiscountRate();
            if (disc != null && (disc.compareTo(BigDecimal.ZERO) < 0 || disc.compareTo(new BigDecimal("100")) > 0)) {
                errors.add("第 " + row + " 列:折扣必須介於 0 到 100");
            }
            row++;
        }

        if (form.getTaxRate() != null && form.getTaxRate().signum() < 0) {
            errors.add("稅率不得小於 0");
        }
        if (form.getValidUntil() != null && form.getQuotationDate() != null
                && form.getValidUntil().isBefore(form.getQuotationDate())) {
            errors.add("報價有效期限不得早於報價日期");
        }
        if (form.getContactId() != null) {
            contactRepository.findById(form.getContactId()).ifPresent(c -> {
                if (!c.getCustomer().getId().equals(form.getCustomerId())) {
                    errors.add("所選聯絡人不屬於此客戶");
                }
            });
        }
        return errors;
    }

    // ===================== 建立 / 編輯 =====================

    /** 建立新報價單(版本 1、草稿、自動產生單號) */
    public Quotation create(QuotationForm form) {
        Quotation q = new Quotation();
        q.setQuotationNumber(numberService.generate(form.getQuotationDate()));
        q.setVersion(1);
        q.setStatus(QuotationStatus.DRAFT);
        applyHeader(form, q);
        rebuildItems(form, q);
        pricingService.recalculate(q);
        return quotationRepository.save(q);
    }

    /** 更新既有報價單(保留單號、版本、狀態、建立時間) */
    public Quotation update(Long id, QuotationForm form) {
        Quotation q = getById(id);
        applyHeader(form, q);
        q.clearItems();
        rebuildItems(form, q);
        pricingService.recalculate(q);
        return quotationRepository.save(q);
    }

    /** 複製為全新報價單(新單號、版本 1、草稿) */
    public Quotation copy(Long sourceId) {
        Quotation src = getById(sourceId);
        Quotation q = new Quotation();
        q.setQuotationNumber(numberService.generate(LocalDate.now()));
        q.setVersion(1);
        q.setStatus(QuotationStatus.DRAFT);
        copyHeader(src, q);
        q.setQuotationDate(LocalDate.now());
        copyItems(src, q);
        pricingService.recalculate(q);
        return quotationRepository.save(q);
    }

    /** 建立新版:沿用同一單號,版本 = 目前最大版本 + 1,草稿,保留原版 */
    public Quotation newVersion(Long sourceId) {
        Quotation src = getById(sourceId);
        Quotation q = new Quotation();
        q.setQuotationNumber(src.getQuotationNumber());
        Integer maxVersion = quotationRepository.findMaxVersion(src.getQuotationNumber());
        q.setVersion((maxVersion == null ? src.getVersion() : maxVersion) + 1);
        q.setStatus(QuotationStatus.DRAFT);
        copyHeader(src, q);
        q.setQuotationDate(LocalDate.now());
        copyItems(src, q);
        pricingService.recalculate(q);
        return quotationRepository.save(q);
    }

    /** 變更報價狀態 */
    public Quotation changeStatus(Long id, QuotationStatus status) {
        Quotation q = getById(id);
        q.setStatus(status);
        return quotationRepository.save(q);
    }

    // ===================== 內部輔助 =====================

    private List<QuotationItemForm> nonBlankItems(QuotationForm form) {
        List<QuotationItemForm> result = new ArrayList<>();
        if (form.getItems() != null) {
            for (QuotationItemForm item : form.getItems()) {
                if (item != null && !item.isBlank()) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /** 套用報價單表頭欄位 */
    private void applyHeader(QuotationForm form, Quotation q) {
        Customer customer = customerRepository.findById(form.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到客戶"));
        q.setCustomer(customer);

        if (form.getContactId() != null) {
            Contact contact = contactRepository.findById(form.getContactId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到聯絡人"));
            // 再次確認聯絡人屬於此客戶
            if (!contact.getCustomer().getId().equals(customer.getId())) {
                throw new IllegalArgumentException("所選聯絡人不屬於此客戶");
            }
            q.setContact(contact);
        } else {
            q.setContact(null);
        }

        q.setQuotationDate(form.getQuotationDate());
        q.setCustomerInquiryNumber(trim(form.getCustomerInquiryNumber()));
        q.setValidUntil(form.getValidUntil());
        q.setCurrency(form.getCurrency());
        q.setTaxType(form.getTaxType());
        q.setTaxRate(nz(form.getTaxRate()));
        q.setPaymentTerms(trim(form.getPaymentTerms()));
        q.setDeliveryTerms(trim(form.getDeliveryTerms()));
        q.setEstimatedDelivery(trim(form.getEstimatedDelivery()));
        q.setDeliveryDueDate(form.getDeliveryDueDate());
        q.setOverallDiscount(nz(form.getOverallDiscount()));
        q.setFreight(nz(form.getFreight()));
        q.setOtherFee(nz(form.getOtherFee()));
        q.setQuotationNotes(form.getQuotationNotes());
        q.setInternalNotes(form.getInternalNotes());
    }

    /** 依表單品項重建報價品項(含料號快照與項次) */
    private void rebuildItems(QuotationForm form, Quotation q) {
        int seq = 1;
        for (QuotationItemForm f : nonBlankItems(form)) {
            QuotationItem item = new QuotationItem();
            item.setSequenceNumber(seq++);
            item.setProductId(f.getProductId());
            // 若選了零件但快照欄位未填,從零件主檔補齊
            fillSnapshotFromProduct(f);
            item.setInternalPartNumber(trim(f.getInternalPartNumber()));
            item.setCustomerPartNumber(trim(f.getCustomerPartNumber()));
            item.setProductName(trim(f.getProductName()));
            item.setSpecification(trim(f.getSpecification()));
            item.setMaterial(trim(f.getMaterial()));
            item.setSurfaceTreatment(trim(f.getSurfaceTreatment()));
            item.setQuantity(nz(f.getQuantity()));
            item.setUnit(f.getUnit() == null || f.getUnit().isBlank() ? "PCS" : f.getUnit().trim());
            item.setUnitPrice(nz(f.getUnitPrice()));
            item.setDiscountRate(nz(f.getDiscountRate()));
            item.setLeadTime(trim(f.getLeadTime()));
            item.setNotes(trim(f.getNotes()));
            q.addItem(item);
        }
    }

    private void fillSnapshotFromProduct(QuotationItemForm f) {
        if (f.getProductId() == null) {
            return;
        }
        productRepository.findById(f.getProductId()).ifPresent(p -> {
            if (isBlank(f.getInternalPartNumber())) f.setInternalPartNumber(p.getInternalPartNumber());
            if (isBlank(f.getCustomerPartNumber())) f.setCustomerPartNumber(p.getCustomerPartNumber());
            if (isBlank(f.getProductName())) f.setProductName(p.getName());
            if (isBlank(f.getSpecification())) f.setSpecification(p.getSpecification());
            if (isBlank(f.getMaterial())) f.setMaterial(p.getMaterial());
            if (isBlank(f.getSurfaceTreatment())) f.setSurfaceTreatment(p.getSurfaceTreatment());
            if (isBlank(f.getUnit())) f.setUnit(p.getUnit());
            if (f.getUnitPrice() == null) f.setUnitPrice(p.getDefaultUnitPrice());
        });
    }

    /** 複製表頭(不含單號、版本、狀態、時間) */
    private void copyHeader(Quotation src, Quotation dst) {
        dst.setCustomer(src.getCustomer());
        dst.setContact(src.getContact());
        dst.setCustomerInquiryNumber(src.getCustomerInquiryNumber());
        dst.setValidUntil(src.getValidUntil());
        dst.setCurrency(src.getCurrency());
        dst.setTaxType(src.getTaxType());
        dst.setTaxRate(src.getTaxRate());
        dst.setPaymentTerms(src.getPaymentTerms());
        dst.setDeliveryTerms(src.getDeliveryTerms());
        dst.setEstimatedDelivery(src.getEstimatedDelivery());
        dst.setOverallDiscount(src.getOverallDiscount());
        dst.setFreight(src.getFreight());
        dst.setOtherFee(src.getOtherFee());
        dst.setQuotationNotes(src.getQuotationNotes());
        dst.setInternalNotes(src.getInternalNotes());
    }

    /** 複製品項快照 */
    private void copyItems(Quotation src, Quotation dst) {
        for (QuotationItem s : src.getItems()) {
            QuotationItem item = new QuotationItem();
            item.setSequenceNumber(s.getSequenceNumber());
            item.setProductId(s.getProductId());
            item.setInternalPartNumber(s.getInternalPartNumber());
            item.setCustomerPartNumber(s.getCustomerPartNumber());
            item.setProductName(s.getProductName());
            item.setSpecification(s.getSpecification());
            item.setMaterial(s.getMaterial());
            item.setSurfaceTreatment(s.getSurfaceTreatment());
            item.setQuantity(s.getQuantity());
            item.setUnit(s.getUnit());
            item.setUnitPrice(s.getUnitPrice());
            item.setDiscountRate(s.getDiscountRate());
            item.setLeadTime(s.getLeadTime());
            item.setNotes(s.getNotes());
            dst.addItem(item);
        }
    }

    // ===================== Dashboard 統計 =====================

    @Transactional(readOnly = true)
    public long countThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1);
        return quotationRepository.countCreatedBetween(start, end);
    }

    @Transactional(readOnly = true)
    public long countByStatus(QuotationStatus status) {
        return quotationRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Quotation> recent() {
        return quotationRepository.findTop5ByOrderByCreatedAtDesc();
    }

    /** 已收訂(訂金到)的報價,供「收訂中的公司」清單 */
    @Transactional(readOnly = true)
    public List<Quotation> depositQuotations() {
        return quotationRepository.findByStatus(QuotationStatus.ACCEPTED);
    }

    /** 付清尾款(完成)的報價,供成交金額統計 */
    @Transactional(readOnly = true)
    public List<Quotation> paidQuotations() {
        return quotationRepository.findByStatus(QuotationStatus.PAID);
    }

    @Transactional(readOnly = true)
    public List<Quotation> expiringSoon(int days) {
        LocalDate today = LocalDate.now();
        return quotationRepository.findExpiringSoon(today, today.plusDays(days),
                List.of(QuotationStatus.DRAFT, QuotationStatus.SENT, QuotationStatus.CONFIRMING));
    }

    /** 已過期(有效期限已過、仍進行中) */
    @Transactional(readOnly = true)
    public List<Quotation> expired() {
        return quotationRepository.findExpiredOpen(LocalDate.now(),
                List.of(QuotationStatus.DRAFT, QuotationStatus.SENT, QuotationStatus.CONFIRMING));
    }

    /** 送出後尚未回覆(已送出/確認中,最新版) */
    @Transactional(readOnly = true)
    public List<Quotation> awaitingReply() {
        return quotationRepository.findOpenBefore(
                List.of(QuotationStatus.SENT, QuotationStatus.CONFIRMING), LocalDate.now());
    }

    /** 尚未完成(草稿,最新版) */
    @Transactional(readOnly = true)
    public List<Quotation> drafts() {
        return quotationRepository.findOpenBefore(
                List.of(QuotationStatus.DRAFT), LocalDate.now());
    }

    /** 交期將至/已過:交貨日在 7 天內或已過,狀態為已收訂/付清尾款(供首頁「交期」卡) */
    @Transactional(readOnly = true)
    public List<Quotation> deliveryDueSoon() {
        LocalDate today = LocalDate.now();
        return quotationRepository.findDeliveryDueBefore(today.plusDays(7),
                List.of(QuotationStatus.ACCEPTED, QuotationStatus.PAID));
    }

    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
    private String trim(String s) { return s == null ? null : s.trim(); }
    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
