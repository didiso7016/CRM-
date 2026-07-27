package com.crm.repository;

import com.crm.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /** 取出符合前綴的客戶編號(供自動產生編號時計算當年度流水號) */
    @Query("select c.customerCode from Customer c where c.customerCode like :prefix")
    List<String> findCodesByPrefix(@Param("prefix") String prefix);

    /**
     * 綜合搜尋:關鍵字可比對 公司名稱 / 客戶編號 / 公司 Email / 備註 / 聯絡人姓名。
     * onlyActive 為 true 時只回傳啟用中的客戶。
     */
    @Query(value = """
            select distinct c from Customer c
            left join Contact ct on ct.customer = c
            where (:keyword is null or :keyword = ''
                   or lower(c.companyName) like lower(concat('%', :keyword, '%'))
                   or lower(c.customerCode) like lower(concat('%', :keyword, '%'))
                   or lower(c.email) like lower(concat('%', :keyword, '%'))
                   or lower(c.notes) like lower(concat('%', :keyword, '%'))
                   or lower(ct.name) like lower(concat('%', :keyword, '%')))
              and (:onlyActive = false or c.active = true)
            order by c.updatedAt desc
            """,
            countQuery = """
            select count(distinct c) from Customer c
            left join Contact ct on ct.customer = c
            where (:keyword is null or :keyword = ''
                   or lower(c.companyName) like lower(concat('%', :keyword, '%'))
                   or lower(c.customerCode) like lower(concat('%', :keyword, '%'))
                   or lower(c.email) like lower(concat('%', :keyword, '%'))
                   or lower(c.notes) like lower(concat('%', :keyword, '%'))
                   or lower(ct.name) like lower(concat('%', :keyword, '%')))
              and (:onlyActive = false or c.active = true)
            """)
    Page<Customer> search(@Param("keyword") String keyword,
                          @Param("onlyActive") boolean onlyActive,
                          Pageable pageable);

    /** 供下拉選單:所有啟用中的客戶(不分頁) */
    List<Customer> findByActiveTrueOrderByCompanyNameAsc();

    /** 最近更新的客戶(首頁用) */
    List<Customer> findTop5ByOrderByUpdatedAtDesc();

    long countByActiveTrue();

    /**
     * 需要關懷提醒的客戶:
     *   - 啟用中
     *   - 已標記「納入跟進提醒」(followUpEnabled = true,預設不提醒)
     *   - 未在延後期間內
     *   - 最後聯絡時間(從未聯絡則以建立時間計)早於門檻
     */
    @Query("""
            select c from Customer c
            where c.active = true
              and c.followUpEnabled = true
              and (
                    (c.nextFollowUpDate is not null and c.nextFollowUpDate <= :today)
                 or (c.nextFollowUpDate is null and coalesce(c.lastContactedAt, c.createdAt) < :threshold)
              )
            order by coalesce(c.lastContactedAt, c.createdAt) asc
            """)
    List<Customer> findNeedFollowUp(@Param("threshold") LocalDateTime threshold,
                                    @Param("today") java.time.LocalDate today);
}
