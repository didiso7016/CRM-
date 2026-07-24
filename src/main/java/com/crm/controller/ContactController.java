package com.crm.controller;

import com.crm.dto.ContactForm;
import com.crm.entity.Contact;
import com.crm.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 聯絡人管理控制器。聯絡人隸屬於客戶,操作後導回客戶詳細頁。
 */
@Controller
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /** 新增聯絡人表單(指定客戶) */
    @GetMapping("/new")
    public String newForm(@RequestParam Long customerId, Model model) {
        model.addAttribute("activeMenu", "customers");
        ContactForm form = new ContactForm();
        form.setCustomerId(customerId);
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", form);
        }
        return "contacts/form";
    }

    /** 編輯聯絡人表單 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "customers");
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", toForm(contactService.getById(id)));
        }
        model.addAttribute("editId", id);
        return "contacts/form";
    }

    /** 建立聯絡人 */
    @PostMapping
    public String create(@Valid @ModelAttribute("contactForm") ContactForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "customers");
            return "contacts/form";
        }
        contactService.create(form);
        ra.addFlashAttribute("flashSuccess", "聯絡人已新增");
        return "redirect:/customers/" + form.getCustomerId();
    }

    /** 更新聯絡人 */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("contactForm") ContactForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "customers");
            model.addAttribute("editId", id);
            return "contacts/form";
        }
        contactService.update(id, form);
        ra.addFlashAttribute("flashSuccess", "聯絡人已更新");
        return "redirect:/customers/" + form.getCustomerId();
    }

    /** 設為主要聯絡人 */
    @PostMapping("/{id}/set-primary")
    public String setPrimary(@PathVariable Long id, @RequestParam Long customerId, RedirectAttributes ra) {
        contactService.setPrimary(id);
        ra.addFlashAttribute("flashSuccess", "已設定為主要聯絡人");
        return "redirect:/customers/" + customerId;
    }

    /** 刪除聯絡人 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam Long customerId, RedirectAttributes ra) {
        contactService.delete(id);
        ra.addFlashAttribute("flashSuccess", "聯絡人已刪除");
        return "redirect:/customers/" + customerId;
    }

    private ContactForm toForm(Contact c) {
        ContactForm f = new ContactForm();
        f.setId(c.getId());
        f.setCustomerId(c.getCustomer().getId());
        f.setName(c.getName());
        f.setDepartment(c.getDepartment());
        f.setJobTitle(c.getJobTitle());
        f.setPhone(c.getPhone());
        f.setExtensionNumber(c.getExtensionNumber());
        f.setMobile(c.getMobile());
        f.setEmail(c.getEmail());
        f.setPrimaryContact(c.isPrimaryContact());
        f.setNotes(c.getNotes());
        return f;
    }
}
