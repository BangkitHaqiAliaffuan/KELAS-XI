# Kelas XI — AGENTS.md

This is a student's **learning portfolio** of ~50+ independent projects, not a single application.

## Project Structure

| Directory | Tech | Notes |
|-----------|------|-------|
| `LKS 2025/` | React + Vite, Laravel | Competition prep; has own `package.json` (bootstrap) |
| `Tugas Akhir Semester 1/` | Laravel 12, React, Android, TensorFlow | "TrashCare" waste mgmt; Dockerfile in backend |
| `Tugas PKL/` | React, Next.js 16, Laravel 13, Three.js | Internship projects |
| `Tugas PPKN/` | Next.js 16 + shadcn/ui + Tailwind v4 | Uses **pnpm** |
| `Tugas Luar RPL/` | Laravel 13, React, Next.js | Extracurricular projects |
| `Tugas Video/` | Android (Kotlin), Jetpack Compose | Android Studio projects |

## Working Here

- **No root-level scripts** — each project is self-contained. `cd` into the sub-project to work.
- **Package managers vary** — check for `pnpm-lock.yaml` or `yarn.lock` in the sub-project first.
- **Laravel versions vary** (`^8.2`–`^13.0`), **Filament versions vary** (`3.3`–`5.4`), **Vite versions vary** (`^6`–`^8`). Check the sub-project's `composer.json` / `package.json`.
- **Testing**: vitest where configured (not jest).
- **Android projects** need Gradle / Android Studio.
- `.gitignore` only ignores Firebase service accounts and Google Services JSON.

## Existing Instruction Files

- `Tugas PKL/cooking-assistant/AGENTS.md` — Next.js 16 breaking changes
- `Tugas PKL/cooking-assistant/CLAUDE.md` — references the above

Read those when working on those sub-projects.
