package com.guilherme.funeralHome.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//plano de assistência funerária (Lei 13.261/2016)
@Entity
@Table(name = FuneralPlan.TABLE_NAME) //tabela no banco de dados
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FuneralPlan {

    public static final String TABLE_NAME = "funeral_plans";

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id gerado pelo banco (auto incremento)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    @Size(min = 2, max = 50, message = "O nome do plano deve ter entre 2 e 50 caracteres.")
    @NotBlank(message = "O nome do plano não pode ficar em branco.")
    private String name;

    @Column(name = "description", length = 255)
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
    private String description;

    //Lei 13.261/2016, art. 8º, I: lista detalhada dos serviços cobertos (urna, velório, carro funerário...)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @CollectionTable(name = "plan_covered_services", joinColumns = @JoinColumn(name = "planId"))
    @Column(name = "service", length = 150, nullable = false)
    private List<String> coveredServices = new ArrayList<>();

    @Column(name = "coverageArea", length = 150, nullable = false)
    @NotBlank(message = "A área de abrangência não pode ficar em branco.")
    private String coverageArea; //art. 8º, VI: onde os serviços são prestados (ex: "Grande São Paulo")

    @Column(name = "serviceRequestInstructions", length = 255, nullable = false)
    @NotBlank(message = "A forma de acionamento não pode ficar em branco.")
    private String serviceRequestInstructions; //art. 8º, VI: como acionar o serviço (ex: "Central 24h 0800 000 0000")

    @Column(name = "adjustmentIndex", length = 50, nullable = false)
    @NotBlank(message = "O índice de reajuste não pode ficar em branco.")
    private String adjustmentIndex; //art. 8º, VIII: índice do reajuste anual (ex: "IPCA")

    @Column(name = "monthlyFee", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "A mensalidade não pode ficar em branco.")
    @DecimalMin(value = "0.01", message = "A mensalidade deve ser maior que zero.")
    private BigDecimal monthlyFee; //preço para novos contratos; cada contrato guarda (e reajusta) a sua mensalidade

    @Column(name = "coverageAmount", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "O valor de cobertura não pode ficar em branco.")
    @DecimalMin(value = "0.00", message = "O valor de cobertura não pode ser negativo.")
    private BigDecimal coverageAmount; //valor máximo que o plano paga por atendimento

    @Column(name = "maxDependents", nullable = false)
    @NotNull(message = "A quantidade máxima de dependentes não pode ficar em branco.")
    @Min(value = 0, message = "A quantidade máxima de dependentes não pode ser negativa.")
    private Integer maxDependents;

    @Column(name = "gracePeriodDays", nullable = false)
    @NotNull(message = "A carência não pode ficar em branco.")
    @Min(value = 0, message = "A carência não pode ser negativa.")
    private Integer gracePeriodDays; //carência: dias após a adesão até a cobertura começar

    @Column(name = "registrationDate", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "isActive", nullable = false)
    private boolean isActive; //plano inativo não aceita novos contratos; os existentes continuam valendo
}
