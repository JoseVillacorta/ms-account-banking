
# Banking System Account Microservice

**Microservicio para la gestión de cuentas bancarias y operaciones de saldo.**

Este servicio permite la creación y administración de cuentas (Ahorros, Corriente, etc.), vinculándolas a sus respectivos clientes y permitiendo realizar transferencias directas de forma atómica.

## Descripción del Proyecto

El **Account Microservice** utiliza una arquitectura reactiva y transaccional para asegurar que las operaciones de dinero sean seguras y consistentes.

### Qué hace esta aplicación
- **Gestión de Cuentas:** CRUD completo de cuentas bancarias.
- **Transferencias Atómicas:** Procesa transferencias entre cuentas mediante procedimientos almacenados en la base de datos para garantizar la integridad.
- **Relación con Clientes:** Vincula cuentas a los IDs de clientes existentes en `ms-customer`.
- **Persistencia Reactiva:** Utiliza **R2DBC** y **PostgreSQL**.

### Tecnologías utilizadas
- **Java 21**
- **Spring Boot 3.x / WebFlux**
- **Spring Data R2DBC**
- **PostgreSQL** (con procedimientos almacenados plpgsql).
- **Lombok**
- **Eureka & Config Client**.

---

## Cómo instalar y ejecutar el proyecto

### Requisitos previos
1. **ms-config-server** y **registry-service** corriendo.
2. Base de datos **PostgreSQL** (db_account) disponible.
3. El script SQL `database/script.sql` debe haber sido ejecutado para crear la tabla, los índices y el procedimiento de transferencia.

### Pasos para ejecución local (Gradle)
1. Navega a la carpeta: `cd ms-account`
2. Ejecuta:
   ```bash
   ./gradlew bootRun
   ```

### Pasos para ejecución con Docker
```bash
docker-compose up -d ms-account
```

---

## Cómo utilizar el proyecto

### Endpoints (v1)
- **Listar todos:** `GET /api/v1/accounts`
- **Buscar por ID:** `GET /api/v1/accounts/{id}`
- **Buscar por Cliente:** `GET /api/v1/accounts/customer/{customerId}`
- **Crear cuenta:** `POST /api/v1/accounts`
- **Transferencia:** `POST /api/v1/accounts/transfer` (Requiere un `TransferRequest` en el body).

### Documentación API
La especificación completa está en:
- `swagger/account-swagger.yaml`

