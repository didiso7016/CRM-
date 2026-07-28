package com.crm.dto;

import java.util.List;

/**
 * 產品歷史報價彙整:完整明細 + 最近一次 / 最低 / 已成交摘要。
 */
public record ProductPriceHistory(List<PriceHistoryEntry> entries,
                                  PriceHistoryEntry latest,
                                  PriceHistoryEntry lowest,
                                  List<PriceHistoryEntry> accepted) {

    public boolean isEmpty() {
        return entries == null || entries.isEmpty();
    }
}
