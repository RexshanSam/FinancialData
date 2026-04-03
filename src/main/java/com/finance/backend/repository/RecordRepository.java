package com.finance.backend.repository;

import com.finance.backend.enums.TransactionType;
import com.finance.backend.model.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<FinancialRecord, Long> {

    @Query(value = "SELECT r FROM FinancialRecord r WHERE r.isDeleted = false " +
            "AND (:type IS NULL OR r.type = :type) " +
            "AND (:category IS NULL OR r.category = :category) " +
            "AND (:startDate IS NULL OR r.date >= :startDate) " +
            "AND (:endDate IS NULL OR r.date <= :endDate)",
            countQuery = "SELECT COUNT(r) FROM FinancialRecord r WHERE r.isDeleted = false " +
                    "AND (:type IS NULL OR r.type = :type) " +
                    "AND (:category IS NULL OR r.category = :category) " +
                    "AND (:startDate IS NULL OR r.date >= :startDate) " +
                    "AND (:endDate IS NULL OR r.date <= :endDate)")
    Page<FinancialRecord> findAllWithFilters(
            TransactionType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT SUM(r.amount) FROM FinancialRecord r " +
            "WHERE r.isDeleted = false AND r.type = :type")
    BigDecimal findTotalByType(TransactionType type);

    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r " +
            "WHERE r.isDeleted = false " +
            "GROUP BY r.category " +
            "ORDER BY SUM(r.amount) DESC")
    List<Object[]> findTotalGroupedByCategory();

    @Query(value = "SELECT " +
            "   DATE_FORMAT(r.date, '%Y-%m') as month, " +
            "   SUM(CASE WHEN r.type = 'INCOME' THEN r.amount ELSE 0 END) as income, " +
            "   SUM(CASE WHEN r.type = 'EXPENSE' THEN r.amount ELSE 0 END) as expense " +
            "FROM financial_records r " +
            "WHERE r.is_deleted = false " +
            "AND r.date >= DATE_SUB(CURRENT_DATE, INTERVAL 12 MONTH) " +
            "GROUP BY DATE_FORMAT(r.date, '%Y-%m') " +
            "ORDER BY month DESC", nativeQuery = true)
    List<Object[]> findMonthlyTrends();

    List<FinancialRecord> findTop10ByIsDeletedFalseOrderByDateDesc();

    long countByIsDeletedFalse();
}
