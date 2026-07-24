package com.crm.support;

import java.util.List;

/**
 * 單位標準選項。單位以字串儲存,允許使用者從清單選擇或自行輸入(其他)。
 */
public final class Units {

    public static final List<String> OPTIONS = List.of("PCS", "SET", "KG", "M", "LOT");

    private Units() {
    }
}
