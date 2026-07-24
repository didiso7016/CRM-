package com.crm.service;

import com.crm.entity.Quotation;
import com.crm.entity.QuotationItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金額計算服務。所有金額一律使用 BigDecimal,統一集中於此,避免浮點誤差。
 *
 * 計算規則:
 *   品項金額  = 數量 × 單價 ×(1 - 折扣% ÷ 100)
 *   未稅小計  = 所有品項金額合計
 *   稅額      =(未稅小計 - 整體折扣 + 運費 + 其他費用)× 稅率 ÷ 100
 *   含稅總計  = 未稅小計 - 整體折扣 + 運費 + 其他費用 + 稅額
 */
@Service
public class PricingService {

    /** 金額小數位數(元 / 分) */
    public static final int MONEY_SCALE = 2;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    /** 計算單筆品項金額 */
    public BigDecimal calcItemAmount(BigDecimal quantity, BigDecimal unitPrice, BigDecimal discountRate) {
        BigDecimal qty = nz(quantity);
        BigDecimal price = nz(unitPrice);
        BigDecimal disc = nz(discountRate);
        // 折扣係數 = (100 - 折扣%) / 100
        BigDecimal factor = HUNDRED.subtract(disc).divide(HUNDRED, 10, RoundingMode.HALF_UP);
        BigDecimal amount = qty.multiply(price).multiply(factor);
        return amount.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 重新計算整張報價單的所有金額,並寫回 Entity。
     * 後端一律以此為準,不信任前端傳入的金額。
     */
    public void recalculate(Quotation q) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (QuotationItem item : q.getItems()) {
            BigDecimal amount = calcItemAmount(item.getQuantity(), item.getUnitPrice(), item.getDiscountRate());
            item.setAmount(amount);
            subtotal = subtotal.add(amount);
        }
        subtotal = subtotal.setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        BigDecimal discount = nz(q.getOverallDiscount());
        BigDecimal freight = nz(q.getFreight());
        BigDecimal otherFee = nz(q.getOtherFee());
        BigDecimal taxRate = nz(q.getTaxRate());

        // 課稅基礎 = 未稅小計 - 整體折扣 + 運費 + 其他費用
        BigDecimal taxable = subtotal.subtract(discount).add(freight).add(otherFee);
        BigDecimal taxAmount = taxable.multiply(taxRate).divide(HUNDRED, MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal total = taxable.add(taxAmount).setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        q.setSubtotal(subtotal);
        q.setTaxAmount(taxAmount);
        q.setTotalAmount(total);
    }

    /** null 視為 0 */
    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
