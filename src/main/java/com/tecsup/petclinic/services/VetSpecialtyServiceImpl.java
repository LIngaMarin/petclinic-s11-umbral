package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.entities.VetSpecialtyId;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;
import com.tecsup.petclinic.repositories.VetSpecialtyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VetSpecialtyServiceImpl implements VetSpecialtyService {

	private final VetSpecialtyRepository repository;

	@Override
	public VetSpecialty assign(VetSpecialty vetSpecialty) {
		return repository.save(vetSpecialty);
	}

	@Override
	public void remove(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {
		VetSpecialty existing = find(vetId, specialtyId);
		repository.delete(existing);
	}

	@Override
	public VetSpecialty find(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {
		return repository.findById(new VetSpecialtyId(vetId, specialtyId))
				.orElseThrow(() -> new VetSpecialtyNotFoundException("Record not found...!"));
	}

	@Override
	public List<VetSpecialty> findSpecialtiesByVet(Integer vetId) {
		return repository.findByVetId(vetId);
	}

	@Override
	public List<VetSpecialty> findVetsBySpecialty(Integer specialtyId) {
		return repository.findBySpecialtyId(specialtyId);
	}
}
