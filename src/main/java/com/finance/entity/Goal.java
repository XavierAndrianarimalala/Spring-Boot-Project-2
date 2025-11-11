package com.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "goals", indexes = {
    @Index(name = "idx_goal_target_date", columnList = "targetDate"),
    @Index(name = "idx_goal_status", columnList = "status"),
    @Index(name = "idx_goal_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false)
    private LocalDate targetDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private GoalPriority priority = GoalPriority.MEDIUM;

    @Column(length = 50)
    private String icon;

    @Column(length = 20)
    private String color;

    // Lier l'objectif à un compte spécifique (optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum GoalStatus {
        IN_PROGRESS,    // En cours
        COMPLETED,      // Atteint
        ABANDONED,      // Abandonné
        PAUSED          // En pause
    }

    public enum GoalPriority {
        LOW,            // Basse
        MEDIUM,         // Moyenne
        HIGH,           // Haute
        CRITICAL        // Critique
    }

    /**
     * Calculate the remaining amount to reach the goal
     */
    public BigDecimal getRemainingAmount() {
        return targetAmount.subtract(currentAmount);
    }

    /**
     * Calculate the percentage of completion
     */
    public BigDecimal getPercentageCompleted() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount
            .multiply(BigDecimal.valueOf(100))
            .divide(targetAmount, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate how many days remaining until target date
     */
    public long getDaysRemaining() {
        return ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }

    /**
     * Calculate suggested monthly savings to reach the goal
     */
    public BigDecimal getSuggestedMonthlySavings() {
        long daysRemaining = getDaysRemaining();
        if (daysRemaining <= 0) {
            return getRemainingAmount();
        }

        BigDecimal remainingAmount = getRemainingAmount();
        long monthsRemaining = Math.max(1, daysRemaining / 30);

        return remainingAmount.divide(
            BigDecimal.valueOf(monthsRemaining),
            2,
            RoundingMode.HALF_UP
        );
    }

    /**
     * Check if the goal is overdue
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(targetDate) && status == GoalStatus.IN_PROGRESS;
    }

    /**
     * Check if the goal is completed
     */
    public boolean isCompleted() {
        return currentAmount.compareTo(targetAmount) >= 0 || status == GoalStatus.COMPLETED;
    }
}
