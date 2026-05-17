---
title: Replace hand-rolled date math with kotlinx-datetime
status: backlog
area: kmp
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Replace hand-rolled date math with kotlinx-datetime #repo/ratatoskr-client #area/kmp #status/backlog 🔼

Filed from the 2026-05-17 deep audit (code M1).

## Objective

`feature/settings/.../data/repository/ReadingGoalRepositoryImpl.kt:105-177` reimplements `getCurrentDateString`, `minusDays`, `dateToEpochDays`, `epochDaysToDateString`, `isLeapYear`, `leapYearsBefore` from scratch — ~70 lines of Gregorian math with int-overflow risk on `(year - 1970) * 365` for far-future dates and a `Long → Int` cast at `:149`. `kotlinx-datetime` is already on the classpath; `LocalDate.toEpochDays`, `LocalDate.parse(...).minus(DatePeriod)`, etc. reduce this to ~5 lines.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Replace the 6 hand-rolled helpers with `kotlinx-datetime` equivalents.
- Add unit tests covering the year-boundary, leap-year, and large-`days` cases the helpers were trying to handle.

## Constraints

- Same output format (`yyyy-MM-dd` strings).
- Preserve timezone behavior (UTC vs local — the current code is ambiguous; clarify and document).

## Definition of done

- ~70 lines deleted.
- Tests cover Feb 29 / Mar 1, year 2100 (non-leap), 1000-day deltas.
- No overflow at year 3000+.
