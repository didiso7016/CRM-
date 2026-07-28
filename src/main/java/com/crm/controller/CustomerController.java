package com.crm.controller;

import com.crm.dto.CustomerForm;
import com.crm.entity.Customer;
import com.crm.enums.CustomerType;
import com.crm.service.CompanySettingsService;
import com.crm.service.ContactLogService;
import com.crm.service.ContactService;
import com.crm.service.CustomerNumberService;
import com.crm.service.CustomerService;
import com.crm.service.QuotationService;
import com.crm.support.Currencies;
import com.crm.support.DeliveryTerms;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 客戶管理控制器。僅負責流程控制與畫面,商業邏輯委由 Service。
 */
@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final ContactService contactService;
    private final QuotationService quotationService;
    private final ContactLogService contactLogService;
    private final CustomerNumberService customerNumberService;
    private final CompanySettingsService companySettingsService;

    public CustomerController(CustomerService customerService, ContactService contactService,
                             QuotationService quotationService, ContactLogService contactLogService,
                             CustomerNumberService customerNumberService,
                             CompanySettingsService companySettingsService) {
        this.customerService = customerService;
        this.contactService = contactService;
        this.quotationService = quotationService;
        this.contactLogService = contactLogService;
        this.customerNumberService = customerNumberService;
        this.companySettingsService = companySettingsService;
    }

    /** 客戶列表 + 搜尋 + 分頁 */
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) CustomerType type,
                       @RequestParam(required = false) String status,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        // status:空=全部、active=啟用中、inactive=已停用
        Boolean active = "active".equals(status) ? Boolean.TRUE
                : ("inactive".equals(status) ? Boolean.FALSE : null);
        model.addAttribute("activeMenu", "customers");
        model.addAttribute("keyword", keyword);
        model.addAttribute("fType", type);
        model.addAttribute("fStatus", status);
        model.addAttribute("customerTypes", CustomerType.values());
        var result = customerService.search(keyword, type, active,
                org.springframework.data.domain.PageRequest.of(page, size));
        model.addAttribute("customers", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("pageSize", size);
        return "customers/list";
    }

    /** 新增客戶表單 */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("activeMenu", "customers");
        if (!model.containsAttribute("customerForm")) {
            CustomerForm form = new CustomerForm();
            // 預覽即將自動產生的客戶編號(實際值以儲存當下為準)
            form.setCustomerCode(customerNumberService.generate());
            model.addAttribute("customerForm", form);
        }
        model.addAttribute("customerTypes", CustomerType.values());
        addTransactionOptions(model);
        return "customers/form";
    }

    /** 建立客戶 */
    @PostMapping
    public String create(@Valid @ModelAttribute("customerForm") CustomerForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        // 使用者可自行填寫編號;若有填且與他人重複則擋下(留空則由系統自動產生)
        if (form.getCustomerCode() != null && !form.getCustomerCode().isBlank()
                && customerService.isCodeDuplicate(form.getCustomerCode(), null)) {
            result.rejectValue("customerCode", "duplicate", "客戶編號已存在,請改用其他編號");
        }
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "customers");
            model.addAttribute("customerTypes", CustomerType.values());
            addTransactionOptions(model);
            return "customers/form";
        }
        Customer saved = customerService.create(form);
        ra.addFlashAttribute("flashSuccess", "客戶「" + saved.getCompanyName() + "」已建立");
        return "redirect:/customers/" + saved.getId();
    }

    /** 客戶詳細資料(含聯絡人) */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "customers");
        model.addAttribute("customer", customerService.getById(id));
        model.addAttribute("contacts", contactService.listByCustomer(id));
        model.addAttribute("quotationHistory", quotationService.historyByCustomer(id));
        model.addAttribute("contactLogs", contactLogService.listByCustomer(id));
        Integer rd = companySettingsService.getOrCreate().getContactReminderDays();
        model.addAttribute("reminderDays", rd == null ? 30 : rd);
        return "customers/detail";
    }

    /** 編輯客戶表單 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "customers");
        if (!model.containsAttribute("customerForm")) {
            model.addAttribute("customerForm", toForm(customerService.getById(id)));
        }
        model.addAttribute("customerTypes", CustomerType.values());
        model.addAttribute("editId", id);
        addTransactionOptions(model);
        return "customers/form";
    }

    /** 更新客戶 */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("customerForm") CustomerForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        // 允許修改編號;若改成與其他客戶重複則擋下(排除自己)
        if (form.getCustomerCode() != null && !form.getCustomerCode().isBlank()
                && customerService.isCodeDuplicate(form.getCustomerCode(), id)) {
            result.rejectValue("customerCode", "duplicate", "客戶編號已存在,請改用其他編號");
        }
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "customers");
            model.addAttribute("customerTypes", CustomerType.values());
            model.addAttribute("editId", id);
            addTransactionOptions(model);
            return "customers/form";
        }
        Customer saved = customerService.update(id, form);
        ra.addFlashAttribute("flashSuccess", "客戶「" + saved.getCompanyName() + "」已更新");
        return "redirect:/customers/" + id;
    }

    /** 停用客戶 */
    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes ra) {
        customerService.deactivate(id);
        ra.addFlashAttribute("flashSuccess", "客戶已停用");
        return "redirect:/customers/" + id;
    }

    /** 重新啟用客戶 */
    @PostMapping("/{id}/activate")
    public String activate(@PathVariable Long id, RedirectAttributes ra) {
        customerService.activate(id);
        ra.addFlashAttribute("flashSuccess", "客戶已重新啟用");
        return "redirect:/customers/" + id;
    }

    /** 設定是否納入跟進提醒 */
    @PostMapping("/{id}/follow-up")
    public String setFollowUp(@PathVariable Long id, @RequestParam boolean enabled,
                              @RequestParam(required = false) String redirect, RedirectAttributes ra) {
        customerService.setFollowUp(id, enabled);
        ra.addFlashAttribute("flashSuccess", enabled ? "已納入跟進提醒" : "已停止此客戶的跟進提醒");
        return "redirect:" + backTo(redirect, id);
    }

    /** 延後提醒指定天數 */
    @PostMapping("/{id}/snooze")
    public String snooze(@PathVariable Long id, @RequestParam int days,
                         @RequestParam(required = false) String redirect, RedirectAttributes ra) {
        customerService.snoozeFollowUp(id, days);
        ra.addFlashAttribute("flashSuccess", "已延後 " + days + " 天再提醒");
        return "redirect:" + backTo(redirect, id);
    }

    private String backTo(String redirect, Long id) {
        return (redirect != null && !redirect.isBlank()) ? redirect : "/customers/" + id;
    }

    /** 交易預設下拉選項:與報價單/公司設定共用同一份清單 */
    private void addTransactionOptions(Model model) {
        model.addAttribute("currencyOptions", Currencies.OPTIONS);
        model.addAttribute("deliveryTermsOptions", DeliveryTerms.OPTIONS);
    }

    /** Entity 轉表單(編輯時回填) */
    private CustomerForm toForm(Customer c) {
        CustomerForm f = new CustomerForm();
        f.setId(c.getId());
        f.setCustomerCode(c.getCustomerCode());
        f.setCompanyName(c.getCompanyName());
        f.setTaxId(c.getTaxId());
        f.setPhone(c.getPhone());
        f.setFax(c.getFax());
        f.setEmail(c.getEmail());
        f.setWebsite(c.getWebsite());
        f.setAddress(c.getAddress());
        f.setCountry(c.getCountry());
        f.setCity(c.getCity());
        f.setCustomerType(c.getCustomerType());
        f.setIndustry(c.getIndustry());
        f.setSource(c.getSource());
        f.setDefaultCurrency(c.getDefaultCurrency());
        f.setDefaultPaymentTerms(c.getDefaultPaymentTerms());
        f.setDefaultDeliveryTerms(c.getDefaultDeliveryTerms());
        f.setNotes(c.getNotes());
        f.setActive(c.isActive());
        f.setFollowUpEnabled(c.isFollowUpEnabled());
        return f;
    }
}
