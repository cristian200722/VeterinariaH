# 🐾 Sistema de Gestión Veterinaria

Sistema de gestión desarrollado para facilitar la administración y organización de los procesos internos de una veterinaria.

El proyecto permitirá gestionar información relacionada con **clientes, mascotas, servicios, productos y ventas**, centralizando los procesos principales del negocio en una sola aplicación.

## 🚀 Tecnologías

- ☕ Java
- 🍃 Spring Boot
- 🗄️ PostgreSQL
- 🔗 Spring Data JPA
- ⚙️ Hibernate
- ✈️ Flyway

## 🚧 Estado del proyecto

**En desarrollo** 👨‍💻

Actualmente se están implementando y mejorando las diferentes funcionalidades del sistema.

## 🐶 API de mascotas

El módulo de mascotas se relaciona con un cliente existente y expone las siguientes rutas:

- `POST /api/v1/mascotas` crea una mascota.
- `GET /api/v1/mascotas` lista todas las mascotas.
- `GET /api/v1/mascotas?clienteId={id}` lista las mascotas de un cliente.
- `GET /api/v1/mascotas/{id}` consulta una mascota.
- `PUT /api/v1/mascotas/{id}` actualiza una mascota.
- `DELETE /api/v1/mascotas/{id}` elimina una mascota.

Ejemplo para crear una mascota:

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

Para entender el código del módulo paso a paso, revisa la [guía de mascotas](docs/mascotas.md).

---

🐾 *Proyecto de gestión y administración para una veterinaria.*
