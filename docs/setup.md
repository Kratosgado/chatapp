# Setup Guide

This guide will help you set up the project for local development.

## Prerequisites

- **Java**: JDK 21
- **Node.js**: v20+
- **Database**: PostgreSQL 14+
- **Frontend Package Manager**: `pnpm` (install with `npm install -g pnpm`)

## Backend Setup (Spring Boot)

1.  **Database Configuration**:
    Create a PostgreSQL database named `chatapp`.
    The application defaults to `postgres:postgres` on `localhost:5432`. You can override these using environment variables (see `backend/src/main/resources/application.yaml`).

2.  **Run with Gradle**:
    ```bash
    cd backend
    ./gradlew bootRun
    ```
    The server will start on `http://localhost:8080`.

3.  **Tests**:
    ```bash
    ./gradlew test
    ```

## Frontend Setup (Vue 3 + Vite)

1.  **Install Dependencies**:
    ```bash
    cd frontend
    pnpm install
    ```

2.  **Start Development Server**:
    ```bash
    pnpm dev
    ```
    The application will be available at `http://localhost:5173`.

## Environment Variables

### Backend
| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | Database host |
| `DB_PORT` | `5432` | Database port |
| `DB_NAME` | `chatapp` | Database name |
| `DB_USER` | `postgres` | Database user |
| `DB_PASS` | `postgres` | Database password |
| `JWT_SECRET_KEY` | (see yaml) | Secret key for JWT signing |

### Frontend
Create a `.env` file in `frontend/` if needed. Currently, the API URL is hardcoded or proxied via Vite config (check `vite.config.ts`).
