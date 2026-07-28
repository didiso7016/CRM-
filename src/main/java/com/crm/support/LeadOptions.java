package com.crm.support;

import java.util.List;

/**
 * 客戶開發(Lead)模組的下拉選項。值以業主提供者為準。
 */
public final class LeadOptions {

    /** 客戶類型 */
    public static final List<String> LEAD_TYPES = List.of(
            "水處理設備商／工程兼經銷",
            "泵浦與污水設備通路",
            "鼓風機／曝氣設備通路");

    /** 優先級 */
    public static final List<String> PRIORITIES = List.of("A", "B", "C");

    /** 快速成交 */
    public static final List<String> FAST_DEALS = List.of("高", "中", "低");

    /** 品牌衝突 */
    public static final List<String> BRAND_CONFLICTS = List.of(
            "中:已有台灣／美國散氣盤來源;以現貨與規格比較切入",
            "需查核");

    /** 切入產品 */
    public static final List<String> TARGET_PRODUCTS = List.of(
            "EV-270（補足曝氣系統產品線）",
            "EV-270（特殊風機交叉銷售）",
            "EV-270；EVT-600/1000");

    /** 客製化切入點 */
    public static final List<String> CUSTOM_ANGLES = List.of(
            "以EV-270補足污水處理設備組合;先從小量／樣品開始,不要求大量備庫。",
            "強調台灣現貨、彈性數量與SGS性能測試;可按實際專案需求補貨,降低庫存資金。",
            "散氣器可與鼓風機一起報價;EV-270台灣現貨、可小量補貨,協助處理急單與替換需求。");

    /** 開發進度 */
    public static final List<String> STATUSES = List.of("已寄信", "暫緩", "不適合");

    /** 下一步 */
    public static final List<String> NEXT_STEPS = List.of("查核收件人後寄出第一封信");

    /** 建議主旨預設值 */
    public static final String DEFAULT_SUBJECT = "Add Ready-Stock EV-270 Diffusers to Your Aeration Product Range";

    /** 國家(SE Asia 常用,可自行輸入) */
    public static final List<String> COUNTRIES = List.of(
            "Vietnam", "Thailand", "Indonesia", "Malaysia", "Philippines", "Singapore", "其他");

    private LeadOptions() {
    }
}
