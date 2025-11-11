package com.finance.repository;

import com.finance.entity.Goal;
import com.finance.entity.Goal.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserId(Long userId);

    List<Goal> findByUserIdAndStatus(Long userId, GoalStatus status);

    List<Goal> findByUserIdOrderByTargetDateAsc(Long userId);

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.status = 'IN_PROGRESS' " +
           "ORDER BY g.priority DESC, g.targetDate ASC")
    List<Goal> findActiveGoalsByUserIdOrderedByPriority(@Param("userId") Long userId);

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.targetDate < :date " +
           "AND g.status = 'IN_PROGRESS'")
    List<Goal> findOverdueGoalsByUserId(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.status = 'IN_PROGRESS' " +
           "AND g.currentAmount >= g.targetAmount")
    List<Goal> findCompletedGoalsNotMarkedByUserId(@Param("userId") Long userId);

    List<Goal> findByAccountId(Long accountId);

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.user.id = :userId AND g.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") GoalStatus status);
}
