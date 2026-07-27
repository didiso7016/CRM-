package com.crm.dto;

/**
 * 傳給報價單表單 JS 的客戶交易預設值。
 * 選定客戶時,自動帶入其預設幣別 / 付款條件 / 交貨條件。
 */
public record CustomerDefaultsOption(Long id, String currency, String paymentTerms, String deliveryTerms) {
}
