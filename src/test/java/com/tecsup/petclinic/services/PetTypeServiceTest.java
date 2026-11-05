package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.PetType;
import com.tecsup.petclinic.exceptions.PetTypeNotFoundException;
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
public class PetTypeServiceTest {

	@Autowired
	private PetTypeService service;

	@Test
	public void testCreateType() {
		PetType created = service.create(PetType.builder().name("parrot")
				.description("Talking bird").active(true).sizeCategory("small")
				.averageLifespan(25).careLevel("high").build());
		log.info("TYPE CREATED: {}", created);

		assertNotNull(created.getId());
		assertEquals("parrot", created.getName());
		assertEquals(25, created.getAverageLifespan());
	}

	@Test
	public void testUpdateType() throws PetTypeNotFoundException {
		PetType created = service.create(PetType.builder().name("ferret")
				.description("Small carnivore").active(true).sizeCategory("small")
				.averageLifespan(7).careLevel("medium").build());

		created.setDescription("Domestic ferret");
		created.setCareLevel("high");
		PetType updated = service.update(created);

		assertEquals("Domestic ferret", updated.getDescription());
		assertEquals("high", service.findById(created.getId()).getCareLevel());
	}

	@Test
	public void testFindTypeById() throws PetTypeNotFoundException {
		PetType type = service.findById(1);

		assertEquals("cat", type.getName());
		assertEquals("Domestic feline", type.getDescription());
	}

	@Test
	public void testFindTypeById_NotFound() {
		assertThrows(PetTypeNotFoundException.class, () -> service.findById(9999));
	}

	@Test
	public void testDeleteType() throws PetTypeNotFoundException {
		PetType created = service.create(PetType.builder().name("temporal")
				.description("x").active(true).sizeCategory("small")
				.averageLifespan(1).careLevel("low").build());

		service.delete(created.getId());

		assertThrows(PetTypeNotFoundException.class, () -> service.findById(created.getId()));
	}

	@Test
	public void testFindByName() {
		List<PetType> found = service.findByName("dog");

		assertEquals(1, found.size());
		assertEquals(2, found.get(0).getId());
	}

	@Test
	public void testFindActiveTypes() {
		assertEquals(7, service.findByActive(true).size());
	}

	@Test
	public void testFindInactiveTypes() {
		List<PetType> inactive = service.findByActive(false);

		assertEquals(1, inactive.size());
		assertEquals("snake", inactive.get(0).getName());
	}
}
