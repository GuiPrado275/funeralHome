package com.guilherme.funeralHome.repositories;

import com.guilherme.funeralHome.models.Product;
import com.guilherme.funeralHome.models.enums.ProductTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsActiveTrue(); //só os produtos ativos do catálogo

    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name); //busca por nome (parcial, sem diferenciar maiúsculas)

    List<Product> findByTypeAndIsActiveTrue(ProductTypeEnum type); //ex: todas as urnas, todas as flores

    //o estoque muda com um UPDATE atômico (em vez de ler, subtrair e salvar), assim duas vendas simultâneas
    //não levam a mesma última unidade; retorna 0 quando falta estoque
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity " +
            "WHERE p.id = :id AND p.stockQuantity >= :quantity")
    int decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :id")
    int increaseStock(@Param("id") Long id, @Param("quantity") int quantity);
}
