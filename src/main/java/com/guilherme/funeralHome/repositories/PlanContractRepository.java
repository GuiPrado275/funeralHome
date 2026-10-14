package com.guilherme.funeralHome.repositories;

import com.guilherme.funeralHome.models.PlanContract;
import com.guilherme.funeralHome.models.enums.ContractStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface PlanContractRepository extends JpaRepository<PlanContract, Long> {

    List<PlanContract> findByHolderId(Long holderId); //histórico de contratos de um cliente

    List<PlanContract> findByStatus(ContractStatusEnum status); //ex: todos os contratos ativos

    List<PlanContract> findByPlanIdAndStatusIn(Long planId, Collection<ContractStatusEnum> statuses); //usado no reajuste anual

    boolean existsByHolderIdAndStatusIn(Long holderId, Collection<ContractStatusEnum> statuses); //só um contrato aberto por cliente

    boolean existsByHolderId(Long holderId); //usado antes de excluir um usuário

    boolean existsByPlanIdAndStatusIn(Long planId, Collection<ContractStatusEnum> statuses); //plano com contratos em vigor

    //relatório financeiro da Lei 13.261/2016
    long countByStatus(ContractStatusEnum status);

    @Query("SELECT COALESCE(SUM(c.monthlyFee), 0) FROM PlanContract c WHERE c.status = :status")
    BigDecimal sumMonthlyFeeByStatus(@Param("status") ContractStatusEnum status);

    //contratos que incluem a pessoa, como titular ou como dependente
    @Query("SELECT DISTINCT c FROM PlanContract c LEFT JOIN c.dependents d " +
            "WHERE c.status IN :statuses AND (c.holder.cpf = :cpf OR d.cpf = :cpf)")
    List<PlanContract> findCoveringCpf(@Param("cpf") String cpf,
                                       @Param("statuses") Collection<ContractStatusEnum> statuses);

    //maior número de dependentes entre os contratos do plano, para o limite não ficar abaixo dele
    @Query("SELECT COALESCE(MAX(SIZE(c.dependents)), 0) FROM PlanContract c " +
            "WHERE c.plan.id = :planId AND c.status IN :statuses")
    int findMaxDependentsInUse(@Param("planId") Long planId,
                               @Param("statuses") Collection<ContractStatusEnum> statuses);
}
