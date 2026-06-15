# Inventory Core Services

Spring Boot core service for inventory monitoring, built like the demo `coreservices` project.

Includes JWT authentication, role-based access control, user management, inventory CRUD operations, business summary logic, PostgreSQL mappings, and Swagger.

## Run

```bash
mvnw.cmd spring-boot:run
```

Run without local PostgreSQL by using the H2-backed local profile:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

## PostgreSQL

Default database settings are in `src/main/resources/application.properties`:

- Database: `inventory_monitoring_db`
- Username: `postgres`
- Password: `postgres`
- Port: `5432`

You can override them with environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`

## URLs

- Auth API: `http://localhost:8001/users/signin`
- Inventory API: `http://localhost:8001/inventory/items`
- Spring Swagger UI: `http://localhost:8001/swagger-ui.html`

Seeded login:

- Email: `admin@inventory.com`
- Password: `admin123`

Role notes:

- `1` = Admin / inventory manager
- `2` = Standard user
- Inventory create/update/delete/adjust and user management require an admin JWT in the `Token` header.
