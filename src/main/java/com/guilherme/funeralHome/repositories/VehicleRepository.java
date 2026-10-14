package com.guilherme.funeralHome.repositories;

import com.guilherme.funeralHome.models.Vehicle;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByIsActiveTrue(); //veículos em uso

    boolean existsByPlate(String plate); //evita cadastrar o mesmo veículo duas vezes

    //SELECT ... FOR UPDATE: dois agentes reservando o mesmo veículo esperam um pelo outro, em vez de passarem juntos na checagem
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vehicle v WHERE v.id = :id")
    Optional<Vehicle> findByIdForUpdate(@Param("id") Long id);
}
