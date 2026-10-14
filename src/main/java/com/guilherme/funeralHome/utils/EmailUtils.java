package com.guilherme.funeralHome.utils;

import java.util.Locale;
import java.util.Objects;

public final class EmailUtils {

    private EmailUtils() {
    }

    //e-mails são salvos em minúsculas, então "Maria@X.com" e "maria@x.com" são a mesma conta (no cadastro e no login)
    public static String normalize(String email) {
        if (Objects.isNull(email)) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
