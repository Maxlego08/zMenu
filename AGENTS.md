# AGENTS.md

> **Agentic Automation & Development Guide**
> zMenu / GroupeZ-dev

---

**Quick Reference Links:**
- [Documentation](https://docs.zmenu.dev/)
- [API Javadocs](https://javadocs.groupez.dev/zmenu)
- [Discord Support](https://discord.groupez.dev/)
- [README](./README.md)
- [Changelog](./changelog.md)

---

## 📦 Build, Lint, and Test Commands

| Task                   | Gradle Command                       | Notes                                             |
|------------------------|--------------------------------------|---------------------------------------------------|
| Build all              | `./gradlew build`                    | Output: `target/`, API jars: `target-api/`        |
| Lint (CI)              | _Super-Linter in GitHub Actions_     | On PRs/push to `main`, `develop`                  |
| Test all               | `./gradlew test`                     | JUnit 5, discovers test files by convention       |
| Test single class      | `./gradlew test --tests '*ClassName'`| Use fully qualified names as needed               |
| Test single method     | `./gradlew test --tests '*ClassName.methodName'` | Regexp/wildcard match supported      |
| Full clean             | `./gradlew clean build`               | Removes build artifacts first                     |

**CI/Automation Lint:**
- Linting runs via GitHub Actions: `super-linter`.
- Enforced on all pushes/PRs to protected branches.
- Locally, linting and formatting must match `.editorconfig` and IntelliJ IDE.

**Dependency/Gradle Guidance:**
- Strictly define all dependencies/plugins in `build.gradle.kts`. **Never** edit lock files by hand.
- Default toolchain: **Java 21** (see `toolchain` block).
- When adding dependencies, follow indentation and style of the root config.
- Output is produced in `target/` and `target-api/` folders.

**Troubleshooting:**
- Ensure you use the provided `gradlew`/`gradlew.bat` scripts, **not** system Gradle.
- Line-length, indentation, or import order are the most common causes of build/lint failures.

**Test Discovery:**
- JUnit test classes should be placed in the standard `src/test/java` directories within each module, named with `*Test.java` or `*Tests.java` by convention.

---

## 🎨 Code Style, Formatting, and Project Conventions

> Agents must strictly auto-correct all code style violations before merge or patch submission.

### General Formatting:
- **Spaces only** (`indent_style = space`), **4 spaces for Java**, **2 for YAML** (`indent_size`).
- **Line length:** max **160** chars (`max_line_length`).
- **Encoding:** UTF-8. End-of-line: unset/IDE default. No required final newline.
- **Always** use 4-space indentation for Java (see `.editorconfig` `[*.java]`), 2-space for YAML.
- **Blank lines:** Keep two between top-level elements, and one after imports/package statements.
- **IntelliJ auto-format** must be respected (`ij_*` settings in `.editorconfig` control detailed wrapping, alignment, etc).

### Imports:
- IntelliJ style: All wildcard imports (`@*`), static imports (`*`), then `javax.**`, `java.**`, then user packages (`$*`), with one blank line between groups.
- Do **not** use import wildcards unless >5 imports from the same package.
- Place imports after `package` declarations, before class definition.
- No imports for inner classes unless specifically required.

### Java Indentation, Wrapping, & Structure (from `.editorconfig`):
- Braces style: **end_of_line** (Egyptian braces: `{` on same line)
- Wrapping: Most methods, params, and arrays: do **not** wrap by default. Builder/annotation/params: wrap before elements, not after.
- Case/switch: indent by one level, case on separate line.
- Import/order/blank lines: See `ij_java_*` rules for precise handling.
- YAML files: indent with 2 spaces (see `[*.yml,*.yaml]`).

### Javadoc & Comments:
- Use `/** ... */` for Javadoc on **all public classes/methods**.
- Align params, throws, and returns vertically. Always add a blank after the description.
- Block/line comments: Space after `//`, align at column start.

### Naming Conventions:
- Classes: PascalCase. Methods/vars: camelCase. Constants: UPPER_SNAKE_CASE. Interfaces: end with -able/-Listener or semantic (`FooListener`), else PascalCase.
- Packages: all lowercase, dot-separated. Resources/configs: kebab-case, lower-case extensions.

### Spacing & Operator Rules:
- Always put spaces after commas/semicolons; before `{`; around `=`, `+`, etc.
- **No** space between method name and parenthesis: `foo(bar)`, not `foo (bar)`.
- No extra spaces inside parentheses.

### Other Style Essentials:
- Arrays/lists: no newline after `{`, one entry per line unless >80 chars.
- “Widget/annotation/parameter” lists: split lines before each element if multiline.
- Do not enforce a final newline in code or config files.
- Use `.editorconfig` and reformat via IDE or script before reviews.

---

## 📋 Error Handling & Logging
- **Always** check for null unless contractually guaranteed.
- Prefer granular `try/catch` blocks—provide **full tracebacks** for errors.
- **Never** `System.out.println`—use `fr.maxlego08.menu.zcore.logger.Logger` error/info methods.
- Log **all** exceptions at error level, with useful context, not just stack.
- For all command/argument validation, employ validation helpers and user-facing feedback.

---

## 🛠 Project Architecture Guidelines
- Multi-module: key folders are `API/`, `Common/`, and `Hooks/`.
- Shared logic goes in `Common`; API contracts and interfaces in `API`; integrations in `Hooks`.
- **No cross-module imports** except by well-defined APIs.
- Plugins/libraries must follow `build.gradle.kts` group/versioning style.
- **Java 21** required (Gradle enforced).
- Test code: keep *only* in designated test source roots.

---

## 🕵️‍♂️ Agent Protocols & Good Practices
- Run `./gradlew build` and `./gradlew test` prior to PR or merge.
- Run **specific test** before pushing: `./gradlew test --tests '*ClassName.methodName'`.
- If touching dependencies, **update version** and add a changelog entry.
- Reference official docs/readme/JavaDocs for context! See top links.
- **Respect modular boundaries:** Do not blur API/adapters/core code.
- **Commit messages:** clear and intent-driven (e.g. `fix: ...`, `feat: ...`, `refactor: ...`).
- **Prior to merge:** Ensure auto-format ({IDE} or script) matches `.editorconfig`.
- **If no AGENTS.md:** You must maintain and update this file after material changes!

## For More Info
- [Documentation](https://docs.zmenu.dev/)
- [API Javadocs](https://javadocs.groupez.dev/zmenu)
- [Discord (support)](https://discord.groupez.dev/)

---

**ALL AGENTS** must enforce these rules before checking in code, running PRs, or post-processing patches. Lint, test, and *never* introduce manual code style drift. Reference and update this file regularly!