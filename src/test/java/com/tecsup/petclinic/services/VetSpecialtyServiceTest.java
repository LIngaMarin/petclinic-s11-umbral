package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;

import java.time.LocalDate;
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
public class VetSpecialtyServiceTest {

	@Autowired
	private VetSpecialtyService service;

	@Test
	public void testAssignSpecialtyToVet() throws VetSpecialtyNotFoundException {
		// El veterinario 1 no tiene especialidades en los datos de prueba
		VetSpecialty assigned = service.assign(VetSpecialty.builder().vetId(1).specialtyId(2)
				.certificationDate(LocalDate.of(2020, 5, 10)).yearsExperience(4)
				.primarySpecialty(true).notes("Nueva asignación").build());
		log.info("ASSIGNED: {}", assigned);

		VetSpecialty found = service.find(1, 2);
		assertEquals(4, found.getYearsExperience());
		assertTrue(found.getPrimarySpecialty());
	}

	@Test
	public void testFindSpecialtiesByVet() {
		// El veterinario 3 tiene cirugía y odontología
		List<VetSpecialty> list = service.findSpecialtiesByVet(3);

		assertEquals(2, list.size());
	}

	@Test
	public void testFindSpecialtiesByVet_Empty() {
		assertTrue(service.findSpecialtiesByVet(1).isEmpty());
	}

	@Test
	public void testFindVetsBySpecialty() {
		// Radiología la tienen los veterinarios 2 y 5
		List<VetSpecialty> list = service.findVetsBySpecialty(1);

		assertEquals(2, list.size());
	}

	@Test
	public void testRemoveSpecialtyFromVet() throws VetSpecialtyNotFoundException {
		service.remove(2, 1);

		assertThrows(VetSpecialtyNotFoundException.class, () -> service.find(2, 1));
	}

	@Test
	public void testRemoveSpecialtyFromVet_NotFound() {
		// El veterinario 1 no tiene la especialidad 3
		assertThrows(VetSpecialtyNotFoundException.class, () -> service.remove(1, 3));
	}
}
