# Database Concepts Lab — Spring Boot + PostgreSQL

A hands-on learning project for the 10 database concepts:
1. Indexing
2. Transactions and ACID
3. Isolation levels and locking
4. Query optimization
5. Pagination
6. Sharding and partitioning
7. Replication and scaling
8. Idempotency and data integrity
9. Normalization and schema design
10. Caching

## Stack
- Java 17
- Spring Boot 3.4.5
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Maven
- Docker Compose (database only; run the Spring Boot app locally)

## 1. Start PostgreSQL
From the project root:

```bash
docker compose up -d
docker compose ps
```

PostgreSQL is exposed on host port **5433** so it won't conflict with a local PostgreSQL on 5432.

Credentials:
- Database: `concepts_lab`
- User: `lab_user`
- Password: `lab_password`
- Host port: `5433`

The initialization script seeds:
- 20,000 customers
- 60,000 orders
- 40,000 payments

The init script runs only when the volume is created for the first time. To completely recreate the lab database and seed data:

```bash
docker compose down -v
docker compose up -d
```

Warning: `down -v` deletes this lab database's Docker volume and all its data.

## 2. Run the Spring Boot app
Requires JDK 17+ and Maven 3.9+.

```bash
mvn spring-boot:run
```

Base URL: `http://localhost:8080`

Try:
```text
GET http://localhost:8080/actuator/health
GET http://localhost:8080/api/customers/1
GET http://localhost:8080/api/customers?page=0&size=20
GET http://localhost:8080/api/customers?page=5&size=20
GET http://localhost:8080/api/customers/by-city?city=Bengaluru&page=0&size=20
GET http://localhost:8080/api/customers/by-email?email=customer100@example.com
```

## 3. Database client
Connect with DBeaver, pgAdmin, or IntelliJ Database tool:
- Host: `localhost`
- Port: `5433`
- Database: `concepts_lab`
- Username: `lab_user`
- Password: `lab_password`

Verify row counts:
```sql
SELECT COUNT(*) FROM customers; -- 20000
SELECT COUNT(*) FROM orders;    -- 60000
SELECT COUNT(*) FROM payments;  -- 40000
```

## 4. First lab: indexing (do this before adding any index)
Run in the SQL console:

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT *
FROM customers
WHERE city = 'Bengaluru';
```

Then add an index:
```sql
CREATE INDEX idx_customers_city ON customers(city);
ANALYZE customers;

EXPLAIN (ANALYZE, BUFFERS)
SELECT *
FROM customers
WHERE city = 'Bengaluru';
```

Important: PostgreSQL may still choose a sequential scan if the query returns a large percentage of the table. This is expected; an index is not automatically faster for every query. Compare execution plans and timings, and do not treat one run's timing as a benchmark.

Composite index experiment:
```sql
CREATE INDEX idx_customers_city_status
ON customers(city, status);

EXPLAIN (ANALYZE, BUFFERS)
SELECT id, full_name, email
FROM customers
WHERE city = 'Bengaluru' AND status = 'ACTIVE'
ORDER BY id
LIMIT 20;
```

Compare with the query filtering only on `status`, and discuss why column order matters.

## Learning workflow
We'll implement each topic incrementally in this same project:
1. Run the experiment.
2. Understand the SQL plan and behavior.
3. Add the Spring Boot code for the concept.
4. Test normal and edge cases.
5. Review interview questions and trade-offs.

## Current starter scope
This starter currently includes the schema, sample data, customer entity/repository/controller, basic offset pagination, and SQL experiments for indexing. The remaining concepts are intended to be implemented step by step so each one is understandable and testable.
