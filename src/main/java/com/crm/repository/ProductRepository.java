package com.crm.repository;

import com.crm.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 零件資料存取。
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByInternalPartNumber(String internalPartNumber);

    Optional<Product> findByInternalPartNumber(String internalPartNumber);

    /** 依料號(內部/客戶)或名稱搜尋 */
    @Query("""
            select p from Product p
            where (:keyword is null or :keyword = ''
                   or lower(p.internalPartNumber) like lower(concat('%', :keyword, '%'))
                   or lower(p.customerPartNumber) like lower(concat('%', :keyword, '%'))
                   or lower(p.name) like lower(concat('%', :keyword, '%')))
              and (:onlyActive = false or p.active = true)
            order by p.updatedAt desc
            """)
    List<Product> search(@Param("keyword") String keyword,
                         @Param("onlyActive") boolean onlyActive);

    /** 報價單選擇零件時,只列出啟用中的 */
    List<Product> findByActiveTrueOrderByInternalPartNumberAsc();
}
