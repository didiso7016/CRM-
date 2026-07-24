package com.crm.repository;

import com.crm.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 客戶資料存取。全部以 Spring Data 參數化查詢,無手拼 SQL。
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByCustomerCode(String customerCode);

    Optional<Customer> findByCustomerCode(String customerCode);

    /**
     * 綜合搜尋:可依公司名稱、客戶編號或聯絡人姓名模糊比對。
     * onlyActive 為 true 時只回傳啟用中的客戶。
     */
    @Query("""
            select distinct c from Customer c
            left join Contact ct on ct.customer = c
            where (:keyword is null or :keyword = ''
                   or lower(c.companyName) like lower(concat('%', :keyword, '%'))
                   or lower(c.customerCode) like lower(concat('%', :keyword, '%'))
                   or lower(ct.name) like lower(concat('%', :keyword, '%')))
              and (:onlyActive = false or c.active = true)
            order by c.updatedAt desc
            """)
    List<Customer> search(@Param("keyword") String keyword,
                          @Param("onlyActive") boolean onlyActive);

    /** 最近更新的客戶(首頁用) */
    List<Customer> findTop5ByOrderByUpdatedAtDesc();

    long countByActiveTrue();

    /**
     * 需要關懷提醒的客戶:啟用中,且從未聯絡或最後聯絡時間早於指定門檻。
     */
    @Query("""
            select c from Customer c
            where c.active = true
              and (c.lastContactedAt is null or c.lastContactedAt < :threshold)
            order by c.lastContactedAt asc nulls first
            """)
    List<Customer> findNeedFollowUp(@Param("threshold") LocalDateTime threshold);
}
