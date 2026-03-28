# Build Migration Deferred

The AGP 9 and Kotlin Multiplatform plugin migration is intentionally deferred from this architecture refactor.

This pass changes ownership, module boundaries, navigation contracts, and transport placement. It does not change the current Android/KMP plugin wiring, compatibility flags, or convention-plugin setup.

The migration follow-up should cover:

- replacing legacy Android library plugin usage in KMP modules
- removing temporary AGP compatibility flags from `gradle.properties`
- validating configuration-cache and CI behavior after the plugin switch

Do not block architecture cleanup work on those build-tooling warnings.
