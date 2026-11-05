package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.entities.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Acceso JPA a specialties; permite CRUD y búsqueda exacta por nombre. */
public interface SpecialityRepository extends JpaRepository<Speciality, Integer> {
    /** Recupera todas las especialidades cuyo nombre coincide. */
    List<Speciality> findByName(String name);
}