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

    /**
     * Crea una visita y actualiza descripción, fecha y costo desde una entidad separada.
     * Comprueba que los cambios persistan con el mismo ID y conserven ambas relaciones.
     */
    @Test
    void testUpdateVisit() throws Exception {
        Visit creada = service.create(nuevaVisita());
        Integer id = creada.getId();
        Integer petId = creada.getPet().getId();
        Integer vetId = creada.getVet().getId();
        recargarDesdeBaseDeDatos();
        creada.setDescription("Control actualizado S11");
        creada.setVisitDate(LocalDate.of(2026, 11, 6));
        creada.setCost(new BigDecimal("60.75"));

        Visit actualizada = service.update(creada);
        recargarDesdeBaseDeDatos();

        Visit recuperada = service.findById(id);
        assertAll(
                () -> assertEquals(id, actualizada.getId()),
                () -> assertEquals("Control actualizado S11", recuperada.getDescription()),
                () -> assertEquals(LocalDate.of(2026, 11, 6), recuperada.getVisitDate()),
                () -> assertEquals(new BigDecimal("60.75"), recuperada.getCost()),
                () -> assertEquals(petId, recuperada.getPet().getId()),
                () -> assertEquals(vetId, recuperada.getVet().getId()));
    }

    /**
     * Crea dos visitas para la misma mascota y elimina solo la primera.
     * Verifica su ausencia y la conservación de la otra visita, mascota, dueño y veterinario.
     */
    @Test
    void testDeleteVisit() throws Exception {
        Visit primera = service.create(nuevaVisita());
        Integer id = primera.getId();
        Integer petId = primera.getPet().getId();
        Integer ownerId = primera.getPet().getOwnerId();
        Integer vetId = primera.getVet().getId();
        Visit otra = new Visit();
        otra.setPet(primera.getPet());
        otra.setVet(primera.getVet());
        otra.setVisitDate(LocalDate.of(2026, 11, 7));
        otra.setDescription("Segunda visita S11");
        Integer otroId = service.create(otra).getId();
        recargarDesdeBaseDeDatos();

        service.delete(id);
        recargarDesdeBaseDeDatos();

        assertThrows(VisitNotFoundException.class, () -> service.findById(id));
        assertEquals("Segunda visita S11", service.findById(otroId).getDescription());
        assertEquals("Mascota S11", entityManager.find(Pet.class, petId).getName());
        assertNotNull(entityManager.find(Owner.class, ownerId));
        assertNotNull(entityManager.find(Vet.class, vetId));
    }

    /**
     * Crea y elimina una visita propia para obtener un ID garantizado como ausente.
     * La consulta debe lanzar VisitNotFoundException e identificar el registro solicitado.
     */
    @Test
    void testFindVisitByIdNotFound() throws Exception {
        Integer id = service.create(nuevaVisita()).getId();
        service.delete(id);
        recargarDesdeBaseDeDatos();

        VisitNotFoundException error = assertThrows(VisitNotFoundException.class,
                () -> service.findById(id));
        assertTrue(error.getMessage().contains(id.toString()));
    }

    /**
     * Prepara todas las relaciones de una visita pero omite su fecha obligatoria.
     * El servicio debe rechazarla con DataIntegrityViolationException; el rollback
     * de la prueba descarta la transacción fallida sin ejecutar más consultas.
     */
    @Test
    void testCreateVisitWithoutDate() {
        Visit invalida = nuevaVisita();
        invalida.setVisitDate(null);

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> service.create(invalida));
    }

    /**
     * Crea dos visitas para una mascota y otra visita para una mascota distinta.
     * Una de las primeras no tiene veterinario, permitido por el esquema.
     * La consulta debe devolver solo los dos IDs propios, conservar la relación
     * y persistir el veterinario nulo sin incluir visitas de otra mascota.
     */
    @Test
    void testFindVisitsByPetId() {
        Visit primera = service.create(nuevaVisita());
        Integer petId = primera.getPet().getId();
        Visit segunda = new Visit();
        segunda.setPet(primera.getPet());
        segunda.setVisitDate(LocalDate.of(2026, 11, 8));
        segunda.setDescription("Visita sin veterinario S11");
        Integer segundaId = service.create(segunda).getId();
        Visit ajena = service.create(nuevaVisita());
        recargarDesdeBaseDeDatos();

        var encontradas = service.findByPetId(petId);
        var ids = encontradas.stream().map(Visit::getId).collect(java.util.stream.Collectors.toSet());
        assertEquals(java.util.Set.of(primera.getId(), segundaId), ids);
        assertFalse(ids.contains(ajena.getId()));
        assertTrue(encontradas.stream().allMatch(visita -> visita.getPet().getId().equals(petId)));
        Visit sinVeterinario = encontradas.stream()
                .filter(visita -> visita.getId().equals(segundaId)).findFirst().orElseThrow();
        assertNull(sinVeterinario.getVet());
    }
}
