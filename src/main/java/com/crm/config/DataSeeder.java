package com.crm.config;

import com.crm.dto.*;
import com.crm.entity.CompanySettings;
import com.crm.entity.Customer;
import com.crm.entity.Quotation;
import com.crm.enums.ContactType;
import com.crm.enums.CustomerType;
import com.crm.enums.QuotationStatus;
import com.crm.repository.CustomerRepository;
import com.crm.service.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 示範資料產生器。僅在啟動參數帶 --crm.seed=true 時執行,且資料庫為空時才灌入,
 * 方便快速看到系統效果。正式使用時不需開啟。
 */
@Component
@ConditionalOnProperty(name = "crm.seed", havingValue = "true")
public class DataSeeder implements ApplicationRunner {

    private final CustomerService customerService;
    private final ContactService contactService;
    private final ProductService productService;
    private final QuotationService quotationService;
    private final ContactLogService contactLogService;
    private final CompanySettingsService settingsService;
    private final CustomerRepository customerRepository;

    public DataSeeder(CustomerService customerService, ContactService contactService,
                      ProductService productService, QuotationService quotationService,
                      ContactLogService contactLogService, CompanySettingsService settingsService,
                      CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.contactService = contactService;
        this.productService = productService;
        this.quotationService = quotationService;
        this.contactLogService = contactLogService;
        this.settingsService = settingsService;
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (customerRepository.count() > 0) {
            return; // 已有資料,不重複灌入
        }

        // ---- 公司設定:未聯絡提醒 7 天、報價跟進 14 天 ----
        CompanySettings s = settingsService.getOrCreate();
        s.setCompanyName("鼎新精密工業股份有限公司");
        s.setTaxId("53212345");
        s.setAddress("台中市西屯區工業八路 50 號");
        s.setPhone("04-2358-9999");
        s.setFax("04-2358-9988");
        s.setEmail("sales@dingxin.com.tw");
        s.setContactName("陳志明");
        s.setDefaultCurrency("TWD");
        s.setDefaultTaxRate(new BigDecimal("5"));
        s.setDefaultPaymentTerms("月結 30 天");
        s.setDefaultDeliveryTerms("工廠交貨");
        s.setContactReminderDays(7);
        s.setQuotationFollowupDays(14);
        settingsService.save(s);

        // ---- 客戶 ----
        Customer airspec = customerService.create(customer("C001", "Airspec 航太精密", "11223344",
                "03-577-1234", "info@airspec.com", "新竹市科學園區研新一路 8 號", CustomerType.LONG_TERM, "航太", "展覽"));
        Customer hydro = customerService.create(customer("C002", "Hydro System 水力系統", "22334455",
                "07-812-5678", "service@hydrosys.com", "高雄市前鎮區成功二路 25 號", CustomerType.GENERAL, "油壓機械", "老客戶介紹"));
        Customer datong = customerService.create(customer("C003", "大同機械廠", "33445566",
                "04-711-3366", "sales@datong-m.com", "台中市大甲區工業路 100 號", CustomerType.GENERAL, "工具機", "網路"));
        Customer jinggong = customerService.create(customer("C004", "精工五金企業社", "44556677",
                "04-2533-2211", "jg@jinggong.tw", "台中市神岡區中山路 55 號", CustomerType.DEALER, "五金", "展覽"));
        customerService.create(customer("C005", "台灣鋼鐵材料", "55667788",
                "02-2599-8877", "supply@twsteel.com", "新北市五股區五權路 3 號", CustomerType.SUPPLIER, "鋼材", "供應商"));

        // ---- 聯絡人 ----
        contactService.create(contact(airspec.getId(), "王建國", "採購部", "採購經理", "0912-345-678", "wang@airspec.com", true));
        contactService.create(contact(airspec.getId(), "李美玲", "品保部", "工程師", "0922-111-222", "lee@airspec.com", false));
        contactService.create(contact(hydro.getId(), "張文華", "工程部", "專案經理", "0933-444-555", "chang@hydrosys.com", true));
        contactService.create(contact(datong.getId(), "陳大明", "廠務", "廠長", "0955-666-777", "chen@datong-m.com", true));
        contactService.create(contact(jinggong.getId(), "林淑芬", "業務", "負責人", "0966-888-999", null, true));

        // ---- 零件 ----
        productService.create(product("PN-1001", "AS-B08", "六角承窩螺絲 M8x25", "M8x25", "SUS304", "鈍化", "PCS", "3.5", 500, 14));
        productService.create(product("PN-1002", "HY-F50", "不鏽鋼法蘭 DN50", "DN50 PN16", "SUS316", "拋光", "PCS", "185", 50, 21));
        productService.create(product("PN-1003", null, "精密軸承座 SNL-208", "內徑 40mm", "FC250", "烤漆", "SET", "420", 20, 28));
        productService.create(product("PN-1004", null, "CNC 車銑複合加工件", "依圖", "A6061", "陽極", "PCS", "95", 100, 30));
        productService.create(product("PN-1005", "HY-J14", "高壓油壓接頭 1/4", "1/4 NPT", "黃銅", "鍍鎳", "PCS", "68", 200, 10));

        // ---- 報價單(過去日期以觸發跟進提醒) ----
        // Airspec:今天(已接受)
        Quotation q1 = quotationService.create(quotation(airspec.getId(), LocalDate.now(), "500",
                item("PN-1001", "六角承窩螺絲 M8x25", "2000", "PCS", "3.5", "5"),
                item("PN-1004", "CNC 車銑複合加工件", "300", "PCS", "95", "0")));
        quotationService.changeStatus(q1.getId(), QuotationStatus.ACCEPTED);

        // Airspec:20 天前(已送出,未成交)
        Quotation q2 = quotationService.create(quotation(airspec.getId(), LocalDate.now().minusDays(20), "0",
                item("PN-1003", "精密軸承座 SNL-208", "40", "SET", "420", "0")));
        quotationService.changeStatus(q2.getId(), QuotationStatus.SENT);

        // Hydro:16 天前(客戶確認中,未成交)
        Quotation q3 = quotationService.create(quotation(hydro.getId(), LocalDate.now().minusDays(16), "800",
                item("PN-1002", "不鏽鋼法蘭 DN50", "120", "PCS", "185", "8"),
                item("PN-1005", "高壓油壓接頭 1/4", "500", "PCS", "68", "0")));
        quotationService.changeStatus(q3.getId(), QuotationStatus.CONFIRMING);

        // 大同:5 天前(草稿,尚未超過 14 天)
        quotationService.create(quotation(datong.getId(), LocalDate.now().minusDays(5), "0",
                item("PN-1004", "CNC 車銑複合加工件", "1000", "PCS", "92", "3")));

        // ---- 聯絡紀錄(設定各客戶最後聯絡時間) ----
        contactLogService.record(log(airspec.getId(), LocalDate.now().minusDays(1), ContactType.EMAIL, "已寄報價單並確認交期"));
        contactLogService.record(log(hydro.getId(), LocalDate.now().minusDays(10), ContactType.PHONE, "電話追蹤報價,客戶評估中"));
        contactLogService.record(log(datong.getId(), LocalDate.now().minusDays(40), ContactType.MEETING, "拜訪並取得詢價圖面"));
        contactLogService.record(log(jinggong.getId(), LocalDate.now(), ContactType.QUOTE, "寄送型錄與報價"));

        System.out.println(">>> 已灌入示範資料(5 客戶 / 5 聯絡人 / 5 零件 / 4 報價 / 4 聯絡紀錄)");
    }

    // ===== 建構表單的小工具 =====

    private CustomerForm customer(String code, String name, String taxId, String phone, String email,
                                  String address, CustomerType type, String industry, String source) {
        CustomerForm f = new CustomerForm();
        f.setCustomerCode(code);
        f.setCompanyName(name);
        f.setTaxId(taxId);
        f.setPhone(phone);
        f.setEmail(email);
        f.setAddress(address);
        f.setCustomerType(type);
        f.setIndustry(industry);
        f.setSource(source);
        f.setActive(true);
        return f;
    }

    private ContactForm contact(Long customerId, String name, String dept, String title,
                                String mobile, String email, boolean primary) {
        ContactForm f = new ContactForm();
        f.setCustomerId(customerId);
        f.setName(name);
        f.setDepartment(dept);
        f.setJobTitle(title);
        f.setMobile(mobile);
        f.setEmail(email);
        f.setPrimaryContact(primary);
        return f;
    }

    private ProductForm product(String internalPn, String customerPn, String name, String spec,
                                String material, String surface, String unit, String price, int moq, int lead) {
        ProductForm f = new ProductForm();
        f.setInternalPartNumber(internalPn);
        f.setCustomerPartNumber(customerPn);
        f.setName(name);
        f.setSpecification(spec);
        f.setMaterial(material);
        f.setSurfaceTreatment(surface);
        f.setUnit(unit);
        f.setDefaultUnitPrice(new BigDecimal(price));
        f.setMoq(moq);
        f.setDefaultLeadTimeDays(lead);
        f.setActive(true);
        return f;
    }

    private QuotationItemForm item(String internalPn, String name, String qty, String unit,
                                   String price, String disc) {
        QuotationItemForm f = new QuotationItemForm();
        f.setInternalPartNumber(internalPn);
        f.setProductName(name);
        f.setQuantity(new BigDecimal(qty));
        f.setUnit(unit);
        f.setUnitPrice(new BigDecimal(price));
        f.setDiscountRate(new BigDecimal(disc));
        return f;
    }

    private QuotationForm quotation(Long customerId, LocalDate date, String freight, QuotationItemForm... items) {
        QuotationForm f = new QuotationForm();
        f.setCustomerId(customerId);
        f.setQuotationDate(date);
        f.setValidUntil(date.plusDays(30));
        f.setCurrency("TWD");
        f.setTaxType("應稅");
        f.setTaxRate(new BigDecimal("5"));
        f.setFreight(new BigDecimal(freight));
        f.setPaymentTerms("月結 30 天");
        f.setDeliveryTerms("工廠交貨");
        f.setItems(List.of(items));
        return f;
    }

    private ContactLogForm log(Long customerId, LocalDate date, ContactType type, String note) {
        ContactLogForm f = new ContactLogForm();
        f.setCustomerId(customerId);
        f.setLogDate(date);
        f.setType(type);
        f.setNote(note);
        return f;
    }
}
