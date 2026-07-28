package com.crm.repository;

import com.crm.entity.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 客戶開發(Lead)資料存取。
 */
public interface LeadRepository extends JpaRepository<Lead, Long> {

    /**
     * 綜合搜尋:關鍵字(公司名稱/城市/切入產品),另可依 開發進度、優先級 篩選。
     * 條件為 null/空 時忽略。依評分高到低排序。
     */
    @Query(value = """
            select l from Lead l
            where l.active = true
              and (:keyword is null or :keyword = ''
                   or lower(l.companyName) like lower(concat('%', :keyword, '%'))
                   or lower(l.city) like lower(concat('%', :keyword, '%'))
                   or lower(l.targetProduct) like lower(concat('%', :keyword, '%')))
              and (:status is null or :status = '' or l.status = :status)
              and (:priority is null or :priority = '' or l.priority = :priority)
            order by (case when l.score is null then -1 else l.score end) desc, l.updatedAt desc
            """,
            countQuery = """
            select count(l) from Lead l
            where l.active = true
              and (:keyword is null or :keyword = ''
                   or lower(l.companyName) like lower(concat('%', :keyword, '%'))
                   or lower(l.city) like lower(concat('%', :keyword, '%'))
                   or lower(l.targetProduct) like lower(concat('%', :keyword, '%')))
              and (:status is null or :status = '' or l.status = :status)
              and (:priority is null or :priority = '' or l.priority = :priority)
            """)
    Page<Lead> search(@Param("keyword") String keyword,
                      @Param("status") String status,
                      @Param("priority") String priority,
                      Pageable pageable);
}
