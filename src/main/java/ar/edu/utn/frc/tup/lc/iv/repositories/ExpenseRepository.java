package ar.edu.utn.frc.tup.lc.iv.repositories;

import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Integer> {

    Optional<ExpenseEntity> findExpenseEntitiesByInvoiceNumberAndProviderId(Integer invoiceNumber,Integer providerId);
    @Query("select e from ExpenseEntity e join e.installmentsList i where i.paymentDate between :from and :to and e.enabled")
    List<ExpenseEntity> findAllByPaymentDate(@Param("from") LocalDate from, @Param("to") LocalDate to);
    /**
     * Finds all enabled ExpenseEntity records where the expense date is between the specified dates.
     *
     * @param from the start date
     * @param to the end date
     * @return a list of ExpenseEntity records
     */
    @Query("select e from ExpenseEntity e where e.expenseDate between :from and :to and e.enabled")
    List<ExpenseEntity> findAllByDate(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
