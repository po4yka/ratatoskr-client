package com.po4yka.bitesizereader.util.performance

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

/**
 * Performance monitoring utilities for tracking operation timing
 */
object PerformanceMonitor {

    /**
     * Measure execution time of a suspending function
     */
    suspend fun <T> measureSuspend(
        operation: String,
        threshold: Long = 1000, // Log warning if > 1 second
        block: suspend () -> T
    ): T {
        var result: T
        val time = measureTimeMillis {
            result = block()
        }

        if (time > threshold) {
            logger.warn { "⚠️ SLOW OPERATION: $operation took ${time}ms (threshold: ${threshold}ms)" }
        } else {
            logger.debug { "✓ $operation completed in ${time}ms" }
        }

        return result
    }

    /**
     * Measure execution time of a regular function
     */
    inline fun <T> measure(
        operation: String,
        threshold: Long = 1000,
        block: () -> T
    ): T {
        var result: T
        val time = measureTimeMillis {
            result = block()
        }

        if (time > threshold) {
            logger.warn { "⚠️ SLOW OPERATION: $operation took ${time}ms (threshold: ${threshold}ms)" }
        } else {
            logger.debug { "✓ $operation completed in ${time}ms" }
        }

        return result
    }

    /**
     * Monitor Flow execution time
     */
    fun <T> Flow<T>.measureFlow(
        operation: String,
        threshold: Long = 1000
    ): Flow<T> {
        var startTime = 0L

        return this
            .onStart {
                startTime = System.currentTimeMillis()
                logger.debug { "▶ Started: $operation" }
            }
            .onCompletion { error ->
                val duration = System.currentTimeMillis() - startTime

                if (error != null) {
                    logger.error(error) { "✗ Failed: $operation after ${duration}ms" }
                } else if (duration > threshold) {
                    logger.warn { "⚠️ SLOW FLOW: $operation took ${duration}ms (threshold: ${threshold}ms)" }
                } else {
                    logger.debug { "✓ Completed: $operation in ${duration}ms" }
                }
            }
    }
}

/**
 * Extension function for easy performance measurement
 */
suspend fun <T> measured(
    operation: String,
    threshold: Long = 1000,
    block: suspend () -> T
): T = PerformanceMonitor.measureSuspend(operation, threshold, block)

/**
 * Database query performance tracker
 */
class QueryPerformanceTracker {
    private val queryTimes = mutableMapOf<String, MutableList<Long>>()

    fun trackQuery(queryName: String, timeMs: Long) {
        queryTimes.getOrPut(queryName) { mutableListOf() }.add(timeMs)
    }

    fun getStats(queryName: String): QueryStats? {
        val times = queryTimes[queryName] ?: return null
        return QueryStats(
            queryName = queryName,
            count = times.size,
            avgTimeMs = times.average(),
            minTimeMs = times.minOrNull() ?: 0,
            maxTimeMs = times.maxOrNull() ?: 0
        )
    }

    fun getAllStats(): List<QueryStats> {
        return queryTimes.keys.mapNotNull { getStats(it) }
    }

    fun reset() {
        queryTimes.clear()
    }
}

data class QueryStats(
    val queryName: String,
    val count: Int,
    val avgTimeMs: Double,
    val minTimeMs: Long,
    val maxTimeMs: Long
)

/**
 * Memory usage tracker
 */
object MemoryMonitor {
    /**
     * Log current memory usage
     */
    fun logMemoryUsage(tag: String = "Memory") {
        // Note: Actual implementation depends on platform
        // This is a placeholder that can be implemented platform-specifically
        logger.debug { "[$tag] Memory usage: Platform-specific implementation needed" }
    }

    /**
     * Track object allocation
     */
    inline fun <reified T> trackAllocation(tag: String = T::class.simpleName ?: "Unknown"): T where T : Any {
        logger.debug { "Allocated: $tag" }
        return T::class.java.getDeclaredConstructor().newInstance()
    }
}

/**
 * Startup performance tracker
 */
object StartupTracker {
    private val checkpoints = mutableMapOf<String, Long>()
    private val startTime = System.currentTimeMillis()

    fun checkpoint(name: String) {
        val time = System.currentTimeMillis() - startTime
        checkpoints[name] = time
        logger.info { "🚀 Startup: $name at ${time}ms" }
    }

    fun getCheckpoints(): Map<String, Long> = checkpoints.toMap()

    fun logSummary() {
        val totalTime = System.currentTimeMillis() - startTime
        logger.info { "🏁 Startup complete in ${totalTime}ms" }
        checkpoints.forEach { (name, time) ->
            logger.info { "  ├─ $name: ${time}ms" }
        }
    }
}
