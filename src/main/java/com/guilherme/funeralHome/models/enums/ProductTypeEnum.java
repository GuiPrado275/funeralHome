package com.guilherme.funeralHome.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductTypeEnum {
    COFFIN(true),        //urna funerária (caixão), todo atendimento tem exatamente uma
    CINERARY_URN(true),  //urna cinerária, para as cinzas, só em cremações
    FLOWERS(true),       //coroas e arranjos de flores
    EMBALMING(false),    //tanatopraxia/embalsamamento, exigida em translados longos
    SERVICE(false),      //ornamentação, higienização, necromaquiagem, etc.
    OTHER(true);         //véu, livro de presença, lembranças, etc.

    private final boolean physical; //produtos físicos têm estoque, serviços não
}
