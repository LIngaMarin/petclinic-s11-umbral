package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Speciality;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Caso 3: integración del servicio con JPA y H2, tomando PetServiceTest como referencia.
 * Cada prueba crea sus datos y revierte su transacción. Se vacía el contexto de
 * persistencia antes de consultar para comprobar la base de datos y no solo la caché.
 * La tabla real se llama specialties; SpecialtyServiceTest original se conserva.
 */
@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class SpecialityServiceTest {
    @Autowired
    private SpecialityService service;
    @Autowired
    private EntityManager entityManager;

    /** Prepara una especialidad completa sin asignar identificadores manuales. */
    private Speciality nuevaEspecialidad(String nombre) {
        Speciality speciality = new Speciality();
        speciality.setName(nombre);
        speciality.setOffice("Consultorio S11");
        speciality.setOpeningHour(8);
        speciality.setClosingHour(17);
        return speciality;
    }

    /** Envía el SQL pendiente y descarta la caché para exigir una lectura real. */
    private void recargarDesdeBaseDeDatos() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Crea Cardiología mediante el servicio y vuelve a leerla desde H2.
     * Las aserciones comprueban el ID generado y todos los datos almacenados.
     */
    @Test
    void testCreateSpeciality() throws Exception {
        Speciality creada = service.create(nuevaEspecialidad("Cardiología S11"));
        assertNotNull(creada.getId(), "La base debe generar el identificador");
        Integer id = creada.getId();
        recargarDesdeBaseDeDatos();

        Speciality recuperada = service.findById(id);
        assertAll(
                () -> assertEquals(id, recuperada.getId()),
                () -> assertEquals("Cardiología S11", recuperada.getName()),
                () -> assertEquals("Consultorio S11", recuperada.getOffice()),
                () -> assertEquals(8, recuperada.getOpeningHour()),
                () -> assertEquals(17, recuperada.getClosingHour()));
    }

    /**
     * Prepara Dermatología y consulta su identificador generado.
     * Comprueba que se encuentre la especialidad y coincidan su ID y nombre.
     */
    @Test
    void testFindSpecialityById() throws Exception {
        Integer id = service.create(nuevaEspecialidad("Dermatología S11")).getId();
        recargarDesdeBaseDeDatos();

        Speciality encontrada = service.findById(id);
        assertNotNull(encontrada);
        assertEquals(id, encontrada.getId());
        assertEquals("Dermatología S11", encontrada.getName());
    }

    /**
     * Crea una especialidad, cambia su nombre y ejecuta update.
     * La lectura posterior debe conservar el ID y mostrar el nuevo nombre.
     */
    @Test
    void testUpdateSpeciality() throws Exception {
        Speciality creada = service.create(nuevaEspecialidad("Nombre inicial S11"));
        Integer id = creada.getId();
        recargarDesdeBaseDeDatos();
        creada.setName("Nombre actualizado S11");

        Speciality actualizada = service.update(creada);
        recargarDesdeBaseDeDatos();

        assertEquals(id, actualizada.getId());
        assertEquals("Nombre actualizado S11", service.findById(id).getName());
    }

    /**
     * Crea una especialidad y elimina su ID mediante el servicio.
     * Tras ejecutar el SQL, la consulta debe lanzar la excepción de ausencia.
     */
    @Test
    void testDeleteSpeciality() throws Exception {
        Integer id = service.create(nuevaEspecialidad("Eliminar S11")).getId();
        recargarDesdeBaseDeDatos();

        service.delete(id);
        recargarDesdeBaseDeDatos();

        assertThrows(com.tecsup.petclinic.exceptions.SpecialityNotFoundException.class,
                () -> service.findById(id));
    }

    /**
     * Genera un ID propio y lo elimina para garantizar que ya no exista.
     * La búsqueda debe lanzar SpecialityNotFoundException con ese identificador.
     */
    @Test
    void testFindSpecialityByIdNotFound() throws Exception {
        Integer id = service.create(nuevaEspecialidad("Ausente S11")).getId();
        service.delete(id);
        recargarDesdeBaseDeDatos();

        var error = assertThrows(com.tecsup.petclinic.exceptions.SpecialityNotFoundException.class,
                () -> service.findById(id));
        assertTrue(error.getMessage().contains(id.toString()));
    }

    /**
     * Crea dos especialidades con nombres distintos y busca el primero.
     * Comprueba que la búsqueda exacta incluya solo el registro correspondiente.
     */
    @Test
    void testFindSpecialityByName() {
        Speciality esperada = service.create(nuevaEspecialidad("Nombre único S11"));
        service.create(nuevaEspecialidad("Otro nombre S11"));
        recargarDesdeBaseDeDatos();

        var encontradas = service.findByName("Nombre único S11");
        assertEquals(1, encontradas.size());
        assertEquals(esperada.getId(), encontradas.getFirst().getId());
        assertEquals("Nombre único S11", encontradas.getFirst().getName());
    }

    /**
     * Crea una especialidad y actualiza su consultorio y horario desde una entidad separada.
     * Comprueba los valores persistidos y que no se inserte otra especialidad con el mismo nombre.
     */
    @Test
    void testPersistSpecialityScheduleUpdate() throws Exception {
        Speciality creada = service.create(nuevaEspecialidad("Horario S11"));
        Integer id = creada.getId();
        recargarDesdeBaseDeDatos();
        creada.setOffice("Consultorio nuevo S11");
        creada.setOpeningHour(10);
        creada.setClosingHour(19);

        service.update(creada);
        recargarDesdeBaseDeDatos();

        Speciality recuperada = service.findById(id);
        assertAll(
                () -> assertEquals(id, recuperada.getId()),
                () -> assertEquals("Consultorio nuevo S11", recuperada.getOffice()),
                () -> assertEquals(10, recuperada.getOpeningHour()),
                () -> assertEquals(19, recuperada.getClosingHour()),
                () -> assertEquals(1, service.findByName("Horario S11").size()));
    }
}
