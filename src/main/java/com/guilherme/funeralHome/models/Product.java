package com.guilherme.funeralHome.models;

import com.guilherme.funeralHome.models.enums.ProductTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//item do catálogo: urnas, urnas cinerárias, flores e serviços (tanatopraxia, translado...)
@Entity
@Table(name = Product.TABLE_NAME) //tabela no banco de dados
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {

    public static final String TABLE_NAME = "products";

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id gerado pelo banco (auto incremento)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    @NotBlank(message = "O nome não pode ficar em branco.")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    @NotNull(message = "O tipo do produto não pode ficar em branco.")
    private ProductTypeEnum type;

    @Column(name = "description", length = 255)
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
    private String description;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "O preço não pode ficar em branco.")
    @DecimalMin(value = "0.00", message = "O preço não pode ser negativo.")
    private BigDecimal price;

    @Column(name = "stockControlled", nullable = false)
    private boolean stockControlled; //false para serviços, que não têm estoque físico

    //updatable = false: só os UPDATEs atômicos do ProductRepository mudam o estoque, assim editar o nome/preço
    //não regrava um estoque antigo por cima de uma venda feita nesse meio tempo
    @Column(name = "stockQuantity", updatable = false)
    @Min(value = 0, message = "O estoque não pode ser negativo.")
    private Integer stockQuantity; //null quando o estoque não é controlado

    @Column(name = "registrationDate", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "isActive", nullable = false)
    private boolean isActive; //exclusão lógica: mantém o produto no histórico dos atendimentos
}
