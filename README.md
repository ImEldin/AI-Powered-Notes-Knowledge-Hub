# AI‑Powered Notes & Knowledge Hub

Version: v1.0.0 (MVP)

Full‑stack app (Spring Boot + Angular) for an AI‑assisted notes/knowledge hub. This v1 focuses on robust auth flows, secure token handling, and a styled auth UI.

## Overview

- Backend: Spring Boot, JWT access/refresh with rotation, refresh stored in HttpOnly cookies, email flows, Google OAuth.
- Frontend: Angular + Angular Material, dark neon theme, global HTTP interceptor, UX for all auth states.
- Status: MVP shipped; core auth and pages complete, dashboard placeholder added.

## Features (v1.0)

Backend
- JWT Access + Refresh tokens
  - Access token: short‑lived(HttpOnly cookie)
  - Refresh token: long‑lived
  - Rotation on refresh; old refresh invalidated.
  - Logout clears refresh cookie and jwt server‑side.
- Auth
  - Email/password login and registration 
  - Google OAuth2 login (ID token verification server‑side)
  - Provider conflict handling (blocks password login for OAuth users → 409)
- Email flows
  - Email verification token (issue + confirm)
  - Password reset token (request + apply)
- Security
  - Spring Security with stateless JWT filter
  - CORS configured for frontend origin
  - CSRF: disabled for stateless API; refresh protected by same‑site cookie policy
  - Centralized GlobalExceptionHandler with consistent JSON errors
- Validation & roles
  - Bean validation for inputs
  - Roles scaffold (e.g., USER/ADMIN ready)

Frontend
- Pages: Login, Register, Verify Email (request), Email Verification Result, Forgot Password, Reset Password, Dashboard (Coming Soon)
- Auth state service (isLoggedIn, isAuthInitialized)
- HTTP interceptor
  - 401 nuanced handling:
    - If previously authenticated → session expired → clear state + redirect
    - If login attempt → “Invalid email or password”
    - Initial /api/auth/me on load → silent (no snackbar)
  - 403/400/409/5xx surfaced via snackbar
- UI
  - Dark neon theme
  - 
Backend layers
- Controller → Service → Repository
- SecurityConfig + JWT filters
- Custom exceptions + global handler

Frontend structure
- auth/components: login, register, forgot, reset, verify-email, email-verification...
- auth/interceptors: auth.interceptor.ts
- auth/guards: auth.guard.ts, role.guard.ts...
- auth/service: auth.service.ts, auth-state.service.ts
- features/dashboard
- shared/services/notification.service.ts

## API (expected)

Auth
- POST /api/auth/register
- POST /api/auth/login            → returns access token + sets refresh cookie
- GET  /api/auth/me               → current user
- POST /api/auth/refresh          → rotates refresh cookie, returns new access token
- POST /api/auth/logout           → clears refresh cookie
- POST /api/auth/forgot-password  → sends email
- POST /api/auth/reset-password   → applies token
- GET /api/auth/verify-email     (GET ?token=...) → confirms email
- POST /api/auth/resend-verification
- POST /api/auth/google-login    → login with Google ID token

## Database & Migrations (Flyway)

- Location: backend/src/main/resources/db/migration
- Naming: V1__init.sql, V2__add_users_table.sql, V3__... (V<version>__<desc>.sql)
- Run mode: Flyway runs automatically on Spring Boot startup (spring.flyway.enabled=true)
- Config: uses app DB env vars (spring.flyway.url/user/password)
- Baseline existing DB: set spring.flyway.baseline-on-migrate=true for first run if schema exists (then remove)

Add a new migration
1) Create a new SQL file in db/migration with next version number.
2) Start backend; Flyway applies it on startup.
3) If it fails, fix SQL and bump to next version (don’t edit applied files).

## Local Setup

Prereqs
- Java 17+, Maven
- Node 18+, Angular CLI
- PostgreSQL (configure in backend)
- SMTP creds for email (or use MailHog/Dev service)

Backend
```bash
cd backend
# Configure application.yml (DB, mail, JWT, cookie)
mvn clean spring-boot:run
```

Frontend (Windows)
```bash
cd frontend
npm install
npm start
# or: npx ng serve --open
```

Default URLs
- API: http://localhost:8080
- Frontend: http://localhost:4200

## Configuration

You set values via environment variables (.env or deployment config). `application.properties` (or `application.yml`) references them using `${VAR}`.

### Required Environment Variables
- `DB_HOST`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION` — access token lifetime (e.g. `15m`, or milliseconds)
- `JWT_COOKIE_NAME` — HttpOnly access token cookie name (e.g. `jwt`)
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `MAIL_FROM`
- `MAIL_FROM_NAME`
- `GOOGLE_CLIENT_ID`
- `FRONTEND_VERIFICATION_URL` — e.g. `http://localhost:4200/auth/email-verification`
- `REFRESH_TOKEN_EXP_DAYS` — normal refresh lifetime (e.g. `7`)
- `REMEMBER_REFRESH_TOKEN_EXP_DAYS` — extended lifetime (e.g. `30`)

### Optional / Tuning
- `HIKARI_MAX_POOL_SIZE` (default `5`)
- `HIKARI_MIN_IDLE` (default `2`)

### Example .env (local)
```env
DB_HOST=localhost
DB_NAME=notes
DB_USER=notes_user
DB_PASSWORD=devpass

JWT_SECRET=super-secret-256-key
JWT_EXPIRATION=15m
JWT_COOKIE_NAME=jwt

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your@gmail.com
MAIL_PASSWORD=app-specific-pass
MAIL_FROM=your@gmail.com
MAIL_FROM_NAME=AI Notes

GOOGLE_CLIENT_ID=xxxxxxxx.apps.googleusercontent.com
FRONTEND_VERIFICATION_URL=http://localhost:4200/auth/email-verification

REFRESH_TOKEN_EXP_DAYS=7
REMEMBER_REFRESH_TOKEN_EXP_DAYS=30

HIKARI_MAX_POOL_SIZE=5
HIKARI_MIN_IDLE=2
```

### application.properties (flattened)
```properties
spring.application.name=backend
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false
logging.level.org.springframework.security=DEBUG

# Database
spring.datasource.url=jdbc:postgresql://${DB_HOST}/${DB_NAME}?sslmode=require
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# HikariCP
spring.datasource.hikari.maximum-pool-size=${HIKARI_MAX_POOL_SIZE:5}
spring.datasource.hikari.minimum-idle=${HIKARI_MIN_IDLE:2}
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Flyway
spring.flyway.url=jdbc:postgresql://${DB_HOST}/${DB_NAME}?sslmode=require
spring.flyway.user=${DB_USER}
spring.flyway.password=${DB_PASSWORD}
spring.flyway.locations=classpath:db/migration
spring.flyway.enabled=true
spring.flyway.clean-disabled=true
logging.level.org.flywaydb=INFO
logging.level.org.springframework.boot.autoconfigure.flyway=DEBUG

# Access token (HttpOnly cookie)
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=${JWT_EXPIRATION}
app.jwt.cookie-name=${JWT_COOKIE_NAME}

# Mail
app.mail.host=${MAIL_HOST}
app.mail.port=${MAIL_PORT}
app.mail.username=${MAIL_USERNAME}
app.mail.password=${MAIL_PASSWORD}
app.mail.mail_from=${MAIL_FROM}
app.mail.mail_from_name=${MAIL_FROM_NAME}

# Google OAuth
app.google.client_id=${GOOGLE_CLIENT_ID}

# Refresh token lifetimes (not HttpOnly cookie)
app.auth.refresh-token-expiration-days=${REFRESH_TOKEN_EXP_DAYS}
app.auth.remember-refresh-token-expiration-days=${REMEMBER_REFRESH_TOKEN_EXP_DAYS}

# Frontend link used in emails
app.frontend.verification-url=${FRONTEND_VERIFICATION_URL}
```

### Notes
- Access token is set as an HttpOnly cookie named `JWT_COOKIE_NAME`.
- Refresh token is returned separately (not HttpOnly) and rotated on refresh.
- Use `Secure` + appropriate `SameSite` on cookies in production.
- Flyway runs migrations automatically on startup; `ddl-auto=validate` enforces schema.
