package com.guilherme.funeralHome.models;

import com.guilherme.funeralHome.models.enums.VehicleTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

//frota: carros funerários e veículos de remoção
@Entity
@Table(name = Vehicle.TABLE_NAME) //tabela no banco de dados
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vehicle {

    public static final String TABLE_NAME = "vehicles";

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id gerado pelo banco (auto incremento)
    private Long id;

    @Column(name = "plate", length = 7, nullable = false, unique = true)
    @NotBlank(message = "A placa não pode ficar em branco.")
    private String plate; //salva sem hífen e em maiúsculas

    @Column(name = "model", length = 50, nullable = false)
    @NotBlank(message = "O modelo não pode ficar em branco.")
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    @NotNull(message = "O tipo do veículo não pode ficar em branco.")
    private VehicleTypeEnum type;

    @Column(name = "ratePerKm", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "O valor por km não pode ficar em branco.")
    @DecimalMin(value = "0.00", message = "O valor por km não pode ser negativo.")
    private BigDecimal ratePerKm;

    @Column(name = "isActive", nullable = false)
    private boolean isActive;
}
