# SummaryDetailViewModel Delegation Refactor — Design Spec

**Date:** 2026-03-17
**Status:** Approved for implementation

## Problem

`SummaryDetailViewModel` (530 lines, 22 constructor dependencies) violates single-responsibility by managing seven distinct concerns in one class: content loading, reading session tracking, audio playback, highlights/annotations, feedback/re-summarize, collection management, and reading preferences. This makes the class hard to reason about, test, and extend.

The codebase already has a working solution: `SettingsViewModel` uses the delegate pattern to decompose similarly complex state into focused collaborators. This spec applies that same pattern to `SummaryDetailViewModel`.

## Design Overview

Introduce 4 nested sub-states in `SummaryDetailState` and extract 5 `@Factory` delegate classes, each owning one concern. The ViewModel drops from 22 constructor dependencies to 11.

---

## State Architecture

### Modified `SummaryDetailState`

```kotlin
data class SummaryDetailState(
    // Core content (stays flat)
    val summary: Summary? = null,
    val isLoading: Boolean = false,
    val isLoadingContent: Boolean = false,
    val error: String? = null,
    val lastReadPosition: Int = 0,
    val lastReadOffset: Int = 0,
    // Reading preferences (stays flat — only 2 fields)
    val readingPreferences: ReadingPreferences = ReadingPreferences(),
    val showReadingSettings: Boolean = false,
    // Audio (AudioPlaybackState? already exists as a model — stays as-is)
    val audioState: AudioPlaybackState? = null,
    // Nested sub-states (NEW)
    val session: ReadingSessionState = ReadingSessionState(),
    val highlights: HighlightState = HighlightState(),
    val feedback: FeedbackState = FeedbackState(),
    val collection: CollectionDialogState = CollectionDialogState(),
)
```

### New Sub-State Classes

**`ReadingSessionState`** — visible session progress:
```kotlin
data class ReadingSessionState(
    val isSessionPaused: Boolean = false,
    val currentSessionDurationSec: Int = 0,  // Currently has no UI consumer; exposed for future use
)
```

**`HighlightState`** — highlights and annotation editing:
```kotlin
data class HighlightState(
    val highlights: List<Highlight> = emptyList(),
    val highlightedNodeOffsets: Set<Int> = emptySet(),
    val isHighlightModeActive: Boolean = false,
    val editingAnnotationHighlightId: String? = null,
    val annotationDraft: String = "",
)
```

**`FeedbackState`** — feedback submission and re-summarize flow.

> **Migration note:** The flat `SummaryDetailState` currently has a field `val feedback: SummaryFeedback?` (the domain model). This field is absorbed into `FeedbackState.feedback` and the flat field is removed. All UI references to `state.feedback` (the domain model) must be updated to `state.feedback.feedback`.

```kotlin
data class FeedbackState(
    val feedback: SummaryFeedback? = null,
    val showFeedbackDialog: Boolean = false,
    val isSubmittingFeedback: Boolean = false,
    val isResummarizing: Boolean = false,
    val resummarizeProgress: Float = 0f,
    val resummarizeStage: ProcessingStage = ProcessingStage.UNSPECIFIED,
    val resummarizeError: String? = null,
    val showResummarizeConfirmDialog: Boolean = false,
)
```

**`CollectionDialogState`** — add-to-collection dialog:
```kotlin
data class CollectionDialogState(
    val showDialog: Boolean = false,
    val collections: List<Collection> = emptyList(),
    val isLoading: Boolean = false,
    val isAdding: Boolean = false,
    val error: String? = null,
)
```

---

## Delegates

All delegates are `@Factory`-annotated and follow the same calling convention as existing delegates (`TelegramLinkingDelegate`, `SyncSettingsDelegate`, etc.): methods receive `scope: CoroutineScope`, `currentState: () -> SubState`, and `onState: (SubState) -> Unit`.

### `ReadingSessionDelegate`

**Constructor deps:** `StartReadingSessionUseCase`, `EndReadingSessionUseCase`, `SaveReadPositionUseCase`, `MarkSummaryAsReadUseCase`

**Responsibilities:**
- Manages private timing vars: `activeSessionId`, `sessionStartTime`, `lastScrollTime`, `isSessionPaused`
- Calls `markSummaryAsReadUseCase` inside `startSession()` when summary is unread (co-located with session start)
- Exposes `checkInactivity()` — the ViewModel's `init` runs the 60s timer loop and calls this method; the loop does NOT live in the delegate
- Updates `ReadingSessionState.isSessionPaused` and `currentSessionDurationSec`

**Key methods:** `startSession(summaryId, isRead, scope, onState)`, `endSession(scope)`, `notifyScrolled(currentState, onState)`, `saveReadPosition(summaryId, position, offset, scope)`, `checkInactivity(currentState, onState)`

**Timer loop (ViewModel `init`):**
```kotlin
viewModelScope.launch {
    while (true) {
        delay(60_000L)
        readingSessionDelegate.checkInactivity(
            currentState = { _state.value.session },
            onState = { sub -> _state.update { it.copy(session = sub) } },
        )
    }
}
```

**Note:** `onDestroy()` in the ViewModel calls `readingSessionDelegate.endSession()` — this wiring stays in the ViewModel.

### `AudioDelegate`

**Constructor deps:** `GenerateAudioUseCase`, `GetAudioUseCase`, `AudioPlayer`

**Responsibilities:** Audio generation, playback control (play/pause/stop), state updates on `audioState: AudioPlaybackState?`

**Calling convention exception:** Audio state is `AudioPlaybackState?` (not a sub-state class), so methods take `currentState: () -> AudioPlaybackState?` / `onState: (AudioPlaybackState?) -> Unit`.

**Key methods:** `generateAndPlayAudio`, `toggleAudioPlayback`, `stopAudio`

### `HighlightDelegate`

**Constructor deps:** `GetHighlightsUseCase`, `ToggleHighlightUseCase`, `UpdateAnnotationUseCase`

**Responsibilities:** Observing highlights flow, toggling highlight mode, managing annotation editor state.

**Key methods:** `observeHighlights(summaryId, scope, onState)`, `toggleHighlightMode`, `toggleHighlight`, `openAnnotationEditor`, `updateAnnotationDraft`, `saveAnnotation`, `closeAnnotationEditor`

### `FeedbackDelegate`

**Constructor deps:** `SubmitSummaryFeedbackUseCase`, `GetSummaryFeedbackUseCase`, `ProcessingService`

**Responsibilities:** Feedback observation, rating submission, detailed feedback submission, re-summarize flow.

**Special wiring:** `resummarize()` accepts an `onSummaryReload: (summaryId: String) -> Unit` callback so the ViewModel can reload the summary when re-summarization completes.

**Key methods:** `observeFeedback(summaryId, scope, onState)`, `rateSummary`, `dismissFeedbackDialog`, `submitDetailedFeedback`, `openResummarizeConfirmDialog`, `dismissResummarizeConfirmDialog`, `resummarize`

### `CollectionDelegate`

**Constructor deps:** `CollectionRepository`, `AddToCollectionUseCase`

**Responsibilities:** Loading collections list, managing add-to-collection dialog state, performing the add operation.

**Key methods:** `showAddToCollection`, `dismissAddToCollection`, `addToCollection`

---

## Modified ViewModel

```
SummaryDetailViewModel constructor (11 deps, was 22):
  - ReadingSessionDelegate
  - AudioDelegate
  - HighlightDelegate
  - FeedbackDelegate
  - CollectionDelegate
  - GetSummaryByIdUseCase
  - GetSummaryContentUseCase
  - RefreshFullContentUseCase
  - DeleteSummaryUseCase
  - ToggleFavoriteUseCase
  - ReadingPreferencesRepository
```

`loadSummary()` stays in the ViewModel — it coordinates all delegates:
1. Resets state: `_state.value = SummaryDetailState(isLoading = true)` — this zeros out all nested sub-states, which is correct when navigating to a new summary
2. Calls `readingSessionDelegate.startSession(summaryId, summary.isRead, ...)` — the delegate handles `markSummaryAsReadUseCase` internally
3. Calls `highlightDelegate.observeHighlights()` to subscribe to the highlights flow
4. Calls `feedbackDelegate.observeFeedback()` to subscribe to the feedback flow

The `init` block in the ViewModel retains the `readingPreferencesRepository.getPreferences()` flow subscription and the 60-second inactivity timer loop (the timer delegates actual logic to `ReadingSessionDelegate.checkInactivity()`).

The `saveReadPosition()` pass-through in the ViewModel must call `readingSessionDelegate.endSession()` before delegating to `readingSessionDelegate.saveReadPosition()`, preserving the existing cross-concern sequencing.

All other public methods become thin pass-throughs to the appropriate delegate.

---

## Files Changed

**New files (9):**
- `presentation/state/ReadingSessionState.kt`
- `presentation/state/HighlightState.kt`
- `presentation/state/FeedbackState.kt`
- `presentation/state/CollectionDialogState.kt`
- `presentation/viewmodel/ReadingSessionDelegate.kt`
- `presentation/viewmodel/AudioDelegate.kt`
- `presentation/viewmodel/HighlightDelegate.kt`
- `presentation/viewmodel/FeedbackDelegate.kt`
- `presentation/viewmodel/CollectionDelegate.kt`

**Modified files:**
- `presentation/state/SummaryDetailState.kt` — restructured
- `presentation/viewmodel/SummaryDetailViewModel.kt` — refactored
- UI composables in `composeApp/` that access renamed state fields (e.g. `state.highlights` → `state.highlights.highlights`, `state.showFeedbackDialog` → `state.feedback.showFeedbackDialog`)

---

## Constraints

- All delegates must use `@Factory` (not `@Single`) — same lifecycle as ViewModel
- No `module { }` DSL — annotations only
- All existing public method signatures on `SummaryDetailViewModel` are preserved (no breaking change for UI layer)
- The `onDestroy()` hook remains in the ViewModel, calling `readingSessionDelegate.endSession()`
- `SummaryDetailComponent.kt` is not changed — it wires to `viewModel` directly

---

## Verification

After implementation, run:
```bash
./gradlew :shared:allTests ktlintCheck detekt
```

All tests must pass and no new lint/detekt violations should be introduced.

---

## Out of Scope

- `ProxyRepository`/`NotificationRepository` inlining — rejected (adds `runCatching` error wrapping, consistent with all 17 other repositories)
- Highlights sync backend implementation — blocked on missing backend API
