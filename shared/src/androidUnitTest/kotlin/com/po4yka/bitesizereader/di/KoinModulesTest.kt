package com.po4yka.bitesizereader.di

import android.content.Context
import io.mockk.mockk
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinApplication
import org.koin.ksp.generated.module
import org.koin.test.KoinTest
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class KoinModulesTest : KoinTest {
    @Test
    fun `verify android platform module configuration`() {
        AndroidModule().module.verify(
            extraTypes = listOf(Context::class),
        )
    }

    @Test
    fun `verify split common modules can be loaded together`() {
        val mockContext = mockk<Context>(relaxed = true)

        koinApplication {
            properties(mapOf("androidContext" to mockContext))
            modules(appModules())
        }
    }

    @Test
    fun `verify split feature modules are instantiated`() {
        val modules =
            listOf(
                NetworkModule().module,
                DatabaseModule().module,
                AuthFeatureModule().module,
                CollectionsFeatureModule().module,
                DigestFeatureModule().module,
                SettingsFeatureModule().module,
                SummaryFeatureModule().module,
                SyncFeatureModule().module,
            )

        modules.forEach { module ->
            assert(module != null)
        }
    }
}
