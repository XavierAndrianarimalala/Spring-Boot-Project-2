package com.finance.repository;

import com.finance.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);

    Page<Transaction> findByCategoryId(Long categoryId, Pageable pageable);

    List<Transaction> findByUserIdAndTransactionDateBetween(
        Long userId, LocalDate startDate, LocalDate endDate
    );

    List<Transaction> findByAccountIdAndTransactionDateBetween(
        Long accountId, LocalDate startDate, LocalDate endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.type = :type")
    List<Transaction> findByUserIdAndDateRangeAndType(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("type") Transaction.TransactionType type
    );

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndTypeAndDateRange(
        @Param("userId") Long userId,
        @Param("type") Transaction.TransactionType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
           "WHERE t.category.id = :categoryId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByCategoryIdAndDateRange(
        @Param("categoryId") Long categoryId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.payee) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Transaction> searchByUserIdAndKeyword(
        @Param("userId") Long userId,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}
