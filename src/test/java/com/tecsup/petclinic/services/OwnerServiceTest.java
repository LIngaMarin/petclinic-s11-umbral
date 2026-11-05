package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias con base de datos (H2). Cada prueba se revierte al terminar
 * (@Transactional) para no alterar los datos de las demás.
 */
@SpringBootTest
@Transactional
@Slf4j
public class OwnerServiceTest {

	@Autowired
	private OwnerService service;

	@Test
	public void testCreateOwner() {
		Owner owner = Owner.builder().firstName("Lucas").lastName("Inga")
				.address("Av. Lima 123").city("Lima").telephone("999111222").build();

		Owner created = service.create(owner);
		log.info("OWNER CREATED: {}", created);

		assertNotNull(created.getId());
		assertEquals("Lucas", created.getFirstName());
		assertEquals("Lima", created.getCity());
	}

	@Test
	public void testUpdateOwner() throws OwnerNotFoundException {
		Owner created = service.create(Owner.builder().firstName("Ana").lastName("Perez")
				.address("Calle 1").city("Cusco").telephone("111").build());

		created.setFirstName("Ana María");
		created.setCity("Arequipa");
		Owner updated = service.update(created);

		assertEquals("Ana María", updated.getFirstName());
		assertEquals("Arequipa", service.findById(created.getId()).getCity());
	}

	@Test
	public void testFindOwnerById() throws OwnerNotFoundException {
		Owner owner = service.findById(1);

		assertEquals("George", owner.getFirstName());
		assertEquals("Franklin", owner.getLastName());
	}

	@Test
	public void testFindOwnerById_NotFound() {
		assertThrows(OwnerNotFoundException.class, () -> service.findById(9999));
	}

	@Test
	public void testDeleteOwner() throws OwnerNotFoundException {
		Owner created = service.create(Owner.builder().firstName("Borrar").lastName("Me")
				.address("Calle 2").city("Piura").telephone("222").build());

		service.delete(created.getId());

		assertThrows(OwnerNotFoundException.class, () -> service.findById(created.getId()));
	}

	@Test
	public void testDeleteOwner_NotFound() {
		assertThrows(OwnerNotFoundException.class, () -> service.delete(9999));
	}

	@Test
	public void testFindByLastName() {
		List<Owner> owners = service.findByLastName("Davis");

		assertEquals(2, owners.size());
	}

	@Test
	public void testFindByLastName_Empty() {
		assertTrue(service.findByLastName("NoExiste").isEmpty());
	}

	@Test
	public void testFindByCity() {
		List<Owner> owners = service.findByCity("Madison");

		assertEquals(4, owners.size());
	}
}
