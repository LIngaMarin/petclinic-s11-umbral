# Laboratorio 11 — casos 3 y 4

Autor: SAguirreT — sheila.aguirre@tecsup.edu.pe  
Repositorio: https://github.com/LIngaMarin/petclinic-s11-umbral  
Directorio utilizado: `C:\AGUIRRE11\petclinic-s11-umbral`  
Rama: `feature/SAguirreT-casos-3-4`  
Fecha de verificación: 5 de noviembre de 2026, America/Lima.

## Alcance y revisión inicial

Se comprobó estado limpio, remoto origin, identidad Git y ausencia de la rama solicitada.
Se ejecutó fetch y se creó la rama desde origin/main, commit base `3f73959`.
La copia `C:\11AGUIRRE` no fue modificada.
No se actualizó ni se hicieron commits en main, merges ni operaciones destructivas.

Se revisaron pom.xml, entidades, servicios e implementaciones, repositorios, configuración,
schema.sql, datos iniciales y pruebas existentes, especialmente PetServiceTest.
El proyecto usa Java 21, Spring Boot 3.5.14, JUnit 5, Spring Data JPA y Maven Wrapper.
No existía src/test/resources: se reutiliza application-h2.yml con H2 en memoria.
La suite base contenía 32 pruebas ejecutables.

No existían las capas de especialidades ni visitas en origin/main.
Su implementación fue autorizada expresamente antes de agregarlas.
El esquema real contiene `specialties`, no `specialities`.
Se conserva SpecialtyServiceTest original, que solo contiene código comentado y no aporta pruebas ejecutables.
Las nuevas pruebas se llaman SpecialityServiceTest y VisitServiceTest, como se solicitó.

## Archivos creados

Las rutas Java se encuentran bajo `src/main/java/com/tecsup/petclinic/`:

- entities/Speciality.java y entities/Visit.java.
- repositories/SpecialityRepository.java y repositories/VisitRepository.java.
- services/SpecialityService.java y services/SpecialityServiceImpl.java.
- services/VisitService.java y services/VisitServiceImpl.java.
- exceptions/SpecialityNotFoundException.java y exceptions/VisitNotFoundException.java.

Pruebas bajo `src/test/java/com/tecsup/petclinic/services/`:

- SpecialityServiceTest.java.
- VisitServiceTest.java.

Documentación:

- docs/LABORATORIO11-CASOS3-4.md.
- docs/evidencias-lab11-resultados.txt: extractos reales de las salidas Maven, sin propiedades de entorno.

No se modificaron archivos preexistentes del proyecto, configuración, esquema ni pruebas de otros integrantes.

## Contratos y relaciones implementados

Ambos servicios ofrecen `create`, `findById`, `update` y `delete`.
La actualización exige que el ID ya exista. La búsqueda y eliminación de un ID ausente
lanzan la excepción específica, siguiendo la convención de OwnerService y VetService.
SpecialityService añade `findByName`, que hace una búsqueda exacta y devuelve una lista.
VisitService añade `findByPetId`, delegando a `VisitRepository.findByPet_Id`.

Speciality mapea nombre, consultorio y horas de apertura/cierre de specialties.
No se inventaron restricciones para nombres vacíos u horarios que el esquema no define.

Visit mapea:

| Campo | Tipo Java | Restricción del esquema |
|---|---|---|
| id | Integer | Generado por la base de datos |
| pet | Pet | ManyToOne obligatorio, pet_id NOT NULL |
| vet | Vet | ManyToOne opcional, vet_id admite NULL |
| visitDate | LocalDate | visit_date NOT NULL |
| description | String | Hasta 255 caracteres, opcional |
| cost | BigDecimal | DECIMAL(10,2), opcional |

No hay cascadas JPA desde Visit hacia Pet o Vet.
Las claves foráneas ya existentes controlan las relaciones en la base de datos.

## Pruebas implementadas

Todas las pruebas nuevas se ejecutan con SpringBootTest, ActiveProfiles("h2") y
Transactional. Son pruebas de integración con servicio y repositorio reales.
Cada una crea sus datos y revierte su transacción al terminar.
Los identificadores se generan al insertar; no se depende de IDs de los datos semilla.
Para probar un ID inexistente se crea y elimina un registro propio.

Las pruebas de persistencia ejecutan flush y clear antes de recuperar datos.
Así se comprueba el SQL almacenado y se evita validar únicamente la caché de JPA.
Esto verifica persistencia dentro de la transacción de prueba, no durabilidad después de un commit:
las pruebas terminan en rollback.

| Caso | Método | Objetivo y resultado esperado |
|---|---|---|
| 3 | testCreateSpeciality | Guardar y recuperar ID, nombre, consultorio y horario completos. |
| 3 | testFindSpecialityById | Recuperar una especialidad creada y comparar ID y nombre. |
| 3 | testUpdateSpeciality | Cambiar el nombre y verificar su persistencia conservando el ID. |
| 3 | testDeleteSpeciality | Eliminar una especialidad y comprobar que ya no pueda consultarse. |
| 3 | testFindSpecialityByIdNotFound | Confirmar SpecialityNotFoundException y el ID en su mensaje. |
| 3 | testFindSpecialityByName | Filtrar por nombre exacto excluyendo otra especialidad. |
| 3 | testPersistSpecialityScheduleUpdate | Persistir consultorio y horario desde una entidad separada sin duplicar el registro. |
| 4 | testCreateVisit | Persistir fecha, descripción, costo y relaciones con mascota y veterinario. |
| 4 | testFindVisitById | Recuperar la visita y cargar los datos de ambas relaciones. |
| 4 | testUpdateVisit | Persistir descripción, fecha y costo conservando ID y relaciones. |
| 4 | testDeleteVisit | Borrar solo una visita conservando otra visita, mascota, dueño y veterinario. |
| 4 | testFindVisitByIdNotFound | Confirmar VisitNotFoundException y el ID en su mensaje. |
| 4 | testCreateVisitWithoutDate | Rechazar la fecha nula con DataIntegrityViolationException. |
| 4 | testFindVisitsByPetId | Recuperar exactamente dos visitas de una mascota, excluir otra y comprobar veterinario opcional. |

Para preparar visitas se crean tipo, dueño, mascota y veterinario propios.
Como no existe entidad Type, el tipo se inserta con JdbcTemplate y se recupera su clave generada.
Esa inserción participa de la misma transacción de prueba.

## Resultados reales

Cada comando test compiló producción y pruebas antes de ejecutar Surefire.
Todas las ejecuciones de la tabla terminaron con BUILD SUCCESS, sin fallos, errores u omisiones.

| Ejecución | Pruebas | Correctas | Fallos | Errores | Omitidas |
|---|---:|---:|---:|---:|---:|
| Línea base antes de implementar | 32 | 32 | 0 | 0 | 0 |
| Especialidades: creación y búsqueda | 2 | 2 | 0 | 0 | 0 |
| Especialidades: CRUD | 4 | 4 | 0 | 0 | 0 |
| Especialidades: caso completo | 7 | 7 | 0 | 0 | 0 |
| Visitas: creación y búsqueda | 2 | 2 | 0 | 0 | 0 |
| Visitas: CRUD | 4 | 4 | 0 | 0 | 0 |
| Visitas: caso completo | 7 | 7 | 0 | 0 | 0 |
| Ambos casos juntos | 14 | 14 | 0 | 0 | 0 |
| Suite completa, primera ejecución | 46 | 46 | 0 | 0 | 0 |
| Suite completa, segunda ejecución | 46 | 46 | 0 | 0 | 0 |
| Suite completa, verificación final de formato | 46 | 46 | 0 | 0 | 0 |

Se implementaron 14 pruebas nuevas. La suite completa tiene 46 pruebas distintas;
las ejecuciones progresivas y repeticiones no representan pruebas adicionales.

Distribución de los reportes Maven Surefire:

| Clase | Pruebas | Fallos | Errores | Omitidas |
|---|---:|---:|---:|---:|
| PetClinicApplicationTests | 1 | 0 | 0 | 0 |
| OwnerServiceTest | 9 | 0 | 0 | 0 |
| PetServiceMockitoTest | 7 | 0 | 0 | 0 |
| PetServiceTest | 7 | 0 | 0 | 0 |
| SpecialityServiceTest | 7 | 0 | 0 | 0 |
| VetServiceTest | 8 | 0 | 0 | 0 |
| VisitServiceTest | 7 | 0 | 0 | 0 |
| Total | 46 | 0 | 0 | 0 |

Los reportes originales de la última ejecución están en target/surefire-reports.
Los logs de ejecuciones progresivas están en target/lab11-evidencias.
target está ignorado por Git y puede eliminarse al ejecutar clean.
El archivo de extractos en docs conserva los resultados relevantes para el informe.

## Incidencias y límites

- JAVA_HOME apuntaba a Java 7, causando UnsupportedClassVersionError antes de ejecutar Maven.
  Se seleccionó el JDK 21 instalado solo para los procesos de verificación, sin cambios globales.
- Faltaban las capas de producción de ambos casos; se implementaron con autorización expresa.
- No hubo fallos de aserciones, compilación ni regresiones durante la implementación.
- La excepción de fecha nula es el resultado esperado y verificado por assertThrows.
- Se observaron advertencias de instrumentación dinámica de Mockito en Java 21; no afectaron las pruebas.
- Se revisó el formato de cierre de las clases nuevas antes de la entrega.
- La validación se realizó en H2 en memoria. No se verificó MySQL ni se modificaron datos reales.

## Commits

Todos los commits pertenecen a feature/SAguirreT-casos-3-4:

| ID | Mensaje |
|---|---|
| 5f04ced | test(caso3): implementar creación y búsqueda de especialidades |
| ca1158c | test(caso3): implementar actualización y eliminación de especialidades |
| f0fc5bc | test(caso3): agregar pruebas adicionales de especialidades |
| dddc4bd | test(caso4): implementar creación y búsqueda de visitas |
| 26b0f13 | test(caso4): implementar actualización y eliminación de visitas |
| ee67945 | test(caso4): agregar pruebas adicionales de visitas |

El séptimo commit contiene este informe, los extractos y el ajuste de formato de las clases nuevas:
`docs(test): documentar pruebas y resultados de casos 3 y 4`.
Su ID se obtiene con `git log -1 --oneline` después de crearlo.

Destino de publicación: [rama de GitHub](https://github.com/LIngaMarin/petclinic-s11-umbral/tree/feature/SAguirreT-casos-3-4).
El push se realiza después del commit documental y se verifica comparando el ID local con la rama remota.
No se realiza merge a main.

## Repetir las pruebas desde PowerShell

```powershell
Set-Location 'C:\AGUIRRE11\petclinic-s11-umbral'
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

.\mvnw.cmd -B test "-Dspring.profiles.active=h2" "-Dtest=SpecialityServiceTest"
.\mvnw.cmd -B test "-Dspring.profiles.active=h2" "-Dtest=VisitServiceTest"
.\mvnw.cmd -B test "-Dspring.profiles.active=h2" "-Dtest=SpecialityServiceTest,VisitServiceTest"
.\mvnw.cmd -B test "-Dspring.profiles.active=h2"
```

En IntelliJ se puede seleccionar JDK 21 para el proyecto y para Maven Runner.
Para ejecutar una clase con el icono de prueba, las clases nuevas ya activan el perfil h2.

## Capturas reales para el informe

No se generaron ni simularon capturas. Para obtenerlas:

1. Abrir la ruta del repositorio utilizada y expandir entities, repositories, services y src/test/java
   en el panel Project de IntelliJ. Capturar la estructura y la rama visible.
2. Abrir SpecialityServiceTest.java. Capturar las anotaciones de aislamiento y sus siete métodos;
   usar varias capturas si hace falta para mantener el texto legible.
3. Abrir VisitServiceTest.java. Capturar preparación de relaciones, CRUD y pruebas adicionales.
4. Ejecutar el comando individual de SpecialityServiceTest y capturar el resumen de 7 pruebas con BUILD SUCCESS.
5. Ejecutar el comando individual de VisitServiceTest y capturar el resumen de 7 pruebas con BUILD SUCCESS.
6. Ejecutar la suite completa y capturar las 46 pruebas y BUILD SUCCESS. Los logs completos
   de esta sesión también se pueden abrir en target/lab11-evidencias/suite-01.log y suite-02.log.
7. Ejecutar `git log -7 --oneline` y `git log --all --graph --oneline -20`;
   capturar los siete commits propios y el nombre de la rama.
8. Abrir el enlace de la rama en GitHub después del push. Capturar el selector de rama,
   los archivos nuevos y la lista de commits.

Para verificar la publicación sin capturas:

```powershell
git branch --show-current
git status --short --branch
git log -7 --oneline
git rev-parse HEAD
git ls-remote origin refs/heads/feature/SAguirreT-casos-3-4
```

El hash devuelto por ls-remote debe coincidir con HEAD.

## Tres conclusiones técnicas

1. Las pruebas de integración con H2 validan el recorrido servicio–repositorio–SQL;
   flush y clear permiten detectar errores de persistencia que una comprobación en caché podría ocultar.
2. La preparación de datos propios, los IDs generados y el rollback permiten pruebas independientes
   que respetan las claves foráneas y preservan los registros de los demás casos.
3. La ejecución progresiva y dos ejecuciones completas sin regresiones, junto con commits por funcionalidad,
   aportan evidencia reproducible y facilitan revisar los cambios antes de integrarlos al trabajo del equipo.