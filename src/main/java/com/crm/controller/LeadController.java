package com.crm.controller;

import com.crm.dto.LeadForm;
import com.crm.entity.Customer;
import com.crm.entity.Lead;
import com.crm.service.LeadService;
import com.crm.support.LeadOptions;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 客戶開發 / 潛在客戶控制器。
 */
@Controller
@RequestMapping("/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    /** 開發清單 + 搜尋 + 分頁 */
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String priority,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        model.addAttribute("activeMenu", "leads");
        var result = leadService.search(keyword, status, priority, PageRequest.of(page, size));
        model.addAttribute("leads", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("pageSize", size);
        model.addAttribute("fKeyword", keyword);
        model.addAttribute("fStatus", status);
        model.addAttribute("fPriority", priority);
        addOptions(model);
        return "leads/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("activeMenu", "leads");
        if (!model.containsAttribute("leadForm")) {
            LeadForm form = new LeadForm();
            form.setSuggestedSubject(LeadOptions.DEFAULT_SUBJECT);
            model.addAttribute("leadForm", form);
        }
        addOptions(model);
        return "leads/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("leadForm") LeadForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "leads");
            addOptions(model);
            return "leads/form";
        }
        Lead saved = leadService.create(form);
        ra.addFlashAttribute("flashSuccess", "已新增潛在客戶「" + saved.getCompanyName() + "」");
        return "redirect:/leads/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "leads");
        model.addAttribute("lead", leadService.getById(id));
        return "leads/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "leads");
        if (!model.containsAttribute("leadForm")) {
            model.addAttribute("leadForm", toForm(leadService.getById(id)));
        }
        model.addAttribute("editId", id);
        addOptions(model);
        return "leads/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("leadForm") LeadForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "leads");
            model.addAttribute("editId", id);
            addOptions(model);
            return "leads/form";
        }
        Lead saved = leadService.update(id, form);
        ra.addFlashAttribute("flashSuccess", "已更新「" + saved.getCompanyName() + "」");
        return "redirect:/leads/" + id;
    }

    /** 轉為正式客戶 */
    @PostMapping("/{id}/convert")
    public String convert(@PathVariable Long id, RedirectAttributes ra) {
        Customer c = leadService.convertToCustomer(id);
        ra.addFlashAttribute("flashSuccess", "已轉為正式客戶「" + c.getCompanyName() + "」(編號 " + c.getCustomerCode() + ")");
        return "redirect:/customers/" + c.getId();
    }

    /** 停用(從清單移除) */
    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes ra) {
        leadService.deactivate(id);
        ra.addFlashAttribute("flashSuccess", "已移除此開發資料");
        return "redirect:/leads";
    }

    private void addOptions(Model model) {
        model.addAttribute("leadTypes", LeadOptions.LEAD_TYPES);
        model.addAttribute("priorities", LeadOptions.PRIORITIES);
        model.addAttribute("fastDeals", LeadOptions.FAST_DEALS);
        model.addAttribute("brandConflicts", LeadOptions.BRAND_CONFLICTS);
        model.addAttribute("targetProducts", LeadOptions.TARGET_PRODUCTS);
        model.addAttribute("customAngles", LeadOptions.CUSTOM_ANGLES);
        model.addAttribute("statuses", LeadOptions.STATUSES);
        model.addAttribute("nextSteps", LeadOptions.NEXT_STEPS);
        model.addAttribute("countries", LeadOptions.COUNTRIES);
    }

    private LeadForm toForm(Lead l) {
        LeadForm f = new LeadForm();
        f.setId(l.getId());
        f.setCompanyName(l.getCompanyName());
        f.setCountry(l.getCountry());
        f.setCity(l.getCity());
        f.setBusinessType(l.getBusinessType());
        f.setWebsite(l.getWebsite());
        f.setEmail(l.getEmail());
        f.setPhoneZalo(l.getPhoneZalo());
        f.setContactRoute(l.getContactRoute());
        f.setSourceUrl(l.getSourceUrl());
        f.setLeadType(l.getLeadType());
        f.setScore(l.getScore());
        f.setPriority(l.getPriority());
        f.setFastDeal(l.getFastDeal());
        f.setVerification(l.getVerification());
        f.setBrandConflict(l.getBrandConflict());
        f.setTargetProduct(l.getTargetProduct());
        f.setCustomAngle(l.getCustomAngle());
        f.setSuggestedSubject(l.getSuggestedSubject());
        f.setStatus(l.getStatus());
        f.setFirstContactDate(l.getFirstContactDate());
        f.setLatestContactDate(l.getLatestContactDate());
        f.setNextStep(l.getNextStep());
        f.setNotes(l.getNotes());
        f.setActive(l.isActive());
        return f;
    }
}
