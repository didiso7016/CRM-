package com.crm.dto;

/**
 * 客戶即時建議(autocomplete)用的精簡資料。
 */
public record CustomerSuggest(Long id, String companyName, String customerCode) {
}
