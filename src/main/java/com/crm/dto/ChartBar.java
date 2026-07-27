package com.crm.dto;

import java.math.BigDecimal;

/**
 * 圖表用的一根長條:標籤、數值、以及相對最大值的百分比(供長條長度)。
 */
public class ChartBar {
    private final String label;
    private final BigDecimal value;
    private final int pct; // 0~100,相對最大值

    public ChartBar(String label, BigDecimal value, int pct) {
        this.label = label;
        this.value = value;
        this.pct = pct;
    }

    public String getLabel() { return label; }
    public BigDecimal getValue() { return value; }
    public int getPct() { return pct; }
}
