package com.crm.service;

import com.crm.repository.QuotationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 報價單號產生服務。
 * 格式:QT-YYYYMM-0001,每月流水號自 0001 起,每月重新計算。
 */
@Service
public class QuotationNumberService {

    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyyMM");
    private final QuotationRepository quotationRepository;

    public QuotationNumberService(QuotationRepository quotationRepository) {
        this.quotationRepository = quotationRepository;
    }

    /** 依報價日期產生新的報價單號 */
    public String generate(LocalDate quotationDate) {
        LocalDate date = quotationDate == null ? LocalDate.now() : quotationDate;
        String prefix = "QT-" + date.format(YM) + "-";
        List<String> existing = quotationRepository.findNumbersByPrefix(prefix + "%");
        int max = 0;
        for (String number : existing) {
            int seq = parseSeq(number, prefix);
            if (seq > max) {
                max = seq;
            }
        }
        int next = max + 1;
        return prefix + String.format("%04d", next);
    }

    /** 由報價單號解析末四碼流水號,解析失敗回傳 0 */
    private int parseSeq(String number, String prefix) {
        if (number == null || !number.startsWith(prefix)) {
            return 0;
        }
        try {
            return Integer.parseInt(number.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
