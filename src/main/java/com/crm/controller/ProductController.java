package com.crm.controller;

import com.crm.dto.ProductForm;
import com.crm.entity.Product;
import com.crm.service.ProductService;
import com.crm.support.Units;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 零件管理控制器。
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "false") boolean activeOnly,
                       Model model) {
        model.addAttribute("activeMenu", "products");
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeOnly", activeOnly);
        model.addAttribute("products", productService.search(keyword, activeOnly));
        return "products/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        prepareForm(model, null);
        if (!model.containsAttribute("productForm")) {
            model.addAttribute("productForm", new ProductForm());
        }
        return "products/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("productForm") ProductForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (productService.isPartNumberDuplicate(form.getInternalPartNumber(), null)) {
            result.rejectValue("internalPartNumber", "duplicate", "內部料號已存在,請改用其他料號");
        }
        if (result.hasErrors()) {
            prepareForm(model, null);
            return "products/form";
        }
        Product saved = productService.create(form);
        ra.addFlashAttribute("flashSuccess", "零件「" + saved.getName() + "」已建立");
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        prepareForm(model, id);
        if (!model.containsAttribute("productForm")) {
            model.addAttribute("productForm", toForm(productService.getById(id)));
        }
        return "products/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("productForm") ProductForm form,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (productService.isPartNumberDuplicate(form.getInternalPartNumber(), id)) {
            result.rejectValue("internalPartNumber", "duplicate", "內部料號已存在,請改用其他料號");
        }
        if (result.hasErrors()) {
            prepareForm(model, id);
            return "products/form";
        }
        Product saved = productService.update(id, form);
        ra.addFlashAttribute("flashSuccess", "零件「" + saved.getName() + "」已更新");
        return "redirect:/products";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes ra) {
        productService.deactivate(id);
        ra.addFlashAttribute("flashSuccess", "零件已停用");
        return "redirect:/products";
    }

    @PostMapping("/{id}/activate")
    public String activate(@PathVariable Long id, RedirectAttributes ra) {
        productService.activate(id);
        ra.addFlashAttribute("flashSuccess", "零件已重新啟用");
        return "redirect:/products";
    }

    private void prepareForm(Model model, Long editId) {
        model.addAttribute("activeMenu", "products");
        model.addAttribute("unitOptions", Units.OPTIONS);
        model.addAttribute("editId", editId);
    }

    private ProductForm toForm(Product p) {
        ProductForm f = new ProductForm();
        f.setId(p.getId());
        f.setInternalPartNumber(p.getInternalPartNumber());
        f.setCustomerPartNumber(p.getCustomerPartNumber());
        f.setName(p.getName());
        f.setSpecification(p.getSpecification());
        f.setMaterial(p.getMaterial());
        f.setSurfaceTreatment(p.getSurfaceTreatment());
        f.setUnit(p.getUnit());
        f.setDefaultUnitPrice(p.getDefaultUnitPrice());
        f.setMoq(p.getMoq());
        f.setDefaultLeadTimeDays(p.getDefaultLeadTimeDays());
        f.setNotes(p.getNotes());
        f.setActive(p.isActive());
        return f;
    }
}
