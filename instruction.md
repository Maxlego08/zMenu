# Annotation Guidelines for API Module

## Overview
When editing or adding classes in the `API/` folder, it is mandatory to use nullability and contract annotations on all function signatures. This ensures code safety and communicates intended behavior to integrators and tools.

## Checklist for Annotation Usage

1. **Required Annotations**
   - Use `@NotNull` or `@Nullable` to indicate the nullability of return values and parameters.
   - Use JetBrains `@Contract` to describe method purity and return contracts (e.g., `pure = true`, `"_ -> this"`, etc.) where applicable.

2. **How to Annotate**
   - Place annotations directly above the function signature.
   - If the function already has a Javadoc or block comment, insert annotations between the comment and the function declaration.

3. **Interfaces & Implementations**
   - For functions defined in interfaces:
     - Review all implementing classes.
     - Choose nullability (NotNull/Nullable) based on the behavior of all implementors.
     - If implementations vary in nullability, default to `@Nullable` or refactor for consistency.

4. **Annotation Imports**
   - Use JetBrains annotations (`org.jetbrains.annotations.*`) for `@Nullable`, `@NotNull`, and `@Contract`.
   - If the project or consumer codebase uses a different annotation package, maintain consistency.

5. **Documentation**
   - If the function has a comment, do not remove or overwrite it.
   - Place annotations after the comment and before the function definition.

## Example

```java
/**
 * Returns the current user session.
 * @return the current session or null if none exists
 */
@Nullable
public Session getCurrentSession();

/**
 * Performs a reset operation.
 */
@NotNull
@Contract(pure = true, value = "-> this")
MyApi reset();
```

---
