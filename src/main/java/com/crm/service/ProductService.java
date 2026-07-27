package com.crm.service;

import com.crm.dto.PriceHistoryEntry;
import com.crm.dto.ProductForm;
import com.crm.dto.ProductPriceHistory;
import com.crm.entity.Product;
import com.crm.entity.Quotation;
import com.crm.entity.QuotationItem;
import com.crm.enums.QuotationStatus;
import com.crm.repository.ProductRepository;
import com.crm.repository.QuotationItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * 零件商業邏輯:新增/編輯/停用、料號搜尋、內部料號重複檢查與歷史報價查詢。
 */
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final QuotationItemRepository quotationItemRepository;

    public ProductService(ProductRepository productRepository,
                          QuotationItemRepository quotationItemRepository) {
        this.productRepository = productRepository;
        this.quotationItemRepository = quotationItemRepository;
    }

    /** 某零件的歷史報價:完整明細 + 最近一次 / 最低 / 已成交摘要 */
    @Transactional(readOnly = true)
    public ProductPriceHistory priceHistory(Product product) {
        List<QuotationItem> items = quotationItemRepository.findPriceHistory(
                product.getId(),
                product.getInternalPartNumber() == null ? "" : product.getInternalPartNumber());

        List<PriceHistoryEntry> entries = items.stream().map(it -> {
            Quotation q = it.getQuotation();
            return new PriceHistoryEntry(q.getId(), q.getQuotationNumber(), q.getVersion(),
                    q.getCustomer().getCompanyName(), q.getQuotationDate(), it.getQuantity(), it.getUnit(),
                    it.getUnitPrice(), q.getCurrency(), q.getStatus());
        }).toList();

        PriceHistoryEntry latest = entries.isEmpty() ? null : entries.get(0); // 已依日期新到舊
        PriceHistoryEntry lowest = entries.stream()
                .filter(e -> e.unitPrice() != null && e.unitPrice().signum() > 0)
                .min(Comparator.comparing(PriceHistoryEntry::unitPrice))
                .orElse(null);
        List<PriceHistoryEntry> accepted = entries.stream()
                .filter(e -> e.status() == QuotationStatus.ACCEPTED || e.status() == QuotationStatus.PAID)
                .toList();

        return new ProductPriceHistory(entries, latest, lowest, accepted);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Product> search(String keyword, boolean onlyActive,
                                                                org.springframework.data.domain.Pageable pageable) {
        return productRepository.search(keyword == null ? "" : keyword.trim(), onlyActive, pageable);
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
