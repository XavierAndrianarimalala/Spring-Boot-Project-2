package com.finance.repository;

import com.finance.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(Long userId);

    List<Account> findByUserIdAndActive(Long userId, Boolean active);

    List<Account> findByUserIdAndType(Long userId, Account.AccountType type);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId AND a.active = true")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.name LIKE %:name%")
    List<Account> searchByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);
}
