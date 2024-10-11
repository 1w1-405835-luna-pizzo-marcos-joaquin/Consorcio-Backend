package ar.edu.utn.frc.tup.lc.iv.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bills_expense_installments")
public class BillExpenseInstallmentsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "bill_expense_owner_id", nullable = false)
    private BillExpenseOwnerEntity billExpenseOwner;
    @ManyToOne
    @JoinColumn(name = "expense_installment_id", nullable = false)
    private ExpenseInstallmentEntity expenseInstallment;
    @Column(name = "amount", nullable = false, precision = 11, scale = 2)
    private BigDecimal amount;
    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;

    @Column(name = "created_user", nullable = false)
    private Integer createdUser;

    @Column(name = "last_updated_datetime", nullable = false)
    private LocalDateTime lastUpdatedDatetime;

    @Column(name = "last_updated_user", nullable = false)
    private Integer lastUpdatedUser;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
}
