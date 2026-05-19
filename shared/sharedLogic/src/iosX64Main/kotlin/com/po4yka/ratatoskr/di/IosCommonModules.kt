package com.po4yka.ratatoskr.di

import org.koin.core.module.Module
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_AuthFeatureModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_CollectionsFeatureModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_CoreCommonModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_DatabaseModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_DigestFeatureModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_NetworkModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_SettingsFeatureModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_SummaryFeatureModule
import org.koin.ksp.generated.com_po4yka_ratatoskr_di_SyncFeatureModule

internal actual object GeneratedAppModules {
    actual val coreCommon: Module = com_po4yka_ratatoskr_di_CoreCommonModule
    actual val network: Module = com_po4yka_ratatoskr_di_NetworkModule
    actual val database: Module = com_po4yka_ratatoskr_di_DatabaseModule
    actual val auth: Module = com_po4yka_ratatoskr_di_AuthFeatureModule
    actual val collections: Module = com_po4yka_ratatoskr_di_CollectionsFeatureModule
    actual val digest: Module = com_po4yka_ratatoskr_di_DigestFeatureModule
    actual val settings: Module = com_po4yka_ratatoskr_di_SettingsFeatureModule
    actual val summary: Module = com_po4yka_ratatoskr_di_SummaryFeatureModule
    actual val sync: Module = com_po4yka_ratatoskr_di_SyncFeatureModule
}
