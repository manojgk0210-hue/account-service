# account-service

Minimal account management service for the banking microservice practice project.
It validates auth-service JWTs locally with the same Base64-decoded JJWT signing key;
it never calls auth-service. Use a `JWT_SECRET` identical to auth-service's value.

## Folder structure

```text
account-service/
├── db/create-accountdb.sql
├── src/main/java/com/example/accountservice/
│   ├── controller/   # REST endpoints
│   ├── dto/          # API request and response types
│   ├── exception/    # Domain errors and JSON error handler
│   ├── model/        # Account entity and enums
│   ├── repository/   # Spring Data persistence
│   ├── security/     # JWT filter and stateless security
│   └── service/      # Account business rules
├── src/main/resources/application.yml
├── src/test/java/com/example/accountservice/service/AccountServiceTest.java
├── .dockerignore
├── .env.example
├── .gitignore
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Run locally

Copy `.env.example` to `.env`, set `DB_PASSWORD`, and set `JWT_SECRET` to the exact
Base64 secret used by auth-service. Then run:

```sh
docker compose up --build
```

The API listens on `http://localhost:8081/api/accounts`; PostgreSQL is available on
host port `5433`. `ddl-auto: update` is for development only; production should use Flyway.

If using the existing Kubernetes PostgreSQL pod, execute the database setup from the
directory containing this file after replacing the SQL password placeholder:

```sh
kubectl exec -i postgres-0 -n dev -- psql -U authuser -d authdb < db/create-accountdb.sql
```

## API examples

First log in to auth-service at `http://localhost:8080/api/auth/login` and copy the
returned token. The following commands use that token:

```sh
TOKEN='<JWT returned by auth-service>'
curl -X POST http://localhost:8081/api/accounts \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"accountType":"SAVINGS","initialDeposit":1000.00}'

curl http://localhost:8081/api/accounts -H "Authorization: Bearer $TOKEN"
curl http://localhost:8081/api/accounts/1/balance -H "Authorization: Bearer $TOKEN"
curl -X POST http://localhost:8081/api/accounts/1/deposit \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"amount":250.50}'
curl -X POST http://localhost:8081/api/accounts/1/withdraw \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"amount":100.00}'
```

The public health endpoint is `GET http://localhost:8081/api/accounts/health`.
Swagger UI is available at `/swagger-ui/index.html`.

## All source and configuration files

The project contents are organized as shown in the folder structure above. The Maven
dependencies, Docker setup, Compose database, SQL bootstrap, application settings,
API DTOs, persistence model, JWT security, controllers, exception responses, and unit
tests are each in the corresponding paths listed there.
