package com.crm.repository;

import com.crm.entity.Quotation;
import com.crm.enums.QuotationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 報價單資料存取。
 */
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    /** 讀取單張報價並一併載入品項(避免 open-in-view=false 的延遲載入問題) */
    @EntityGraph(attributePaths = {"items", "customer", "contact"})
    @Query("select q from Quotation q where q.id = :id")
    Optional<Quotation> findByIdWithItems(@Param("id") Long id);

    /** 同一客戶的所有報價(含各版本),供客戶頁歷史查詢 */
    List<Quotation> findByCustomerIdOrderByQuotationNumberDescVersionDesc(Long customerId);

    /** 同一報價單號的所有版本(新到舊),供報價明細頁的版本歷史 */
    @EntityGraph(attributePaths = {"customer"})
    List<Quotation> findByQuotationNumberOrderByVersionDesc(String quotationNumber);

    /** 某狀態且為「最新版本」的報價數量(舊版不計入) */
    @Query("""
            select count(q) from Quotation q
            where q.status in :statuses
              and q.version = (select max(q2.version) from Quotation q2 where q2.quotationNumber = q.quotationNumber)
            """)
    long countByStatusInLatest(@Param("statuses") List<QuotationStatus> statuses);

    /** 取得某報價單號目前最大版本 */
    @Query("select max(q.version) from Quotation q where q.quotationNumber = :number")
    Integer findMaxVersion(@Param("number") String number);

    /** 取得指定月份前綴的所有報價單號(用於產生流水號) */
    @Query("select distinct q.quotationNumber from Quotation q where q.quotationNumber like :prefix")
    List<String> findNumbersByPrefix(@Param("prefix") String prefix);

    /** 本月建立的報價單數量 */
    @Query("select count(q) from Quotation q where q.createdAt >= :start and q.createdAt < :end")
    long countCreatedBetween(@Param("start") java.time.LocalDateTime start,
                             @Param("end") java.time.LocalDateTime end);

    long countByStatus(QuotationStatus status);

    /** 依狀態取回報價(含客戶),供儀表板統計 */
    @EntityGraph(attributePaths = {"customer"})
    List<Quotation> findByStatus(QuotationStatus status);

    /** 最近建立的報價單 */
    @EntityGraph(attributePaths = {"customer"})
    List<Quotation> findTop5ByOrderByCreatedAtDesc();

    /** 即將到期:有效期限介於今日與門檻之間,且仍在進行中的狀態 */
    @EntityGraph(attributePaths = {"customer"})
    @Query("""
            select q from Quotation q
            where q.validUntil is not null
              and q.validUntil >= :today and q.validUntil <= :until
              and q.status in :statuses
            order by q.validUntil asc
            """)
    List<Quotation> findExpiringSoon(@Param("today") LocalDate today,
                                     @Param("until") LocalDate until,
                                     @Param("statuses") List<QuotationStatus> statuses);

    /**
     * 尚在進行中(狀態屬 statuses)且報價日期早於 cutoff 的報價單,
     * 用於「報價後已 N 天未成交」通知。
     */
    @EntityGraph(attributePaths = {"customer"})
    @Query("""
            select q from Quotation q
            where q.status in :statuses and q.quotationDate <= :cutoff
              and q.version = (select max(q2.version) from Quotation q2 where q2.quotationNumber = q.quotationNumber)
            order by q.quotationDate asc
            """)
    List<Quotation> findOpenBefore(@Param("statuses") List<QuotationStatus> statuses,
                                   @Param("cutoff") LocalDate cutoff);

    /** 已過期:有效期限早於今天,且仍在進行中的狀態(最新版) */
    @EntityGraph(attributePaths = {"customer"})
    @Query("""
            select q from Quotation q
            where q.validUntil is not null and q.validUntil < :today
              and q.status in :statuses
              and q.version = (select max(q2.version) from Quotation q2 where q2.quotationNumber = q.quotationNumber)
            order by q.validUntil asc
            """)
    List<Quotation> findExpiredOpen(@Param("today") LocalDate today,
                                    @Param("statuses") List<QuotationStatus> statuses);

    /** 客戶要求回覆日將至/已過:回覆日 <= 門檻,狀態進行中(最新版) */
    @EntityGraph(attributePaths = {"customer"})
    @Query("""
            select q from Quotation q
            where q.customerReplyDueDate is not null and q.customerReplyDueDate <= :threshold
              and q.status in :statuses
              and q.version = (select max(q2.version) from Quotation q2 where q2.quotationNumber = q.quotationNumber)
            order by q.customerReplyDueDate asc
            """)
    List<Quotation> findReplyDueBefore(@Param("threshold") LocalDate threshold,
                                       @Param("statuses") List<QuotationStatus> statuses);

    /** 交期將至/已過:交貨日 <= 門檻,狀態為已收訂/付清尾款(最新版) */
    @EntityGraph(attributePaths = {"customer"})
    @Query("""
            select q from Quotation q
            where q.deliveryDueDate is not null and q.deliveryDueDate <= :threshold
              and q.status in :statuses
              and q.version = (select max(q2.version) from Quotation q2 where q2.quotationNumber = q.quotationNumber)
            order by q.deliveryDueDate asc
            """)
    List<Quotation> findDeliveryDueBefore(@Param("threshold") LocalDate threshold,
                                          @Param("statuses") List<QuotationStatus> statuses);

    /**
     * 報價單搜尋:可依客戶、單號、關鍵字(客戶名稱/編號、料號、品名)、狀態、
     * 日期區間、含稅總計金額區間篩選。條件為 null 時忽略。
     */
    @EntityGraph(attributePaths = {"customer"})
    @Query(value = """
            select distinct q from Quotation q
            left join q.items it
            where (:customerId is null or q.customer.id = :customerId)
              and (:number is null or :number = '' or lower(q.quotationNumber) like lower(concat('%', :number, '%')))
              and (:keyword is null or :keyword = ''
                   or lower(q.customer.companyName) like lower(concat('%', :keyword, '%'))
                   or lower(q.customer.customerCode) like lower(concat('%', :keyword, '%'))
                   or lower(it.internalPartNumber) like lower(concat('%', :keyword, '%'))
                   or lower(it.customerPartNumber) like lower(concat('%', :keyword, '%'))
                   or lower(it.productName) like lower(concat('%', :keyword, '%')))
              and (:status is null or q.status = :status)
              and (:from is null or q.quotationDate >= :from)
              and (:to is null or q.quotationDate <= :to)
              and (:minAmount is null or q.totalAmount >= :minAmount)
              and (:maxAmount is null or q.totalAmount <= :maxAmount)
              and q.version = (select max(q2.version) from Quotation q2 where q2.quotationNumber = q.quotationNumber)
            order by q.createdAt desc
            """,
            countQuery = """
            select count(distinct q) from Quotation q
            left join q.items it
            where (:customerId is null or q.customer.id = :customerId)
              and (:number is null or :number = '' or lower(q.quotationNumber) like lower(concat('%', :number, '%')))
              and (:keyword is null or :keyword = ''
                   or lower(q.customer.companyName) like lower(concat('%', :keyword, '%'))
                   or lower(q.customer.customerCode) like lower(concat('%', :keyword, '%'))
                   or lower(it.internalPartNumber) like lower(concat('%', :keyword, '%'))
                   or lower(it.customerPartNumber) like lower(concat('%', :keyword, '%'))
                   or lower(it.productName) like lower(concat('%', :keyword, '%')))
              and (:status is null or q.status = :status)
              and (:from is null or q.quotationDate >= :from)
              and (:to is null or q.quotationDate <= :to)
              and (:minAmount is null or q.totalAmount >= :minAmount)
              and (:maxAmount is null or q.totalAmount <= :maxAmount)
              and q.version = (select max(q2.version) from Quotation q2 where q2.quotationNumber = q.quotationNumber)
            """)
    Page<Quotation> search(@Param("customerId") Long customerId,
                           @Param("number") String number,
                           @Param("keyword") String keyword,
                           @Param("status") QuotationStatus status,
                           @Param("from") LocalDate from,
                           @Param("to") LocalDate to,
                           @Param("minAmount") java.math.BigDecimal minAmount,
                           @Param("maxAmount") java.math.BigDecimal maxAmount,
                           Pageable pageable);
}
