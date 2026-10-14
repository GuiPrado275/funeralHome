package com.guilherme.funeralHome.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum ContractStatusEnum {
    ACTIVE("Ativo"),
    SUSPENDED("Suspenso por inadimplência"), //mensalidades atrasadas e prazo da notificação vencido: sem cobertura até pagar
    CANCELLED("Cancelado"); //cancelado pelo titular (a Lei 13.261/2016 permite a qualquer momento) ou encerrado pela morte do titular

    //contratos que ainda vinculam o titular (ninguém pode estar em dois ao mesmo tempo)
    public static final List<ContractStatusEnum> OPEN = List.of(ACTIVE, SUSPENDED);

    private final String label; //usado nos documentos impressos
}
