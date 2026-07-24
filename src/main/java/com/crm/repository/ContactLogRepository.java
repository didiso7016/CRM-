package com.crm.repository;

import com.crm.entity.ContactLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 聯絡 / 通知紀錄資料存取。
 */
public interface ContactLogRepository extends JpaRepository<ContactLog, Long> {

    /** 某客戶的聯絡紀錄,新到舊 */
    List<ContactLog> findByCustomerIdOrderByLogDateDescIdDesc(Long customerId);

    /** 最近的聯絡紀錄(通知中心用) */
    @EntityGraph(attributePaths = {"customer"})
    List<ContactLog> findTop15ByOrderByLogDateDescIdDesc();
}
