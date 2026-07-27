package com.crm.service;

import com.crm.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 客戶編號產生服務。
 * 格式:CUS-YYYY-NNN(例:CUS-2026-001),流水號每年重新自 001 起算。
 * (依客戶需求確認表所填格式範例「CUS-2026-001」)
 */
@Service
public class CustomerNumberService {

    private static final String PREFIX = "CUS-";
    private final CustomerRepository customerRepository;

    public CustomerNumberService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /** 依當前年度產生新的客戶編號 */
    public String generate() {
        return generate(LocalDate.now().getYear());
    }

    /** 依指定年度產生新的客戶編號:CUS-{year}-{三碼流水號} */
    public String generate(int year) {
        String prefix = PREFIX + year + "-";
        List<String> existing = customerRepository.findCodesByPrefix(prefix + "%");
        int max = 0;
        for (String code : existing) {
            int seq = parseSeq(code, prefix);
            if (seq > max) {
                max = seq;
            }
        }
        return prefix + String.format("%03d", max + 1);
    }

    /** 由編號解析末段流水號,解析失敗回傳 0 */
    private int parseSeq(String code, String prefix) {
        if (code == null || !code.startsWith(prefix)) {
            return 0;
        }
        try {
            return Integer.parseInt(code.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
