# Keystone Backend — Setup Guide

## Prerequisites
- JDK 21
- Eclipse IDE (2024-03 or newer recommended)
- PostgreSQL 14+ running locally
- Maven (bundled with the project via `mvnw`/`mvnw.cmd`)

## 1. Clone and import
```bash
git clone https://github.com/S-Vaishnavi/keystone.git
cd keystone
git checkout develop
```
In Eclipse: **File → Import → Maven → Existing Maven Projects** → browse to `keystone/backend` → Finish.

## 2. Install Lombok into Eclipse (REQUIRED — do this before anything else)

This project uses Lombok (`@Getter`, `@Setter`, `@AllArgsConstructor`, etc.) to reduce boilerplate.
Maven builds work fine without any extra setup, but **Eclipse's own editor/compiler will show
false "constructor undefined" / "method undefined" errors** unless Lombok is installed into Eclipse itself.

Steps:
1. After Maven downloads dependencies, locate the Lombok jar in your local repo:
   ```
   <your-user-folder>\.m2\repository\org\projectlombok\lombok\<version>\lombok-<version>.jar
   ```
2. Run it directly:
   ```bash
   java -jar "<path-to-lombok-jar>"
   ```
3. The Lombok installer GUI opens and should auto-detect your Eclipse install. Click **Install / Update**.
4. **Fully close and reopen Eclipse** (not just the workspace).
5. Right-click the project → **Project → Clean...** → clean it.

If you still see red errors like "constructor XYZ is undefined" after this, redo steps 3–5 —
it almost always means Eclipse wasn't fully restarted.

## 3. Configure environment variables

Copy the values from `.env.example` into your own local environment (do NOT commit real values):

**Eclipse:** Right-click project → **Run As → Run Configurations** → select the Spring Boot config →
**Environment** tab → add:
- `DB_USERNAME` = your local postgres username (default: `postgres`)
- `DB_PASSWORD` = your local postgres password

## 4. Create your local database
```sql
CREATE DATABASE keystone_dev;
```

## 5. Run it
Right-click project → **Run As → Spring Boot App**, or:
```bash
./mvnw spring-boot:run
```

You should see Flyway apply migrations, Hibernate validate entities, and Tomcat start on port 8080.
Swagger UI will be available at `http://localhost:8080/swagger-ui.html`.

## 6. Maven → Update Project after pulling new code

Whenever you `git pull` and see new dependencies in `pom.xml`, or new MapStruct mappers,
right-click project → **Maven → Update Project...** (check "Force Update") to regenerate everything cleanly.
