# Guía del módulo de mascotas

Esta guía explica únicamente el CRUD de mascotas. Léela en este orden para seguir el viaje de un dato desde el frontend hasta la base de datos.

## 1. Relación en el MER y la base de datos

El MER indica la relación `CLIENTE 1 ─── N MASCOTA`: un cliente puede registrar varias mascotas y cada mascota pertenece a un único cliente.

La migración `V1__crear_tablas.sql` ya había creado la tabla `mascota` con la columna `cliente_id` y una clave foránea hacia `cliente`. Por eso no fue necesario crear una migración nueva.

## 2. Entidad: el reflejo de la tabla

Archivo: `src/main/java/com/veterinariah/mascota/entity/Mascota.java`

La entidad es la clase Java que Hibernate relaciona con la tabla `mascota`.

- `@Entity` indica que es persistente.
- `@Table(name = "mascota")` especifica la tabla de PostgreSQL.
- `@Id` y `@GeneratedValue` indican que `id` es la llave primaria generada por la base de datos.
- `@ManyToOne` y `@JoinColumn(name = "cliente_id")` representan el dueño de la mascota.
- `FetchType.LAZY` evita traer todos los datos del cliente cuando no hacen falta.
- `@Column` define reglas que coinciden con las columnas, como longitud máxima y campos obligatorios.

## 3. DTO de entrada: lo que manda el frontend

Archivo: `src/main/java/com/veterinariah/mascota/dto/MascotaCrearDto.java`

El frontend no envía la entidad `Mascota`; envía este DTO como JSON. Sus anotaciones validan datos antes de llegar al servicio:

- `@NotNull` y `@Positive` en `clienteId`: exige un dueño válido.
- `@NotBlank`: nombre y especie deben contener texto.
- `@Size`: impide superar el tamaño que admite la columna.
- `@Positive`: el peso, si se envía, debe ser mayor que cero.
- `@PastOrPresent`: evita fechas de nacimiento futuras.

Si una regla falla, `@Valid` devuelve HTTP `400 Bad Request` y el manejador global responde el campo que tiene el error.

## 4. DTO de salida: lo que responde el backend

Archivo: `src/main/java/com/veterinariah/mascota/dto/MascotaRespuestaDto.java`

Es la respuesta que recibe el frontend. Devuelve `clienteId`, no todo el objeto cliente; así la respuesta es pequeña y se evita que el JSON se vuelva circular cuando más adelante el cliente tenga una lista de mascotas.

## 5. Mapper: el traductor entre capas

Archivo: `src/main/java/com/veterinariah/mascota/mapper/MascotaMapper.java`

El mapper evita repetir asignaciones de campos en el servicio.

1. `toEntity` crea una mascota nueva.
2. `aplicarCambios` copia y limpia los datos al crear o editar.
3. `toDto` prepara los datos que se devuelven al frontend.
4. `textoOpcional` cambia cadenas vacías por `null`; por ejemplo, una raza no indicada no queda guardada como `""`.

## 6. Repositorio: consultas a PostgreSQL

Archivo: `src/main/java/com/veterinariah/mascota/repository/MascotaRepository.java`

Al extender `JpaRepository<Mascota, Long>` se obtienen métodos ya listos como `save`, `findAll`, `findById`, `existsById` y `deleteById`.

El método `findByClienteIdOrderByNombreAsc` sigue una convención de Spring Data: Spring lee su nombre y genera una consulta equivalente a buscar por `cliente_id` y ordenar por `nombre` sin escribir SQL manualmente.

## 7. Servicio: las reglas del negocio

Archivo: `src/main/java/com/veterinariah/mascota/service/MascotaService.java`

El servicio coordina las otras capas y usa `@Transactional` para que cada cambio sea una operación consistente.

1. `crear`: busca el cliente, convierte el DTO, guarda y devuelve un DTO de respuesta.
2. `listar`: devuelve todas las mascotas o filtra si la URL trae `clienteId`.
3. `obtenerPorId`: busca una mascota por su id.
4. `actualizar`: busca la mascota y el cliente, aplica los cambios y guarda.
5. `eliminar`: comprueba que exista antes de eliminar.
6. `buscarCliente`: reutiliza la comprobación del dueño. Si no existe, lanza `RecursoNoEncontradoException`, que el proyecto convierte en HTTP 404.

## 8. Controlador: las rutas de la API

Archivo: `src/main/java/com/veterinariah/mascota/controller/MascotaController.java`

| Método | Ruta | Uso | Respuesta correcta |
| --- | --- | --- | --- |
| POST | `/api/v1/mascotas` | Crear | 201 Created |
| GET | `/api/v1/mascotas` | Listar todas | 200 OK |
| GET | `/api/v1/mascotas?clienteId=1` | Listar por dueño | 200 OK |
| GET | `/api/v1/mascotas/{id}` | Consultar una | 200 OK |
| PUT | `/api/v1/mascotas/{id}` | Actualizar | 200 OK |
| DELETE | `/api/v1/mascotas/{id}` | Eliminar | 204 No Content |

Ejemplo de JSON para crear o actualizar:

```json
{
  "clienteId": 1,
  "nombre": "Luna",
  "especie": "Perro",
  "raza": "Labrador",
  "peso": 18.50,
  "tipoPelo": "Corto",
  "fechaNacimiento": "2022-05-20"
}
```

## 9. Pruebas

Archivos:

- `src/test/java/com/veterinariah/mascota/service/MascotaServiceTest.java`
- `src/test/java/com/veterinariah/mascota/controller/MascotaControllerTest.java`

Las pruebas del servicio verifican la lógica sin una base de datos real, usando simulaciones (mocks) de repositorios y mapper. Las del controlador simulan peticiones HTTP y comprueban los códigos 201, 200, 204, 400 y 404.

Para volver a ejecutar todas las pruebas desde la carpeta raíz del backend:

```powershell
.\mvnw.cmd test
```
