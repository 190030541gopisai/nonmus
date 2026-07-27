# nonmus

Full-stack video/social platform. Spring Boot 4.1 (Java 17) backend + React 19 (JSX, no TS) frontend.

## Quick start (in order)

```powershell
# 1. Infrastructure (PostgreSQL + Redis)
docker compose up -d

# 2. Local S3 emulator + buckets
floci start
aws s3api create-bucket --bucket nonmus-profile-pics --region us-east-1
aws s3api create-bucket --bucket nonmus-channel --region us-east-1
aws s3api put-bucket-cors --bucket nonmus-profile-pics --cors-configuration file://cors.json
aws s3 cp src/main/resources/static/default_profile_picture.png s3://nonmus-profile-pics/users/default-avatar.png

# 3. Backend
mvnw.cmd spring-boot:run       # or: mvnw.cmd compile, mvnw.cmd test

# 4. Frontend (separate terminal)
cd frontend/nonmus
npm install                     # already done; use if deps change
npm run dev                     # Vite on :5173
```

## Commands

| What | How |
|---|---|
| Run all backend tests (with JaCoCo coverage) | `mvnw.cmd test` |
| Run backend | `mvnw.cmd spring-boot:run` |
| Compile backend | `mvnw.cmd compile` |
| Frontend dev server | `cd frontend/nonmus && npm run dev` |
| Frontend lint | `cd frontend/nonmus && npm run lint` |
| Frontend build | `cd frontend/nonmus && npm run build` |

No frontend test framework exists (no vitest/jest/cypress).

## Architecture

- **Backend**: modular monolith under `com.nonmus.nonmus.modules/{auth,user,channel,common}`
- **Frontend**: feature folders under `frontend/nonmus/src/features/{auth,home,videos,reels,channels,profile}`
- **API base**: `/api/v1` (public: `/auth/**`, `/email/verify`, `/forgot-password/**`)
- **Auth**: JWT in httpOnly cookies (access 60min, refresh 7d/30d). Frontend calls `/users/me`; on 401 calls `/auth/refresh`.
- **Storage**: S3 presigned URLs via floci emulator (`http://localhost.floci.io:4566`)
- **Caching**: Redis (localhost:6379)
- **State**: React Context (AuthProvider), no Redux/Zustand

## Project quirks

- **Windows**: use `mvnw.cmd` (not `./mvnw`). Unix: `./mvnw`
- **No TypeScript** -- JSX/JS only. No tsconfig. ESLint flat config for JSX.
- **React Compiler** enabled via `@rolldown/plugin-babel` + `babel-plugin-react-compiler`
- **Tests use H2 in-memory** (`create-drop`), main uses PostgreSQL (`ddl-auto: update`). Tests work without Docker.
- **Lombok** requires annotation processing (configured in pom.xml via maven-compiler-plugin)
- **JaCoCo** coverage report generated automatically during `mvnw.cmd test`
- **Required env vars**: `JWT_SECRET`, Google OAuth2 `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID/SECRET`, Gmail `SPRING_MAIL_USERNAME/PASSWORD`, `DB_PASSWORD` (defaults to `root`)
- **Email verification link** hardcoded to `https://localhost:5173/verify-email?token=`
- **Tailwind CSS 4** via `@tailwindcss/vite` plugin (no `tailwind.config.js`)
- **Postman collection** at `postman_collection/Nonmus Monolith.postman_collection.json`
