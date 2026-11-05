package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.PetType;
import com.tecsup.petclinic.exceptions.PetTypeNotFoundException;
import com.tecsup.petclinic.repositories.PetTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PetTypeServiceImpl implements PetTypeService {

	private final PetTypeRepository repository;

	@Override
	public PetType create(PetType petType) {
		return repository.save(petType);
	}

	@Override
	public PetType update(PetType petType) throws PetTypeNotFoundException {
		// Si no existe, falla igual que findById
		findById(petType.getId());
		return repository.save(petType);
	}

	@Override
	public void delete(Integer id) throws PetTypeNotFoundException {
		PetType existing = findById(id);
		repository.delete(existing);
	}

	@Override
	public PetType findById(Integer id) throws PetTypeNotFoundException {
		if (id == null) {
			throw new PetTypeNotFoundException("Record not found...!");
		}
		return repository.findById(id)
				.orElseThrow(() -> new PetTypeNotFoundException("Record not found...!"));
	}

	@Override
	public List<PetType> findAll() {
		return repository.findAll();
	}

	@Override
	public List<PetType> findByName(String name) {
		return repository.findByName(name);
	}

	@Override
	public List<PetType> findByActive(Boolean active) {
		return repository.findByActive(active);
	}
}
