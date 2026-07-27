package com.crm.controller;

import com.crm.dto.ContactOption;
import com.crm.dto.ProductOption;
import com.crm.dto.QuotationForm;
import com.crm.dto.QuotationItemForm;
import com.crm.entity.Contact;
import com.crm.entity.Customer;
import com.crm.entity.Product;
import com.crm.entity.Quotation;
import com.crm.entity.QuotationItem;
import com.crm.enums.QuotationStatus;
import com.crm.repository.ContactRepository;
import com.crm.repository.CustomerRepository;
import com.crm.repository.ProductRepository;
import com.crm.service.CompanySettingsService;
import com.crm.service.CustomerService;
import com.crm.service.QuotationService;
import com.crm.support.DeliveryTerms;
import com.crm.support.Units;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * 報價單管理控制器。
 */
@Controller
@RequestMapping("/quotations")
public class QuotationController {

    private static final List<String> CURRENCY_OPTIONS = List.of("USD", "TWD", "CNY", "EUR", "JPY", "GBP");
    private static final List<String> TAX_TYPE_OPTIONS = List.of("應稅", "免稅", "零稅率");

    private final QuotationService quotationService;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;
    private final CompanySettingsService companySettingsService;

    public QuotationController(QuotationService quotationService,
                              CustomerService customerService,
                              CustomerRepository customerRepository,
                              ContactRepository contactRepository,
                              ProductRepository productRepository,
                              CompanySettingsService companySettingsService) {
        this.quotationService = quotationService;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.contactRepository = contactRepository;
        this.productRepository = productRepository;
        this.companySettingsService = companySettingsService;
    }

    /** 報價單列表 + 多條件搜尋 */
    @GetMapping
    public String list(@RequestParam(required = false) Long customerId,
                       @RequestParam(required = false) String number,
                       @RequestParam(required = false) QuotationStatus status,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        model.addAttribute("activeMenu", "quotations");
        var result = quotationService.search(customerId, number, status, from, to,
                org.springframework.data.domain.PageRequest.of(page, size));
        model.addAttribute("quotations", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("pageSize", size);
        model.addAttribute("statuses", QuotationStatus.values());
        model.addAttribute("customers", customerRepository.findAll());
        // 回填搜尋條件
        model.addAttribute("fCustomerId", customerId);
        model.addAttribute("fNumber", number);
        model.addAttribute("fStatus", status);
        model.addAttribute("fFrom", from);
        model.addAttribute("fTo", to);
        return "quotations/list";
    }

    /** 新增報價單表單 */
    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Long customerId, Model model) {
        prepareFormModel(model, null);
        if (!model.containsAttribute("quotationForm")) {
            QuotationForm form = new QuotationForm();
            form.setCustomerId(customerId);
            // 帶入公司設定的預設值
            var settings = companySettingsService.getOrCreate();
            form.setCurrency(settings.getDefaultCurrency());
            form.setTaxRate(settings.getDefaultTaxRate());
            form.setPaymentTerms(settings.getDefaultPaymentTerms());
            form.setDeliveryTerms(settings.getDefaultDeliveryTerms());
            // 預設一列空白品項
            form.getItems().add(new QuotationItemForm());
            model.addAttribute("quotationForm", form);
        }
        return "quotations/form";
    }

    /** 建立報價單 */
    @PostMapping
    public String create(@Valid @ModelAttribute("quotationForm") QuotationForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        validateAndReport(form, result);
        if (result.hasErrors()) {
            prepareFormModel(model, null);
            return "quotations/form";
        }
        Quotation saved = quotationService.create(form);
        ra.addFlashAttribute("flashSuccess", "報價單 " + saved.getQuotationNumber() + " 已建立");
        return "redirect:/quotations/" + saved.getId();
    }

    /** 報價單詳細資料 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "quotations");
        Quotation q = quotationService.getById(id);
        model.addAttribute("quotation", q);
        model.addAttribute("statuses", QuotationStatus.values());
        model.addAttribute("versions", quotationService.getVersions(q.getQuotationNumber()));
        return "quotations/detail";
    }

    /** 編輯報價單表單 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        prepareFormModel(model, id);
        if (!model.containsAttribute("quotationForm")) {
            model.addAttribute("quotationForm", toForm(quotationService.getById(id)));
        }
        return "quotations/form";
    }

    /** 更新報價單 */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("quotationForm") QuotationForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        validateAndReport(form, result);
        if (result.hasErrors()) {
            prepareFormModel(model, id);
            return "quotations/form";
        }
        quotationService.update(id, form);
        ra.addFlashAttribute("flashSuccess", "報價單已更新");
        return "redirect:/quotations/" + id;
    }

    /** 複製報價單(建立全新單號) */
    @PostMapping("/{id}/copy")
    public String copy(@PathVariable Long id, RedirectAttributes ra) {
        Quotation q = quotationService.copy(id);
        ra.addFlashAttribute("flashSuccess", "已複製為新報價單 " + q.getQuotationNumber() + ",請確認內容後儲存");
        return "redirect:/quotations/" + q.getId() + "/edit";
    }

    /** 建立新版(同單號、版本 +1) */
    @PostMapping("/{id}/new-version")
    public String newVersion(@PathVariable Long id, RedirectAttributes ra) {
        Quotation q = quotationService.newVersion(id);
        ra.addFlashAttribute("flashSuccess",
                "已建立 " + q.getQuotationNumber() + " Ver." + q.getVersion() + ",請修改後儲存");
        return "redirect:/quotations/" + q.getId() + "/edit";
    }

    /** 變更報價狀態 */
    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam QuotationStatus status,
                               RedirectAttributes ra) {
        quotationService.changeStatus(id, status);
        ra.addFlashAttribute("flashSuccess", "報價狀態已更新為「" + status.getLabel() + "」");
        return "redirect:/quotations/" + id;
    }

    /** A4 列印版 */
    @GetMapping("/{id}/print")
    public String print(@PathVariable Long id, Model model) {
        model.addAttribute("quotation", quotationService.getById(id));
        model.addAttribute("company", companySettingsService.getOrCreate());
        return "quotations/print";
    }

    // ===================== 輔助 =====================

    /** 執行業務驗證,將錯誤加入 BindingResult 以便表單顯示 */
    private void validateAndReport(QuotationForm form, BindingResult result) {
        for (String msg : quotationService.validate(form)) {
            result.reject("quotation.invalid", msg);
        }
    }

    /** 準備表單頁所需的下拉與 JS 選項 */
    private void prepareFormModel(Model model, Long editId) {
        model.addAttribute("activeMenu", "quotations");
        model.addAttribute("editId", editId);
        model.addAttribute("activeCustomers",
                customerRepository.findAll().stream()
                        .filter(Customer::isActive).toList());
        model.addAttribute("contactOptions", contactRepository.findAll().stream()
                .map(c -> new ContactOption(c.getId(), c.getName(), c.getCustomer().getId(), c.isPrimaryContact()))
                .toList());
        model.addAttribute("productOptions", productRepository.findByActiveTrueOrderByInternalPartNumberAsc().stream()
                .map(this::toProductOption).toList());
        model.addAttribute("unitOptions", Units.OPTIONS);
        model.addAttribute("currencyOptions", CURRENCY_OPTIONS);
        model.addAttribute("taxTypeOptions", TAX_TYPE_OPTIONS);
        model.addAttribute("deliveryTermsOptions", DeliveryTerms.OPTIONS);
    }

    private ProductOption toProductOption(Product p) {
        String leadTime = p.getDefaultLeadTimeDays() == null ? null : p.getDefaultLeadTimeDays() + " 天";
        return new ProductOption(p.getId(), p.getInternalPartNumber(), p.getCustomerPartNumber(),
                p.getName(), p.getSpecification(), p.getMaterial(), p.getSurfaceTreatment(),
                p.getUnit(), p.getDefaultUnitPrice(), leadTime);
    }

    /** Entity 轉表單(編輯回填) */
    private QuotationForm toForm(Quotation q) {
        QuotationForm f = new QuotationForm();
        f.setId(q.getId());
        f.setCustomerId(q.getCustomer().getId());
        f.setContactId(q.getContact() != null ? q.getContact().getId() : null);
        f.setQuotationDate(q.getQuotationDate());
        f.setCustomerInquiryNumber(q.getCustomerInquiryNumber());
        f.setValidUntil(q.getValidUntil());
        f.setCurrency(q.getCurrency());
        f.setTaxType(q.getTaxType());
        f.setTaxRate(q.getTaxRate());
        f.setPaymentTerms(q.getPaymentTerms());
        f.setDeliveryTerms(q.getDeliveryTerms());
        f.setEstimatedDelivery(q.getEstimatedDelivery());
        f.setOverallDiscount(q.getOverallDiscount());
        f.setFreight(q.getFreight());
        f.setOtherFee(q.getOtherFee());
        f.setQuotationNotes(q.getQuotationNotes());
        f.setInternalNotes(q.getInternalNotes());
        for (QuotationItem item : q.getItems()) {
            QuotationItemForm itf = new QuotationItemForm();
            itf.setProductId(item.getProductId());
            itf.setInternalPartNumber(item.getInternalPartNumber());
            itf.setCustomerPartNumber(item.getCustomerPartNumber());
            itf.setProductName(item.getProductName());
            itf.setSpecification(item.getSpecification());
            itf.setMaterial(item.getMaterial());
            itf.setSurfaceTreatment(item.getSurfaceTreatment());
            itf.setQuantity(item.getQuantity());
            itf.setUnit(item.getUnit());
            itf.setUnitPrice(item.getUnitPrice());
            itf.setDiscountRate(item.getDiscountRate());
            itf.setLeadTime(item.getLeadTime());
            itf.setNotes(item.getNotes());
            f.getItems().add(itf);
        }
        if (f.getItems().isEmpty()) {
            f.getItems().add(new QuotationItemForm());
        }
        return f;
    }
}
