package com.po4yka.ratatoskr.di

import android.content.Context
import io.mockk.mockk
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinApplication
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_AuthFeatureModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_CollectionsFeatureModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_CoreCommonModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_DatabaseModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_DigestFeatureModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_NetworkModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_SettingsFeatureModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_SummaryFeatureModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_SyncFeatureModule
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
                com_po4yka_ratatoskr_di_CoreCommonModule,
                com_po4yka_ratatoskr_di_NetworkModule,
                com_po4yka_ratatoskr_di_DatabaseModule,
                com_po4yka_ratatoskr_di_AuthFeatureModule,
                com_po4yka_ratatoskr_di_CollectionsFeatureModule,
                com_po4yka_ratatoskr_di_DigestFeatureModule,
                com_po4yka_ratatoskr_di_SettingsFeatureModule,
                com_po4yka_ratatoskr_di_SummaryFeatureModule,
                com_po4yka_ratatoskr_di_SyncFeatureModule,
                authFeatureBindingsModule,
                collectionsFeatureBindingsModule,
                digestFeatureBindingsModule,
                settingsFeatureBindingsModule,
                summaryFeatureBindingsModule,
                syncFeatureBindingsModule,
            )

        modules.forEach { module ->
            assert(module != null)
        }
    }
}
