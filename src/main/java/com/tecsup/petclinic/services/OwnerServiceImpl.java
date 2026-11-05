package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.repositories.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

	private final OwnerRepository repository;

	@Override
	public Owner create(Owner owner) {
		return repository.save(owner);
	}

	@Override
	public Owner update(Owner owner) throws OwnerNotFoundException {
		// Si no existe, falla igual que findById
		findById(owner.getId());
		return repository.save(owner);
	}

	@Override
	public void delete(Integer id) throws OwnerNotFoundException {
		Owner existing = findById(id);
		repository.delete(existing);
	}

	@Override
	public Owner findById(Integer id) throws OwnerNotFoundException {
		if (id == null) {
			throw new OwnerNotFoundException("Record not found...!");
		}
		return repository.findById(id)
				.orElseThrow(() -> new OwnerNotFoundException("Record not found...!"));
	}

	@Override
	public List<Owner> findAll() {
		return repository.findAll();
	}

	@Override
	public List<Owner> findByLastName(String lastName) {
		return repository.findByLastName(lastName);
	}

	@Override
	public List<Owner> findByCity(String city) {
		return repository.findByCity(city);
	}
}
