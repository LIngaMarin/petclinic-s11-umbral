package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
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
public class VetServiceTest {

	@Autowired
	private VetService service;

	@Test
	public void testCreateVet() {
		Vet vet = Vet.builder().firstName("Lucia").lastName("Torres")
				.email("lucia.torres@petclinic.com").phone("999888777").active(true).build();

		Vet created = service.create(vet);
		log.info("VET CREATED: {}", created);

		assertNotNull(created.getId());
		assertEquals("Torres", created.getLastName());
		assertTrue(created.getActive());
	}

	@Test
	public void testUpdateVet() throws VetNotFoundException {
		Vet created = service.create(Vet.builder().firstName("Mario").lastName("Ruiz")
				.email("mario@petclinic.com").phone("111").active(true).build());

		created.setLastName("Ruiz Díaz");
		created.setPhone("222");
		Vet updated = service.update(created);

		assertEquals("Ruiz Díaz", updated.getLastName());
		assertEquals("222", service.findById(created.getId()).getPhone());
	}

	@Test
	public void testFindVetById() throws VetNotFoundException {
		Vet vet = service.findById(1);

		assertEquals("James", vet.getFirstName());
		assertEquals("Carter", vet.getLastName());
	}

	@Test
	public void testFindVetById_NotFound() {
		assertThrows(VetNotFoundException.class, () -> service.findById(9999));
	}

	@Test
	public void testDeleteVet() throws VetNotFoundException {
		Vet created = service.create(Vet.builder().firstName("Borrar").lastName("Vet")
				.email("borrar@petclinic.com").phone("333").active(true).build());

		service.delete(created.getId());

		assertThrows(VetNotFoundException.class, () -> service.findById(created.getId()));
	}

	@Test
	public void testFindByLastName() {
		List<Vet> vets = service.findByLastName("Carter");

		assertEquals(1, vets.size());
		assertEquals("James", vets.get(0).getFirstName());
	}

	@Test
	public void testFindActiveVets() {
		List<Vet> vets = service.findByActive(true);

		assertEquals(5, vets.size());
	}

	@Test
	public void testFindInactiveVets() {
		List<Vet> vets = service.findByActive(false);

		assertEquals(1, vets.size());
		assertEquals("Jenkins", vets.get(0).getLastName());
	}
}
