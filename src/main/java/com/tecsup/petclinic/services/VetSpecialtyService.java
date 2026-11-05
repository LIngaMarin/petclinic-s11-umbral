package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;

import java.util.List;

public interface VetSpecialtyService {

	/** Asigna una especialidad a un veterinario. */
	VetSpecialty assign(VetSpecialty vetSpecialty);

	/** Quita la especialidad del veterinario. */
	void remove(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException;

	VetSpecialty find(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException;

	List<VetSpecialty> findSpecialtiesByVet(Integer vetId);

	List<VetSpecialty> findVetsBySpecialty(Integer specialtyId);
}
