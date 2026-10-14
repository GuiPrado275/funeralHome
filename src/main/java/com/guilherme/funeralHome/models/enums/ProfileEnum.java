package com.guilherme.funeralHome.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ProfileEnum {

    ADMIN(1, "ROLE_ADMIN"),
    AGENT(2, "ROLE_AGENT"), //agente funerário (funcionário): cuida dos atendimentos, do catálogo e das salas
    USER(3, "ROLE_USER"); //cliente: familiar responsável por um atendimento ou titular de plano funerário

    private Integer code;
    private String description;

    public static ProfileEnum toEnum(Integer code) {

        if(Objects.isNull(code)){ //se o código for nulo, retorna null
            return null;
        }
        for(ProfileEnum x : ProfileEnum.values()){ //percorre os valores do enum
            if (code.equals(x.getCode())){
                return x;
            }
        }

        throw new IllegalArgumentException("Código inválido! " + code); //código não encontrado: lança exceção

    }

}
