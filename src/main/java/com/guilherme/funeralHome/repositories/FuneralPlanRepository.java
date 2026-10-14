package com.guilherme.funeralHome.repositories;

import com.guilherme.funeralHome.models.FuneralPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuneralPlanRepository extends JpaRepository<FuneralPlan, Long> {

    List<FuneralPlan> findByIsActiveTrue(); //planos que ainda podem ser contratados
}
