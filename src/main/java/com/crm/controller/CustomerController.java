package com.crm.controller;

import com.crm.dto.CustomerForm;
import com.crm.entity.Customer;
import com.crm.enums.CustomerType;
import com.crm.service.ContactLogService;
import com.crm.service.ContactService;
import com.crm.service.CustomerService;
import com.crm.service.QuotationService;
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

    public CustomerController(CustomerService customerService, ContactService contactService,
                             QuotationService quotationService, ContactLogService contactLogService) {
        this.customerService = customerService;
        this.contactService = contactService;
        this.quotationService = quotationService;
        this.contactLogService = contactLogService;
    }

    /** 客戶列表 + 搜尋 + 分頁 */
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "false") boolean activeOnly,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        model.addAttribute("activeMenu", "customers");
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeOnly", activeOnly);
        var result = customerService.search(keyword, activeOnly,
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
            model.addAttribute("customerForm", new CustomerForm());
        }
        model.addAttribute("customerTypes", CustomerType.values());
        return "customers/form";
    }

    /** 建立客戶 */
    @PostMapping
    public String create(@Valid @ModelAttribute("customerForm") CustomerForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        // 後端重新驗證客戶編號重複
        if (customerService.isCodeDuplicate(form.getCustomerCode(), null)) {
            result.rejectValue("customerCode", "duplicate", "客戶編號已存在,請改用其他編號");
        }
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "customers");
            model.addAttribute("customerTypes", CustomerType.values());
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
        return "customers/form";
    }

    /** 更新客戶 */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("customerForm") CustomerForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (customerService.isCodeDuplicate(form.getCustomerCode(), id)) {
            result.rejectValue("customerCode", "duplicate", "客戶編號已存在,請改用其他編號");
        }
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "customers");
            model.addAttribute("customerTypes", CustomerType.values());
            model.addAttribute("editId", id);
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

    /** 記錄一次聯絡(更新最後聯絡時間,清除未聯絡提醒) */
    @PostMapping("/{id}/record-contact")
    public String recordContact(@PathVariable Long id, RedirectAttributes ra) {
        customerService.recordContact(id);
        ra.addFlashAttribute("flashSuccess", "已記錄本次聯絡時間");
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
        f.setAddress(c.getAddress());
        f.setCountry(c.getCountry());
        f.setCity(c.getCity());
        f.setCustomerType(c.getCustomerType());
        f.setIndustry(c.getIndustry());
        f.setSource(c.getSource());
        f.setNotes(c.getNotes());
        f.setActive(c.isActive());
        f.setFollowUpEnabled(c.isFollowUpEnabled());
        return f;
    }
}
