package com.crm.service;

import com.crm.entity.Quotation;
import com.crm.entity.QuotationItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 金額計算測試:品項金額、未稅小計、稅額、含稅總計、整體折扣。
 */
class PricingServiceTest {

    private final PricingService pricing = new PricingService();

    private QuotationItem item(String qty, String price, String disc) {
        QuotationItem it = new QuotationItem();
        it.setQuantity(new BigDecimal(qty));
        it.setUnitPrice(new BigDecimal(price));
        it.setDiscountRate(new BigDecimal(disc));
        return it;
    }

    @Test
    void 品項金額_無折扣() {
        BigDecimal amount = pricing.calcItemAmount(new BigDecimal("10"), new BigDecimal("100"), BigDecimal.ZERO);
        assertThat(amount).isEqualByComparingTo("1000.00");
    }

    @Test
    void 品項金額_有折扣() {
        // 10 × 100 ×(1 - 10/100)= 900
        BigDecimal amount = pricing.calcItemAmount(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("10"));
        assertThat(amount).isEqualByComparingTo("900.00");
    }

    @Test
    void 品項金額_四捨五入到兩位() {
        // 3 × 33.333 = 99.999 → 100.00
        BigDecimal amount = pricing.calcItemAmount(new BigDecimal("3"), new BigDecimal("33.333"), BigDecimal.ZERO);
        assertThat(amount).isEqualByComparingTo("100.00");
    }

    @Test
    void 品項金額_null視為零() {
        assertThat(pricing.calcItemAmount(null, null, null)).isEqualByComparingTo("0.00");
    }

    @Test
    void 未稅小計_稅額_含稅總計_與整體折扣() {
        Quotation q = new Quotation();
        q.addItem(item("2", "100", "0"));   // 200.00
        q.addItem(item("1", "50", "10"));   // 45.00
        q.setOverallDiscount(new BigDecimal("45"));
        q.setFreight(new BigDecimal("10"));
        q.setOtherFee(new BigDecimal("5"));
        q.setTaxRate(new BigDecimal("5"));

        pricing.recalculate(q);

        // 未稅小計 = 200 + 45 = 245
        assertThat(q.getSubtotal()).isEqualByComparingTo("245.00");
        // 課稅基礎 = 245 - 45 + 10 + 5 = 215;稅額 = 215 × 5% = 10.75
        assertThat(q.getTaxAmount()).isEqualByComparingTo("10.75");
        // 含稅總計 = 215 + 10.75 = 225.75
        assertThat(q.getTotalAmount()).isEqualByComparingTo("225.75");
    }

    @Test
    void 免稅時稅額為零() {
        Quotation q = new Quotation();
        q.addItem(item("5", "200", "0")); // 1000
        q.setTaxRate(BigDecimal.ZERO);

        pricing.recalculate(q);

        assertThat(q.getSubtotal()).isEqualByComparingTo("1000.00");
        assertThat(q.getTaxAmount()).isEqualByComparingTo("0.00");
        assertThat(q.getTotalAmount()).isEqualByComparingTo("1000.00");
    }
}
