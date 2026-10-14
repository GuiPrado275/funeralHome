package com.guilherme.funeralHome.models;

import com.guilherme.funeralHome.models.enums.ContractStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//adesão de um cliente a um plano funerário, cobrindo o titular e os dependentes
@Entity
@Table(name = PlanContract.TABLE_NAME) //tabela no banco de dados
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlanContract {

    public static final String TABLE_NAME = "plan_contracts";

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id gerado pelo banco (auto incremento)
    private Long id;

    @ManyToOne //vários contratos (ao longo do tempo) para um titular
    @JoinColumn(name = "holderId", nullable = false, updatable = false)
    private User holder;

    @ManyToOne //vários contratos para um plano
    @JoinColumn(name = "planId", nullable = false, updatable = false)
    private FuneralPlan plan;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate; //a carência do titular conta a partir daqui

    @Column(name = "monthlyFee", precision = 10, scale = 2, nullable = false)
    private BigDecimal monthlyFee; //copiada do plano na assinatura; depois só muda pelo reajuste anual

    @Column(name = "dueDay", nullable = false)
    private Integer dueDay; //dia de vencimento (1 a 28, para existir em todos os meses)

    @Column(name = "lastAdjustmentDate")
    private LocalDate lastAdjustmentDate; //null até o primeiro reajuste (Lei 10.192/2001: no máximo um por ano)

    @Column(name = "defaultNoticeDate")
    private LocalDate defaultNoticeDate; //quando o titular foi notificado das mensalidades em atraso

    @Column(name = "defaultNoticeDeadline")
    private LocalDate defaultNoticeDeadline; //último dia para pagar antes de o contrato poder ser suspenso

    @Column(name = "suspensionDate")
    private LocalDate suspensionDate;

    @Column(name = "reactivationDate")
    private LocalDate reactivationDate; //depois de uma suspensão, a carência recomeça daqui

    @Column(name = "cancellationDate")
    private LocalDate cancellationDate; //null enquanto o contrato está ativo

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ContractStatusEnum status;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT) //select separado, para carregar junto com outras listas eager
    @CollectionTable(name = "contract_dependents", joinColumns = @JoinColumn(name = "contractId"))
    private List<Dependent> dependents = new ArrayList<>();
}
