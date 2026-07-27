package com.crm.support;

import java.util.List;

/**
 * 交貨條件標準選項(Incoterms)。公司主要使用這兩種。
 */
public final class DeliveryTerms {

    public static final List<String> OPTIONS = List.of("FOB Keelung", "EXW");

    private DeliveryTerms() {
    }
}
