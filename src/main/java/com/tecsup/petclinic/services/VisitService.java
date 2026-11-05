package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import java.util.List;

/** Contrato CRUD y consulta por mascota para las visitas veterinarias. */
public interface VisitService {
    /** Guarda una visita respetando las restricciones de la base de datos. */
    Visit create(Visit visit);
    /** Modifica una visita existente sin crear otra por accidente. */
    Visit update(Visit visit) throws VisitNotFoundException;
    /** Elimina la visita sin eliminar sus entidades relacionadas. */
    void delete(Integer id) throws VisitNotFoundException;
    /** Busca una visita o informa su ausencia. */
    Visit findById(Integer id) throws VisitNotFoundException;
    /** Devuelve exclusivamente las visitas de una mascota. */
    List<Visit> findByPetId(Integer petId);
}