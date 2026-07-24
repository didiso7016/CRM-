package com.crm.repository;

import com.crm.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 聯絡人資料存取。
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /** 依客戶列出聯絡人,主要聯絡人排最前 */
    List<Contact> findByCustomerIdOrderByPrimaryContactDescNameAsc(Long customerId);

    Optional<Contact> findByCustomerIdAndPrimaryContactTrue(Long customerId);

    long countByCustomerId(Long customerId);

    /** 將某客戶所有聯絡人取消主要標記(設定新的主要聯絡人前先清除) */
    @Modifying
    @Query("update Contact c set c.primaryContact = false where c.customer.id = :customerId")
    void clearPrimaryFlag(@Param("customerId") Long customerId);
}
