package com.crm.service;

import com.crm.dto.QuotationForm;
import com.crm.dto.QuotationItemForm;
import com.crm.entity.Customer;
import com.crm.entity.Quotation;
import com.crm.entity.QuotationItem;
import com.crm.enums.QuotationStatus;
import com.crm.repository.ContactRepository;
import com.crm.repository.CustomerRepository;
import com.crm.repository.ProductRepository;
import com.crm.repository.QuotationRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 報價單商業邏輯測試:驗證(品項不可為空、數量、折扣、有效期限)與建立新版。
 */
class QuotationServiceTest {

    private final QuotationRepository quotationRepo = mock(QuotationRepository.class);
    private final QuotationNumberService numberService = mock(QuotationNumberService.class);
    private final PricingService pricingService = new PricingService();
    private final CustomerRepository customerRepo = mock(CustomerRepository.class);
    private final ContactRepository contactRepo = mock(ContactRepository.class);
    private final ProductRepository productRepo = mock(ProductRepository.class);

    private final QuotationService service = new QuotationService(
            quotationRepo, numberService, pricingService, customerRepo, contactRepo, productRepo);

    private QuotationItemForm itemForm(String qty, String price, String disc) {
        QuotationItemForm f = new QuotationItemForm();
        f.setProductName("測試零件");
        f.setQuantity(qty == null ? null : new BigDecimal(qty));
        f.setUnitPrice(new BigDecimal(price));
        f.setDiscountRate(new BigDecimal(disc));
        return f;
    }

    private QuotationForm baseForm() {
        QuotationForm form = new QuotationForm();
        form.setCustomerId(1L);
        form.setQuotationDate(LocalDate.of(2026, 7, 1));
        form.getItems().add(itemForm("1", "100", "0"));
        return form;
    }

    @Test
    void 品項不可為空() {
        QuotationForm form = new QuotationForm();
        form.setCustomerId(1L);
        form.setQuotationDate(LocalDate.of(2026, 7, 1));
        // 沒有任何品項
        List<String> errors = service.validate(form);
        assertThat(errors).anyMatch(e -> e.contains("至少要有一筆品項"));
    }

    @Test
    void 數量必須大於零() {
        QuotationForm form = baseForm();
        form.getItems().clear();
        form.getItems().add(itemForm("0", "100", "0"));
        List<String> errors = service.validate(form);
        assertThat(errors).anyMatch(e -> e.contains("數量必須大於 0"));
    }

    @Test
    void 折扣必須介於0到100() {
        QuotationForm form = baseForm();
        form.getItems().clear();
        form.getItems().add(itemForm("1", "100", "150"));
        List<String> errors = service.validate(form);
        assertThat(errors).anyMatch(e -> e.contains("折扣必須介於 0 到 100"));
    }

    @Test
    void 有效期限不得早於報價日期() {
        QuotationForm form = baseForm();
        form.setValidUntil(LocalDate.of(2026, 6, 30)); // 早於報價日 7/1
        List<String> errors = service.validate(form);
        assertThat(errors).anyMatch(e -> e.contains("有效期限不得早於報價日期"));
    }

    @Test
    void 正常資料通過驗證() {
        QuotationForm form = baseForm();
        form.setValidUntil(LocalDate.of(2026, 7, 31));
        assertThat(service.validate(form)).isEmpty();
    }

    @Test
    void 建立新版_版本加一且沿用單號並複製品項() {
        Customer customer = new Customer();
        customer.setId(1L);

        Quotation source = new Quotation();
        source.setId(10L);
        source.setQuotationNumber("QT-202607-0001");
        source.setVersion(2);
        source.setStatus(QuotationStatus.ACCEPTED);
        source.setCustomer(customer);
        source.setTaxRate(new BigDecimal("5"));
        QuotationItem it = new QuotationItem();
        it.setSequenceNumber(1);
        it.setProductName("零件A");
        it.setQuantity(new BigDecimal("2"));
        it.setUnitPrice(new BigDecimal("100"));
        it.setDiscountRate(BigDecimal.ZERO);
        source.addItem(it);

        when(quotationRepo.findByIdWithItems(10L)).thenReturn(Optional.of(source));
        when(quotationRepo.findMaxVersion("QT-202607-0001")).thenReturn(2);
        when(quotationRepo.save(any(Quotation.class))).thenAnswer(inv -> inv.getArgument(0));

        Quotation newVer = service.newVersion(10L);

        assertThat(newVer.getQuotationNumber()).isEqualTo("QT-202607-0001");
        assertThat(newVer.getVersion()).isEqualTo(3);
        assertThat(newVer.getStatus()).isEqualTo(QuotationStatus.DRAFT);
        assertThat(newVer.getItems()).hasSize(1);
        // 品項金額與小計已由 PricingService 重新計算:2 × 100 = 200
        assertThat(newVer.getSubtotal()).isEqualByComparingTo("200.00");
        assertThat(newVer.getTotalAmount()).isEqualByComparingTo("210.00"); // 含 5% 稅
    }
}
