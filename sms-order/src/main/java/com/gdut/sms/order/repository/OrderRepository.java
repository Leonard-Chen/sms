package com.gdut.sms.order.repository;

import com.gdut.sms.common.entity.Order;
import com.gdut.sms.common.entity.Department;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {

    Optional<Order> findByOrderNo(String orderNo);

    List<Order> findAllByDept(Department dept);

    @Override
    @EntityGraph(attributePaths = {"createdBy", "dept"})
    List<Order> findAll(Specification<Order> spec);

    //使用fetch join提前加载createdBy，避免懒加载问题
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.createdBy WHERE o.orderNo = :orderNo")
    Optional<Order> findByOrderNoWithCreatedBy(String orderNo);

    void deleteByOrderNo(String orderNo);

    Long countByOrderStatus(Integer status);

    Long countByOrderStatusAndCreateTimeBetween(Integer status, LocalDateTime from, LocalDateTime to);

    //统计指定时间段的订单总数
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createTime BETWEEN :start AND :end")
    Long countByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    //统计指定时间段的营收
    @Query("SELECT SUM(o.orderAmount) FROM Order o WHERE o.createTime BETWEEN :start AND :end " +
            "AND o.orderStatus = 2")
    BigDecimal sumAmountByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(o.orderAmount) FROM Order o WHERE o.createTime > :time " +
            "AND o.orderStatus = 2")
    BigDecimal sumAmountByCreateTimeAfter(LocalDateTime time);

    @Query("SELECT SUM(o.orderAmount) FROM Order o WHERE o.createTime < :time " +
            "AND o.orderStatus = 2")
    BigDecimal sumAmountByCreateTimeBefore(LocalDateTime time);

    @Query("SELECT SUM(o.orderAmount) FROM Order o WHERE o.orderStatus = 2")
    BigDecimal sumAmount();

    //按月统计订单数和营收
    @Query("SELECT MONTH(o.createTime), COUNT(o), SUM(CASE WHEN o.orderStatus = 2 THEN o.orderAmount ELSE 0 END) " +
            "FROM Order o " +
            "WHERE YEAR(o.createTime) = :year GROUP BY MONTH(o.createTime) ORDER BY MONTH(o.createTime)")
    List<Object[]> getMonthlyStatistics(Integer year);

    //按年统计订单数和营收
    @Query("SELECT COUNT(o), SUM(CASE WHEN o.orderStatus = 2 THEN o.orderAmount ELSE 0 END) " +
            "FROM Order o WHERE YEAR(o.createTime) " +
            "BETWEEN :start AND :end GROUP BY YEAR(o.createTime) ORDER BY YEAR(o.createTime)")
    List<Object[]> getAnnuallyStatistics(Integer start, Integer end);
}
