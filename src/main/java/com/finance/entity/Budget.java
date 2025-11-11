package com.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budgets", indexes = {
    @Index(name = "idx_budget_period", columnList = "startDate, endDate"),
    @Index(name = "idx_budget_category", columnList = "category_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal spent = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BudgetPeriod period;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 500)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean active = true;

    // Alerte quand le pourcentage est atteint
    @Column(precision = 5, scale = 2)
    private BigDecimal alertThreshold = new BigDecimal("80.00");

    public enum BudgetPeriod {
        WEEKLY,      // Hebdomadaire
        MONTHLY,     // Mensuel
        QUARTERLY,   // Trimestriel
        YEARLY,      // Annuel
        CUSTOM       // Personnalisé
    }

    public BigDecimal getRemainingAmount() {
        return amount.subtract(spent);
    }

    public BigDecimal getPercentageUsed() {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return spent.multiply(new BigDecimal("100")).divide(amount, 2, BigDecimal.ROUND_HALF_UP);
    }
}
