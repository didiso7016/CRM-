package com.crm.repository;

import com.crm.entity.QuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 報價品項資料存取。主要供「產品歷史報價」查詢。
 */
public interface QuotationItemRepository extends JpaRepository<QuotationItem, Long> {

    /**
     * 某顆產品過去所有被報價的紀錄:以來源產品 id 或內部料號比對,
     * 一併載入所屬報價單與客戶,依報價日期新到舊排序。
     */
    @Query("""
            select it from QuotationItem it
            join fetch it.quotation q
            join fetch q.customer c
            where it.productId = :productId
               or (:partNumber <> '' and lower(it.internalPartNumber) = lower(:partNumber))
            order by q.quotationDate desc, q.id desc
            """)
    List<QuotationItem> findPriceHistory(@Param("productId") Long productId,
                                         @Param("partNumber") String partNumber);
}
