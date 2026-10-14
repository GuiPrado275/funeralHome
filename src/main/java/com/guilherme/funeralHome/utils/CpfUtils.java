package com.guilherme.funeralHome.utils;

import java.util.Objects;

public final class CpfUtils {

    private CpfUtils() {
    }

    //CPFs são salvos só com dígitos, então "123.456.789-09" e "12345678909" são a mesma pessoa
    public static String normalize(String cpf) {
        if (Objects.isNull(cpf) || cpf.isBlank()) {
            return null;
        }
        return cpf.replaceAll("\\D", "");
    }
}
