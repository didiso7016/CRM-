package com.crm.service;

import com.crm.dto.ProductForm;
import com.crm.entity.Product;
import com.crm.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 零件商業邏輯:新增/編輯/停用、料號搜尋與內部料號重複檢查。
 */
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> search(String keyword, boolean onlyActive) {
        return productRepository.search(keyword == null ? "" : keyword.trim(), onlyActive);
    }

    @Transactional(readOnly = true)
    public List<Product> activeProducts() {
        return productRepository.findByActiveTrueOrderByInternalPartNumberAsc();
    }

    @Transactional(readOnly = true)
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到零件(id=" + id + ")"));
    }

    @Transactional(readOnly = true)
    public boolean isPartNumberDuplicate(String internalPartNumber, Long excludeId) {
        if (internalPartNumber == null || internalPartNumber.isBlank()) {
            return false;
        }
        return productRepository.findByInternalPartNumber(internalPartNumber.trim())
                .filter(p -> !p.getId().equals(excludeId))
                .isPresent();
    }

    public Product create(ProductForm form) {
        Product p = new Product();
        apply(form, p);
        return productRepository.save(p);
    }

    public Product update(Long id, ProductForm form) {
        Product p = getById(id);
        apply(form, p);
        return productRepository.save(p);
    }

    public void deactivate(Long id) {
        Product p = getById(id);
        p.setActive(false);
        productRepository.save(p);
    }

    public void activate(Long id) {
        Product p = getById(id);
        p.setActive(true);
        productRepository.save(p);
    }

    private void apply(ProductForm form, Product p) {
        p.setInternalPartNumber(trim(form.getInternalPartNumber()));
        p.setCustomerPartNumber(trim(form.getCustomerPartNumber()));
        p.setName(trim(form.getName()));
        p.setSpecification(trim(form.getSpecification()));
        p.setMaterial(trim(form.getMaterial()));
        p.setSurfaceTreatment(trim(form.getSurfaceTreatment()));
        p.setUnit(form.getUnit() == null || form.getUnit().isBlank() ? "PCS" : form.getUnit().trim());
        p.setDefaultUnitPrice(form.getDefaultUnitPrice() == null ? BigDecimal.ZERO : form.getDefaultUnitPrice());
        p.setMoq(form.getMoq());
        p.setDefaultLeadTimeDays(form.getDefaultLeadTimeDays());
        p.setNotes(form.getNotes());
        p.setActive(form.isActive());
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
