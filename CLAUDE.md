# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Additionally, refer to `CLAUDE.local.md` where available for individual guidelines such as code style and commit message conventions.

## Project Overview

This is a Spring Boot backend application for a growth-oriented study service (성장형 스터디 서비스). The application uses JWT-based authentication with custom security filters, JPA for database access, and Redis for refresh token management.

**Key Technologies:**
- Java 21
- Spring Boot 3.5.6
- Spring Security with JWT (OAuth2 Resource Server)
- Spring Data JPA with MySQL (H2 for development)
- Redis for refresh token storage
- Gradle build system
- SpringDoc OpenAPI (Swagger) for API documentation

## Build and Development Commands

### Building and Running
```bash
# Build the project
./gradlew build

# Run the application (development mode)
./gradlew bootRun

# Build JAR file
./gradlew bootJar

# Clean build directory
./gradlew clean
```

### Testing
```bash
# Run all tests
./gradlew test

# Run tests with info logging
./gradlew test -i

# Test results are located in: build/test-results/test-junit-xml/
```

### Database Setup
- **Production:** MySQL (configured via environment variables)
- **Development:** MySQL or H2 (H2 console available at `/h2-console` in dev profile)
- The application uses `spring.jpa.hibernate.ddl-auto=update` in production and `create` in development

### Running Locally
1. Ensure MySQL and Redis are running
2. Update `src/main/resources/application-dev.properties` with local credentials
3. Generate JWT keys if needed (see JWT Keys section)
4. Run `./gradlew bootRun`

## High-Level Architecture

### Package Structure

The codebase follows a domain-driven package structure under `com.example.growingstudy`:

- **`auth`** - User registration, authentication, and account management
- **`security`** - JWT authentication infrastructure, filters, and configurations
- **`studygroup`** - Study group creation and management
- **`session`** - Study sessions, checklists, and submissions
- **`groupsub`** - Group-related features (notices, study time tracking)
- **`coffee`** - Coffee-based reward/progress system
- **`global`** - Global exception handlers and cross-cutting concerns

### Security Architecture

The application uses a custom JWT-based authentication flow with three key filters in the security chain:

1. **`RegenerateTokensFilter`** (before login) - Handles token refresh requests at `/api/auth/refresh`
   - Validates refresh token from Redis
   - Generates new access + refresh token pair
   - Located before `JsonAuthenticationProcessingFilter` in filter chain

2. **`JsonAuthenticationProcessingFilter`** (replaces UsernamePasswordAuthenticationFilter) - Handles login at `/api/auth/login`
   - Accepts JSON body with username/password
   - On success: delegates to `LoginSuccessHandler` which generates JWT tokens
   - Returns both access and refresh tokens

3. **`CheckAccessTokenFilter`** (after BearerTokenAuthenticationFilter) - Validates access tokens
   - Runs after Spring's built-in Bearer token processing
   - Retrieve the token object from the SecurityContext and validate the access token
   - Enforces token type is "access" (not "refresh")

**Important Security Details:**
- Refresh tokens are stored in Redis with user ID as key
- Only one valid refresh token per user (new tokens invalidate old ones)
- Access tokens last 60 minutes, refresh tokens last 1 hour
- All endpoints except `/api/auth/**` require authentication
- JWT keys are RSA 2048-bit keypairs (private_key.pem / public_key.pem)

**Filter Registration:**
- All custom filters are explicitly disabled from servlet container registration via `FilterRegistrationBean.setEnabled(false)` in `AuthConfig` to prevent double registration
- They are only registered in the Spring Security filter chain

### JWT Keys

The application requires RSA key pairs for JWT signing:
- **Location (production):** Configured via `JWT_PUBLIC_KEY_LOCATION` and `JWT_PRIVATE_KEY_LOCATION` environment variables
- **Location (development):** `src/main/resources/private_key.pem` and `public_key.pem`
- **CI/CD:** GitHub Actions workflow automatically generates ephemeral keys for testing

To generate keys locally:
```bash
mkdir -p ./keys
openssl genpkey -algorithm RSA -out ./keys/private_key.pem -pkeyopt rsa_keygen_bits:2048
chmod 600 ./keys/private_key.pem
openssl rsa -in ./keys/private_key.pem -pubout -out ./keys/public_key.pem
chmod 644 ./keys/public_key.pem
```

### Entity Relationships

Key domain relationships:
- `Account` (users) ↔ `GroupMember` ↔ `StudyGroup` (many-to-many through join entity)
- `StudyGroup` → many `Session` (study sessions with order, title, dates)
- `Session` → many `Checklist` (checklist items per session)
- `Account` + `Checklist` → `Submission` (member's checklist submission)
- `StudyGroup` → `TotalStudyTime` (aggregated time per member)
- `Account` + `StudyGroup` → many `StudyTimeLog` (individual time log entries)
- `StudyGroup` → `GroupCoffee` (coffee reward progress tracking)

### Configuration Profiles

- **Production** (`application.properties`): MySQL, Redis from environment variables, port 80
- **Development** (`application-dev.properties`): MySQL or H2, port 8080, detailed logging, H2 console enabled

Note: The main application class has a comment indicating `SecurityAutoConfiguration` was previously excluded during early development, but this is no longer the case as security is now fully implemented.

## API Documentation

Swagger UI is available at `/swagger-ui.html` (security is disabled for Swagger endpoints)

## CI/CD

GitHub Actions workflow (`.github/workflows/workflow.yml`) runs on PRs to `main` or `dev`:
- Sets up MySQL 8.0.43 and Redis 7 as services
- Generates ephemeral JWT keypairs for testing
- Runs `./gradlew test`
- Publishes test reports using dorny/test-reporter

## Rules

- Do not perform `git commit` or `git push`; files will be committed manually.
- Always refer to `CLAUDE.local.md` (if available) for individual code styles, patterns, and commit message conventions.
- For security reasons, do not read or access the contents of `.env` (including `.env.*`) or any PEM files(private/public keys).
- Always ask for clarification whenever there is any ambiguity.
- Provide a brief summary of changes after completing a task.
- If modifying existing code, explain what changed and why.