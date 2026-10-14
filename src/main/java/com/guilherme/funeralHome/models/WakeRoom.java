package com.guilherme.funeralHome.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

//sala de velório
@Entity
@Table(name = WakeRoom.TABLE_NAME) //tabela no banco de dados
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WakeRoom {

    public static final String TABLE_NAME = "wake_rooms";

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id gerado pelo banco (auto incremento)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    @Size(min = 1, max = 50, message = "O nome da sala deve ter entre 1 e 50 caracteres.")
    @NotBlank(message = "O nome da sala não pode ficar em branco.")
    private String name;

    @Column(name = "capacity", nullable = false)
    @NotNull(message = "A capacidade não pode ficar em branco.")
    @Min(value = 1, message = "A capacidade deve ser de no mínimo 1 pessoa.")
    private Integer capacity;

    @Column(name = "hourlyRate", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "O valor por hora não pode ficar em branco.")
    @DecimalMin(value = "0.00", message = "O valor por hora não pode ser negativo.")
    private BigDecimal hourlyRate;

    @Column(name = "isActive", nullable = false)
    private boolean isActive;
}
