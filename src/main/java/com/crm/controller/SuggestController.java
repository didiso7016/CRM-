package com.crm.controller;

import com.crm.dto.CustomerSuggest;
import com.crm.entity.Customer;
import com.crm.service.CustomerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 即時建議(autocomplete)API,回傳 JSON 供搜尋框打字時跳出清單。
 */
@RestController
@RequestMapping("/api/suggest")
public class SuggestController {

    private static final int LIMIT = 8;
    private final CustomerService customerService;

    public SuggestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /** 客戶建議:依關鍵字比對公司名稱/編號/Email/備註/聯絡人,最多回傳 8 筆 */
    @GetMapping("/customers")
    public List<CustomerSuggest> customers(@RequestParam(name = "q", required = false) String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }
        return customerService.search(q.trim(), null, null, PageRequest.of(0, LIMIT)).getContent().stream()
                .map(c -> new CustomerSuggest(c.getId(), c.getCompanyName(), c.getCustomerCode()))
                .toList();
    }
}
