package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.repositories.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Implementa las operaciones de visitas sobre JPA sin cambiar el esquema existente. */
@Service
@RequiredArgsConstructor
@Transactional
public class VisitServiceImpl implements VisitService {
    private final VisitRepository repository;

    /** Delega la inserción y la generación del identificador a JPA. */
    @Override
    public Visit create(Visit visit) {
        return repository.save(visit);
    }

    /** Exige que exista la visita antes de guardar sus cambios. */
    @Override
    public Visit update(Visit visit) throws VisitNotFoundException {
        findById(visit.getId());
        return repository.save(visit);
    }

    /** Elimina solo la visita; las relaciones no tienen cascada REMOVE. */
    @Override
    public void delete(Integer id) throws VisitNotFoundException {
        repository.delete(findById(id));
    }

    /** Convierte la ausencia del registro en una excepción específica. */
    @Override
    @Transactional(readOnly = true)
    public Visit findById(Integer id) throws VisitNotFoundException {
        if (id == null) {
            throw new VisitNotFoundException("Visita no encontrada: null");
        }
        return repository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException("Visita no encontrada: " + id));
    }

    /** Consulta mediante la clave foránea de la mascota. */
    @Override
    @Transactional(readOnly = true)
    public List<Visit> findByPetId(Integer petId) {
        return repository.findByPet_Id(petId);
    }
}