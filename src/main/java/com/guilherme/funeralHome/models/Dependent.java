package com.guilherme.funeralHome.models;

import com.guilherme.funeralHome.models.enums.RelationshipEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//pessoa coberta pelo plano funerário do titular
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Dependent {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "cpf", length = 11, nullable = false)
    private String cpf;

    @Column(name = "birthDate", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", length = 20, nullable = false)
    private RelationshipEnum relationship;

    @Column(name = "inclusionDate", nullable = false)
    private LocalDate inclusionDate; //a carência do dependente conta a partir do dia da inclusão
}
