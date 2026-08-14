# Keystone Deployment Guide

This guide provides instructions on how to set up and run the Keystone backend locally.

## Prerequisites

- **Java 21**: Ensure you have JDK 21 installed on your machine. You can verify your installation by running `java -version`.
- **PostgreSQL**: Install PostgreSQL and ensure the service is running locally.

## 1. PostgreSQL Setup

Create a local database for development:

1. Open your PostgreSQL terminal (psql) or use a GUI client like pgAdmin.
2. Create a new database named `keystone_dev`:
   ```sql
   CREATE DATABASE keystone_dev;
   ```
3. Make sure you know your local PostgreSQL username (usually `postgres`) and password.

## 2. Environment Variables

The application requires certain environment variables to be set for local execution.

1. Locate the `env.example` file in the root of the project.
2. You must configure these variables in your environment (e.g., in your IDE's Run Configuration, or by setting them in your terminal before running).

Required variables:
- `DB_USERNAME`: Your local PostgreSQL username (e.g., `postgres`).
- `DB_PASSWORD`: Your local PostgreSQL password.
- `JWT_SECRET`: A secret string used to sign JWTs (e.g., `dev-secret-change-me-please-make-this-long-enough-for-hs256`).

*Note: Do not commit your actual credentials.*

## 3. Maven Commands

Navigate to the `backend` directory where the `pom.xml` and Maven wrapper (`mvnw`) are located:

```bash
cd backend
```

Common Maven commands:
- **Clean and Build**: `mvnw clean install`
- **Skip Tests during Build**: `mvnw clean install -DskipTests`
- **Clean the target directory**: `mvnw clean`

## 4. Local Execution

To run the application locally using the Maven Spring Boot plugin, navigate to the `backend` directory and execute:

```bash
# On Windows
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

# On macOS/Linux
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Alternatively, you can build the JAR file and run it directly:

```bash
mvnw clean package
java -jar target/keystone-0.0.1-SNAPSHOT.jar
```
*(Make sure your environment variables are exported in the terminal where you run this command)*
