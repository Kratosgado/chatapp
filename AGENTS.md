# AGENTS.md

This document provides essential guidelines and commands for AI agents working on the `chatapp` repository. Please adhere to these instructions to maintain code quality and consistency.

## Project Structure

- **backend/**: Spring Boot application (Kotlin 1.9+, Java 21).
- **frontend/**: Vue 3 application (TypeScript, Nuxt UI, Vite).

---

## 1. Backend (Spring Boot + Kotlin)

**Working Directory:** `backend/`

### Build & Test Commands

- **Build Project:**
  ```bash
  ./gradlew build -x test  # Build skipping tests for speed during dev
  ```

- **Run Application:**
  ```bash
  ./gradlew bootRun
  ```

- **Run All Tests:**
  ```bash
  ./gradlew test
  ```

- **Run a Single Test Class:**
  ```bash
  ./gradlew test --tests "com.kratosgado.chatapp.auth.AuthServiceTest"
  ```

- **Run a Specific Test Method:**
  ```bash
  ./gradlew test --tests "com.kratosgado.chatapp.auth.AuthServiceTest.shouldLoginSuccessfully"
  ```

- **Clean Build:**
  ```bash
  ./gradlew clean
  ```

### Code Style Guidelines

- **Formatting:**
  - Follow standard Kotlin coding conventions.
  - Indentation: 4 spaces.
  - Line Length: Soft limit of 120 characters.
  - Use `val` for immutability whenever possible.

- **Naming Conventions:**
  - **Classes/Interfaces:** PascalCase (e.g., `AuthService`, `UserRepo`).
  - **Functions/Variables:** camelCase (e.g., `registerUser`, `emailAddress`).
  - **Constants:** UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`).
  - **Packages:** Lowercase, dot-separated (e.g., `com.kratosgado.chatapp.services`).

- **Architecture:**
  - **Controllers:** Handle HTTP requests, validate input, call services. Located in feature packages (e.g., `auth`, `users`).
  - **Services:** Contain business logic. Located in `services` or feature packages.
  - **Repositories:** Data access layer (JPA). Located in feature packages.
  - **DTOs:** Use `data class` for Data Transfer Objects. Located in `dto` sub-packages.

- **Error Handling & Responses:**
  - Use `GlobalExceptionHandler` for centralized exception handling.
  - Throw custom exceptions (e.g., `ApiException`) for business logic errors.
  - Return `ApiResponse` for consistent API responses:
    ```kotlin
    return ResponseEntity.ok(ApiResponse.okWithData("Success", data))
    ```

- **Dependencies:**
  - Manage in `build.gradle.kts`.
  - Use specific versions (avoid `+` or dynamic versions).

---

## 2. Frontend (Vue 3 + TypeScript)

**Working Directory:** `frontend/`

### Build & Run Commands

- **Install Dependencies:**
  ```bash
  pnpm install
  ```

- **Start Development Server:**
  ```bash
  pnpm dev
  ```

- **Build for Production:**
  ```bash
  pnpm build
  ```

- **Type Check:**
  ```bash
  pnpm type-check  # Uses vue-tsc
  ```

### Linting & Formatting (Biome)

- **Check Code:**
  ```bash
  npx @biomejs/biome check .
  ```

- **Fix Issues (Auto-format):**
  ```bash
  npx @biomejs/biome check --write .
  ```

### Code Style Guidelines

- **Framework:** Vue 3 with Composition API (`<script setup lang="ts">`).
- **UI Library:** Nuxt UI components.
- **Styling:** Tailwind CSS (utility-first).

- **Formatting (Biome):**
  - Indentation: 2 spaces.
  - Quotes: Single quotes.
  - Line Width: 120 characters.
  - No trailing commas in JSON-like structures if enforced by Biome.

- **Naming Conventions:**
  - **Components:** PascalCase (e.g., `LoginForm.vue`, `UserProfile.vue`).
  - **Props/Emits:** camelCase within script, kebab-case in templates.
  - **Files:** matches component name.

- **TypeScript:**
  - Avoid `any`. Use strict typing with Interfaces or Types.
  - Use `zod` for runtime validation (e.g., form inputs, API responses).
  - Define props with interfaces:
    ```typescript
    interface Props {
      title: string;
      isActive?: boolean;
    }
    const props = defineProps<Props>();
    ```

- **State Management:**
  - Use Pinia for global state.
  - Keep stores modular and located in `stores/` directory (create if needed).

---

## 3. General & Workflow

- **Git Commit Messages:**
  - Use Conventional Commits format:
    - `feat: add user registration endpoint`
    - `fix: resolve login timeout issue`
    - `docs: update API documentation`
    - `style: format code with biome`
    - `refactor: simplify auth logic`

- **Environment Variables:**
  - Backend: Copy `env.sample` to `.env` (or set env vars in run configuration).
  - Frontend: Use `.env` or `.env.local` for Vite environment variables (prefix with `VITE_`).
  - **NEVER** commit secrets or API keys.

- **File Operations:**
  - Always use absolute paths when reading/writing files.
  - Verify file existence before reading.

- **Testing Strategy:**
  - Write unit tests for business logic (Services/Utils).
  - Write integration tests for Controllers/API endpoints.
  - Ensure tests pass before submitting changes.

---

## 4. Specific Rules for Agents

- **Modifying Code:**
  - Read the file first to understand context.
  - Apply changes that match existing style.
  - Run lint/format commands after modification.

- **Creating New Files:**
  - Place them in the correct directory based on feature/module.
  - Include necessary imports and package declarations.

- **Refactoring:**
  - Ensure functionality remains unchanged.
  - Verify with tests.
