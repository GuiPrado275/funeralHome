package com.guilherme.funeralHome.repositories;

import com.guilherme.funeralHome.models.WakeRoom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WakeRoomRepository extends JpaRepository<WakeRoom, Long> {

    List<WakeRoom> findByIsActiveTrue(); //só as salas em uso

    //SELECT ... FOR UPDATE: dois agentes reservando a mesma sala esperam um pelo outro, em vez de passarem juntos na checagem
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM WakeRoom r WHERE r.id = :id")
    Optional<WakeRoom> findByIdForUpdate(@Param("id") Long id);
}
