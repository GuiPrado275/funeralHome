package com.guilherme.funeralHome.repositories;

import com.guilherme.funeralHome.models.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional(readOnly = true)
    User findByEmail(String email); //busca o usuário pelo e-mail

    boolean existsByEmail(String email); //usado no cadastro, para barrar e-mail repetido

    boolean existsByCpf(String cpf); //usado no cadastro, para barrar CPF repetido

    //SELECT ... FOR UPDATE: dois contratos simultâneos para o mesmo titular não são criados juntos
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdForUpdate(@Param("id") Long id);
}
