package com.po4yka.ratatoskr.util.error

import kotlin.coroutines.cancellation.CancellationException

/**
 * Domain-aware variant of [kotlin.runCatching].
 *
 * Differences from the stdlib helper:
 *  - [CancellationException] is **rethrown** rather than wrapped, so structured concurrency keeps
 *    working — a parent scope that cancels its children still observes the cancellation. The
 *    stdlib helper catches `Throwable`, which makes it unsafe inside `coroutineScope` /
 *    `viewModelScope.launch { ... }` blocks.
 *  - Every other [Throwable] is captured in a [Result.failure], including [AppError] subtypes,
 *    so callers can map the failure to a user-facing message via [Throwable.toUserMessage].
 *
 * Wrapping the broad catch here means call sites no longer need
 * `@Suppress("TooGenericExceptionCaught")`: there is exactly one wide catch in the entire
 * codebase (this one), and it is small enough to audit.
 *
 * Typical migration:
 *
 * ```kotlin
 * // before — required @Suppress("TooGenericExceptionCaught") on the function
 * try {
 *     val value = work()
 *     onSuccess(value)
 * } catch (e: Exception) {
 *     onFailure(e.toUserMessage())
 * }
 *
 * // after — no suppression, same behavior
 * runCatchingDomain { work() }
 *     .onSuccess(::onSuccess)
 *     .onFailure { e -> onFailure(e.toUserMessage()) }
 * ```
 *
 * The helper is `inline` so the `block` is not box-allocated and stack traces stay readable.
 */
@Suppress("TooGenericExceptionCaught")
inline fun <T> runCatchingDomain(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (ce: CancellationException) {
        throw ce
    } catch (t: Throwable) {
        Result.failure(t)
    }
