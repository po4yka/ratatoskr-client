---
name: sqldelight-migrations
description:
  Use when modifying core/data/src/commonMain/sqldelight/com/po4yka/
  ratatoskr/database/Database.sq, adding new SQLDelight tables,
  columns, or queries, writing schema migrations under
  core/data/src/commonMain/sqldelight/migrations/, or debugging
  generated database bindings. Schema changes are sensitive — the
  project marks Database.sq "do not edit lightly".
user-invocable: false
---

# SQLDelight Schema and Migrations

SQLDelight is the only authoritative schema for the local cache.

- **Schema**:
  `core/data/src/commonMain/sqldelight/com/po4yka/ratatoskr/database/Database.sq`
- **Migrations** (one file per version step):
  `core/data/src/commonMain/sqldelight/migrations/<from>.sqm`
  (the repo currently has 1.sqm through 10.sqm; new migrations
  continue the sequence)
- **Generated bindings**: produced into `core/data/.../database/`
  on build — never check in or hand-edit them.

## Hard rules

- **Don't edit `Database.sq` lightly.** Existing tables drive the
  cache, sync layer, and on-device queries. A breaking change
  without a migration corrupts user databases on the next install.
- **Add a `.sqm` migration** for any structural change (new column
  with NOT NULL, dropped column, renamed column, changed type, new
  table that other tables reference).
- **Pure additive queries** (a new SELECT/INSERT against an existing
  shape) do **not** need a version bump — just add a labelled block
  to `Database.sq`.
- **Never bypass SQLDelight** by writing raw SQL in repository code
  for cached data. New queries go in `Database.sq` and are called
  through the generated `Queries` accessors.
- **Idempotent migrations.** A migration may be replayed during
  recovery; statements must be safe to re-execute or guarded with
  `IF NOT EXISTS` / `IF EXISTS`.

## Adding a query (no version bump)

1. Add a labelled block to `Database.sq`:
   ```sql
   selectActiveByOwner:
   SELECT * FROM summary
   WHERE ownerId = ? AND archived = 0
   ORDER BY updatedAt DESC;
   ```
2. Rebuild — SQLDelight regenerates the binding.
3. Call from the repository via the generated `summaryQueries`
   accessor. Wrap blocking calls in a coroutine dispatcher as the
   existing repos do.

## Adding a column (requires migration)

1. Update the table definition in `Database.sq` to include the new
   column, with a default that lets existing rows survive:
   ```sql
   readMinutes INTEGER NOT NULL DEFAULT 0
   ```
2. Determine the next migration version `N+1` (look at the highest
   existing `migrations/<N>.sqm`).
3. Bump the SQLDelight `version` property in
   `core/data/build.gradle.kts` to `N+1`.
4. Add `core/data/src/commonMain/sqldelight/migrations/<N>.sqm`
   containing the migration SQL:
   ```sql
   ALTER TABLE summary ADD COLUMN readMinutes INTEGER NOT NULL DEFAULT 0;
   ```
5. Update DTO mappers and domain models that read the new column.

## Dropping or renaming a column

SQLite has limited `ALTER TABLE` support. The standard recipe (which
must live inside the `.sqm` file) is:

1. Create a new table with the desired shape (`<table>_new`).
2. `INSERT INTO <table>_new SELECT … FROM <table>`.
3. `DROP TABLE <table>`.
4. `ALTER TABLE <table>_new RENAME TO <table>`.
5. Recreate indexes that lived on the old table.

Test against a fixture database with seeded rows from an earlier
schema version before merging.

## Verifying

- `./gradlew :core:data:allTests` exercises the schema and any
  query test cases.
- Generated Kotlin bindings under `core/data/build/generated/...`
  must compile cleanly — if they don't, your `.sq`/`.sqm` is the
  problem, never the generator output.
