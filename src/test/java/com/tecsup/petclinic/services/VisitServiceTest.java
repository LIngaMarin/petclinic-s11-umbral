package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.*;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Caso 4: integración de VisitService con sus relaciones reales y H2.
 * Cada prueba crea sus propios registros con IDs generados y rollback automático.
 * flush y clear fuerzan una lectura desde SQL después de las operaciones CRUD.
 */
@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class VisitServiceTest {
    @Autowired
    private VisitService service;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbc;

    /**
     * Prepara tipo, dueño y mascota independientes. Como no existe una entidad Type,
     * inserta el tipo con JDBC y recupera su clave generada sin usar IDs fijos.
     */
    private Pet nuevaMascota(String nombre) {
        GeneratedKeyHolder claves = new GeneratedKeyHolder();
        jdbc.update(conexion -> {
            var sentencia = conexion.prepareStatement(
                    "INSERT INTO types (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, "Tipo S11");
            return sentencia;
        }, claves);
        int tipoId = claves.getKey().intValue();
        Owner owner = new Owner();
        owner.setFirstName("Dueño S11");
        owner.setLastName("Aguirre");
        entityManager.persist(owner);

        Pet pet = new Pet();
        pet.setName(nombre);
        pet.setOwnerId(owner.getId());
        pet.setTypeId(tipoId);
        pet.setBirthDate(LocalDate.of(2020, 1, 15));
        entityManager.persist(pet);
        return pet;
    }

    /** Crea un veterinario independiente para comprobar la relación opcional. */
    private Vet nuevoVeterinario() {
        Vet vet = new Vet();
        vet.setFirstName("Veterinario S11");
        vet.setLastName("Pruebas");
        vet.setActive(true);
        entityManager.persist(vet);
        return vet;
    }

    /** Prepara una visita válida con mascota propia, fecha, descripción y costo decimal. */
    private Visit nuevaVisita() {
        Visit visit = new Visit();
        visit.setPet(nuevaMascota("Mascota S11"));
        visit.setVet(nuevoVeterinario());
        visit.setVisitDate(LocalDate.of(2026, 11, 5));
        visit.setDescription("Control inicial S11");
        visit.setCost(new BigDecimal("45.50"));
        return visit;
    }

    /** Ejecuta el SQL pendiente y descarta las entidades de la caché JPA. */
    private void recargarDesdeBaseDeDatos() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Guarda una visita con mascota y veterinario nuevos mediante el servicio.
     * Comprueba ID, fecha, descripción, costo y claves de ambas relaciones tras recargar.
     */
    @Test
    void testCreateVisit() throws Exception {
        Visit preparada = nuevaVisita();
        Integer petId = preparada.getPet().getId();
        Integer vetId = preparada.getVet().getId();
        Visit creada = service.create(preparada);
        assertNotNull(creada.getId());
        Integer id = creada.getId();
        recargarDesdeBaseDeDatos();

        Visit recuperada = service.findById(id);
        assertAll(
                () -> assertEquals(id, recuperada.getId()),
                () -> assertEquals(LocalDate.of(2026, 11, 5), recuperada.getVisitDate()),
                () -> assertEquals("Control inicial S11", recuperada.getDescription()),
                () -> assertEquals(new BigDecimal("45.50"), recuperada.getCost()),
                () -> assertEquals(petId, recuperada.getPet().getId()),
                () -> assertEquals(vetId, recuperada.getVet().getId()));
    }

    /**
     * Crea una visita y busca su identificador generado desde H2.
     * Verifica sus datos y carga las relaciones para comprobar la mascota y el veterinario.
     */
    @Test
    void testFindVisitById() throws Exception {
        Visit creada = service.create(nuevaVisita());
        Integer id = creada.getId();
        recargarDesdeBaseDeDatos();

        Visit encontrada = service.findById(id);
        assertNotNull(encontrada);
        assertEquals(id, encontrada.getId());
        assertEquals("Control inicial S11", encontrada.getDescription());
        assertEquals("Mascota S11", encontrada.getPet().getName());
        assertEquals("Pruebas", encontrada.getVet().getLastName());
    }
}