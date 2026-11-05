package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.repositories.VetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VetServiceImpl implements VetService {

	private final VetRepository repository;

	@Override
	public Vet create(Vet vet) {
		return repository.save(vet);
	}

	@Override
	public Vet update(Vet vet) throws VetNotFoundException {
		// Si no existe, falla igual que findById
		findById(vet.getId());
		return repository.save(vet);
	}

	@Override
	public void delete(Integer id) throws VetNotFoundException {
		Vet existing = findById(id);
		repository.delete(existing);
	}

	@Override
	public Vet findById(Integer id) throws VetNotFoundException {
		if (id == null) {
			throw new VetNotFoundException("Record not found...!");
		}
		return repository.findById(id)
				.orElseThrow(() -> new VetNotFoundException("Record not found...!"));
	}

	@Override
	public List<Vet> findAll() {
		return repository.findAll();
	}

	@Override
	public List<Vet> findByLastName(String lastName) {
		return repository.findByLastName(lastName);
	}

	@Override
	public List<Vet> findByActive(Boolean active) {
		return repository.findByActive(active);
	}
}
