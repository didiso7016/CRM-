package com.crm.repository;

import com.crm.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 待辦事項資料存取。
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /** 未完成的待辦,到期日近的排前(無到期日排最後) */
    @EntityGraph(attributePaths = {"customer"})
    @Query("select t from Task t where t.done = false order by t.dueDate asc nulls last, t.id asc")
    List<Task> findOpen();

    /** 全部待辦:未完成在前,再依到期日 */
    @EntityGraph(attributePaths = {"customer"})
    @Query("select t from Task t order by t.done asc, t.dueDate asc nulls last, t.id desc")
    List<Task> findAllOrdered();

    /** 某客戶的待辦 */
    @EntityGraph(attributePaths = {"customer"})
    @Query("select t from Task t where t.customer.id = :customerId order by t.done asc, t.dueDate asc nulls last")
    List<Task> findByCustomer(@Param("customerId") Long customerId);

    /** 逾期未完成數量(導覽列徽章、儀表板用) */
    @Query("select count(t) from Task t where t.done = false and t.dueDate is not null and t.dueDate < :today")
    long countOverdue(@Param("today") LocalDate today);

    long countByDoneFalse();
}
