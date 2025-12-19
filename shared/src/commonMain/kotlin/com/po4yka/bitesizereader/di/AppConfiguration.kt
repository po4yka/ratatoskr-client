package com.po4yka.bitesizereader.di

import org.koin.core.module.Module
import org.koin.ksp.generated.module

/**
 * Environment-specific Koin configuration.
 *
 * Provides different module configurations for production and testing environments.
 * This allows easy swapping of implementations (e.g., mocked services in tests).
 *
 * Usage:
 * ```kotlin
 * // Production
 * initKoin(
 *     configuration = PlatformConfiguration(),
 *     extraModules = AppConfiguration.productionModules(),
 * )
 *
 * // Testing
 * koinApplication {
 *     modules(AppConfiguration.testModules())
 *     // Add test-specific mock modules
 * }
 * ```
 */
object AppConfiguration {
    /**
     * Production modules for the application.
     *
     * These include all real implementations for networking, database,
     * repositories, use cases, and view models.
     */
    fun productionModules(): List<Module> =
        listOf(
            NetworkModule().module,
            DatabaseModule().module,
            RepositoryModule().module,
            UseCaseModule().module,
            ViewModelModule().module,
        )

    /**
     * Test modules for unit/integration testing.
     *
     * Returns an empty list by default. Tests should provide their own
     * mock modules using Koin's test utilities:
     *
     * ```kotlin
     * @Test
     * fun myTest() = koinTest {
     *     modules(AppConfiguration.testModules() + myMockModule)
     *     // Test code
     * }
     * ```
     */
    fun testModules(): List<Module> =
        listOf(
            // Test modules can be added here
            // e.g., MockNetworkModule().module
        )

    /**
     * Core modules required for both production and testing.
     *
     * These are the fundamental modules that provide essential services
     * and can work with either real or mocked dependencies.
     */
    fun coreModules(): List<Module> =
        listOf(
            RepositoryModule().module,
            UseCaseModule().module,
            ViewModelModule().module,
        )

    /**
     * Data layer modules (network + database).
     *
     * Useful when you want to test with real network/database implementations.
     */
    fun dataModules(): List<Module> =
        listOf(
            NetworkModule().module,
            DatabaseModule().module,
        )
}
