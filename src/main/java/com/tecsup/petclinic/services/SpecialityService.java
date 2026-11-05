package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Speciality;
import com.tecsup.petclinic.exceptions.SpecialityNotFoundException;
import java.util.List;

/** Contrato CRUD de especialidades, consistente con OwnerService y VetService. */
public interface SpecialityService {
    /** Guarda una nueva especialidad y devuelve su identificador generado. */
    Speciality create(Speciality speciality);
    /** Actualiza una especialidad existente; falla si no se encuentra. */
    Speciality update(Speciality speciality) throws SpecialityNotFoundException;
    /** Elimina únicamente la especialidad indicada. */
    void delete(Integer id) throws SpecialityNotFoundException;
    /** Busca por identificador o informa que el registro no existe. */
    Speciality findById(Integer id) throws SpecialityNotFoundException;
    /** Busca por nombre exacto, sin exigir que sea único en el esquema. */
    List<Speciality> findByName(String name);
}