package com.crm.dto;

import com.crm.enums.QuotationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 零件歷史報價的一筆:某張報價單裡這顆料號被報的單價。
 */
public record PriceHistoryEntry(Long quotationId, String quotationNumber, Integer version,
                                String customerName, LocalDate date, BigDecimal quantity, String unit,
                                BigDecimal unitPrice, String currency, QuotationStatus status) {
}
