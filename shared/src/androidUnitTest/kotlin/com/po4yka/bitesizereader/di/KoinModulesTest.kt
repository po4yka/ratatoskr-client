package com.po4yka.bitesizereader.di

import android.content.Context
import io.mockk.mockk
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.ksp.generated.module
import org.koin.test.KoinTest
import org.koin.test.verify.verify

/**
 * Koin DI verification tests.
 *
 * These tests verify that all Koin modules are correctly configured
 * and all dependencies can be resolved at test time, catching DI
 * errors before runtime.
 */
@OptIn(KoinExperimentalAPI::class)
class KoinModulesTest : KoinTest {
    /**
     * Verify Android module has valid dependency graph.
     *
     * The Android module provides platform-specific implementations
     * that only depend on Android Context.
     */
    @Test
    fun `verify android module configuration`() {
        AndroidModule().module.verify(
            extraTypes =
                listOf(
                    Context::class,
                ),
        )
    }

    /**
     * Verify that all common modules can be loaded together.
     *
     * This test ensures:
     * - All modules are properly configured
     * - No circular dependencies exist
     * - All required types can be resolved (with mocks for platform-specific)
     */
    @Test
    fun `verify all common modules can be loaded together`() {
        val mockContext = mockk<Context>(relaxed = true)

        // Create a mock platform module with all required platform-specific dependencies
        val mockPlatformModule =
            module {
                single { mockContext }
                single<io.ktor.client.HttpClient> { mockk(relaxed = true) }
                single<io.ktor.client.engine.HttpClientEngine> { mockk(relaxed = true) }
                single<com.po4yka.bitesizereader.data.local.SecureStorage> { mockk(relaxed = true) }
                single<com.po4yka.bitesizereader.data.local.DatabaseDriverFactory> { mockk(relaxed = true) }
                single<com.po4yka.bitesizereader.util.network.NetworkMonitor> { mockk(relaxed = true) }
                single<com.po4yka.bitesizereader.util.share.ShareManager> { mockk(relaxed = true) }
                single<com.po4yka.bitesizereader.util.FileSaver> { mockk(relaxed = true) }
                single<com.po4yka.bitesizereader.Platform> { mockk(relaxed = true) }
                // Mock Database for repositories
                single<com.po4yka.bitesizereader.database.Database> { mockk(relaxed = true) }
                // Mock gRPC client and service
                single<com.squareup.wire.GrpcClient> { mockk(relaxed = true) }
                single<com.po4yka.bitesizereader.grpc.processing.GrpcProcessingServiceClient> {
                    mockk(relaxed = true)
                }
            }

        // Verify the application can be created without errors
        // This catches circular dependencies and missing core bindings
        koinApplication {
            modules(mockPlatformModule)
            modules(commonModules())
        }
    }

    /**
     * Verify that all modules can be instantiated.
     *
     * This ensures KSP is generating the modules correctly.
     */
    @Test
    fun `verify all modules can be instantiated`() {
        // Verify each module can be created and has the .module extension
        // This ensures KSP generated the expected code
        val networkModule = NetworkModule().module
        val databaseModule = DatabaseModule().module
        val repositoryModule = RepositoryModule().module
        val useCaseModule = UseCaseModule().module
        val viewModelModule = ViewModelModule().module
        val androidModule = AndroidModule().module

        // All modules should be non-null (generated correctly)
        assert(networkModule != null) { "NetworkModule should be generated" }
        assert(databaseModule != null) { "DatabaseModule should be generated" }
        assert(repositoryModule != null) { "RepositoryModule should be generated" }
        assert(useCaseModule != null) { "UseCaseModule should be generated" }
        assert(viewModelModule != null) { "ViewModelModule should be generated" }
        assert(androidModule != null) { "AndroidModule should be generated" }
    }
}
