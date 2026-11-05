package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Speciality;
import com.tecsup.petclinic.exceptions.SpecialityNotFoundException;
import com.tecsup.petclinic.repositories.SpecialityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Implementa el CRUD sobre el esquema existente sin añadir restricciones de negocio. */
@Service
@RequiredArgsConstructor
@Transactional
public class SpecialityServiceImpl implements SpecialityService {
    private final SpecialityRepository repository;

    /** Persiste la especialidad mediante el repositorio real. */
    @Override
    public Speciality create(Speciality speciality) {
        return repository.save(speciality);
    }

    /** Comprueba la existencia antes de guardar para evitar inserciones accidentales. */
    @Override
    public Speciality update(Speciality speciality) throws SpecialityNotFoundException {
        findById(speciality.getId());
        return repository.save(speciality);
    }

    /** Elimina el registro encontrado dentro de la misma transacción. */
    @Override
    public void delete(Integer id) throws SpecialityNotFoundException {
        repository.delete(findById(id));
    }

    /** Traduce la ausencia del registro a una excepción específica del servicio. */
    @Override
    @Transactional(readOnly = true)
    public Speciality findById(Integer id) throws SpecialityNotFoundException {
        if (id == null) {
            throw new SpecialityNotFoundException("Especialidad no encontrada: null");
        }
        return repository.findById(id)
                .orElseThrow(() -> new SpecialityNotFoundException("Especialidad no encontrada: " + id));
    }

    /** Delega la consulta por nombre sin alterar los resultados. */
    @Override
    @Transactional(readOnly = true)
    public List<Speciality> findByName(String name) {
        return repository.findByName(name);
    }
}