# Repository Guidelines

## Project Structure & Modules
- App code lives in `app/src/main/java/com/machado001/lilol/…`; UI layouts in `app/src/main/res/layout`; strings and icons under `app/src/main/res/values*` and `app/src/main/res/drawable*`.
- Navigation graph: `app/src/main/res/navigation/nav_graph.xml`.
- Gradle config: root `build.gradle.kts`, `settings.gradle.kts`; module config in `app/build.gradle.kts`.
- Release artifacts: `app/release/`; Firebase config: `app/google-services.json`.
- It follows an MVP approach, maintain it.
- Use coroutines and flows following best practices described in books like Coroutines Deep Dive from Marcin Moskala.
- Follow code structure without alter it.

## Build, Test, and Development Commands
- `./gradlew :app:assembleDebug` — build debug APK.
- `./gradlew :app:compileDebugKotlin` — quick Kotlin compile check.
- `./gradlew :app:testDebugUnitTest` — run JVM unit tests.
- `./gradlew :app:connectedAndroidTest` — run instrumented tests (device/emulator required).

## Coding Style & Naming
- Kotlin, AndroidX, Material 3. Use 4-space indentation; prefer expression functions when clear.
- Follow existing package naming (`rotation`, `common`, `rotation.view.fragment`, etc.).
- Layout IDs in `snake_case`; fragments/activities in `PascalCase`; resources prefixed per type (e.g., `fragment_`, `item_`).
- Favor immutable vals; avoid blocking calls on main thread; use coroutines + `lifecycleScope`.

## Testing Guidelines
- Unit tests under `app/src/test/java`; instrumented tests under `app/src/androidTest/java`.
- Name tests with intent-revealing method names (e.g., `fetchRotations_returnsErrorOnUnauthenticated()`).
- Don't open any PRs.

## Security & Configuration Tips
- Keep `app/google-services.json` and signing keys out of commits. Use Play App Signing; register correct SHA-256 in Firebase for App Check.
- Network access is restricted at runtime; avoid adding new network endpoints without review.

## Agent-Specific Notes
- Prefer `rg` for search; keep edits minimal and ASCII.
- Avoid altering unrelated locales/resources; ensure added strings propagate to all locale files when needed.
- When asked to translate `strings.xml`, add the translation for every existing locale directory in `app/src/main/res` (all `values-*` folders), not just the default.
