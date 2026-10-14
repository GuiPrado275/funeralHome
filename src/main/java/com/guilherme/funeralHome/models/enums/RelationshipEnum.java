package com.guilherme.funeralHome.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

//parentesco entre o dependente do plano e o titular
@AllArgsConstructor
@Getter
public enum RelationshipEnum {
    SPOUSE("Cônjuge"),
    CHILD("Filho(a)"),
    FATHER("Pai"),
    MOTHER("Mãe"),
    SIBLING("Irmão(ã)"),
    GRANDPARENT("Avô/Avó"),
    GRANDCHILD("Neto(a)"),
    OTHER("Outro");

    private final String label; //usado nos documentos impressos
}
