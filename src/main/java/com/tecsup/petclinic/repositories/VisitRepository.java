package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.entities.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Acceso JPA a las visitas y a su relación con la mascota. */
public interface VisitRepository extends JpaRepository<Visit, Integer> {
    /** Recupera las visitas que pertenecen a la mascota indicada. */
    List<Visit> findByPet_Id(Integer petId);
}