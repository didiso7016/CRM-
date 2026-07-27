package com.crm.support;

import java.util.List;

/**
 * 幣別標準選項。報價單、公司設定、客戶預設共用同一份清單。
 */
public final class Currencies {

    public static final List<String> OPTIONS = List.of("USD", "TWD", "CNY", "EUR", "JPY", "GBP");

    private Currencies() {
    }
}
