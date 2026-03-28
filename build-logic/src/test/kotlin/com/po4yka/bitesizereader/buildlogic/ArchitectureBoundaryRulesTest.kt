package com.po4yka.bitesizereader.buildlogic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArchitectureBoundaryRulesTest {
    @Test
    fun `direct koin resolution matches get getAll and getKoin`() {
        val violations =
            ArchitectureBoundaryRules.findDirectDiViolations(
                listOf(
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/presentation/navigation/Root.kt",
                        content = "val entry = koin.get<MyEntry>()",
                    ),
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/presentation/navigation/Main.kt",
                        content = "val entries = koin.getAll<FeatureRouteEntry>()",
                    ),
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/presentation/navigation/Shell.kt",
                        content = "val dep = getKoin().get<RootDependency>()",
                    ),
                ),
            )

        assertEquals(3, violations.size)
    }

    @Test
    fun `allowlisted paths can resolve dependencies directly`() {
        assertTrue(
            ArchitectureBoundaryRules.isAllowedDirectDiPath(
                "composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/App.kt",
            ),
        )
        assertTrue(
            ArchitectureBoundaryRules.isAllowedDirectDiPath(
                "composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/app/AppCompositionRoot.kt",
            ),
        )
        assertTrue(
            ArchitectureBoundaryRules.isAllowedDirectDiPath(
                "composeApp/src/androidMain/kotlin/com/po4yka/bitesizereader/worker/SyncWorker.kt",
            ),
        )
        assertFalse(
            ArchitectureBoundaryRules.isAllowedDirectDiPath(
                "composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/presentation/navigation/NavigationRegistry.kt",
            ),
        )
    }

    @Test
    fun `shell cannot import feature presentation or data implementations`() {
        val featureFiles =
            listOf(
                SourceFile(
                    path = "feature/summary/src/commonMain/kotlin/com/po4yka/bitesizereader/feature/summary/presentation/navigation/DefaultSummaryRouteEntry.kt",
                    content =
                        """
                        package com.po4yka.bitesizereader.feature.summary.presentation.navigation

                        class DefaultSummaryRouteEntry
                        """.trimIndent(),
                ),
                SourceFile(
                    path = "feature/summary/src/commonMain/kotlin/com/po4yka/bitesizereader/feature/summary/data/repository/SummaryRepositoryImpl.kt",
                    content =
                        """
                        package com.po4yka.bitesizereader.feature.summary.data.repository

                        class SummaryRepositoryImpl
                        """.trimIndent(),
                ),
            )

        val violations =
            ArchitectureBoundaryRules.findShellBoundaryViolations(
                shellFiles =
                    listOf(
                        SourceFile(
                            path = "composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/presentation/navigation/NavigationRegistry.kt",
                            content =
                                """
                                import com.po4yka.bitesizereader.feature.summary.presentation.navigation.DefaultSummaryRouteEntry
                                import com.po4yka.bitesizereader.feature.summary.data.repository.SummaryRepositoryImpl
                                """.trimIndent(),
                        ),
                    ),
                featureTypeOwners = ArchitectureBoundaryRules.buildFeatureTypeOwners(featureFiles),
            )

        assertEquals(2, violations.size)
    }

    @Test
    fun `features cannot import other feature presentation or data implementations`() {
        val featureFiles =
            listOf(
                SourceFile(
                    path = "feature/summary/src/commonMain/kotlin/com/po4yka/bitesizereader/feature/summary/presentation/viewmodel/SummaryViewModel.kt",
                    content =
                        """
                        package com.po4yka.bitesizereader.feature.summary.presentation.viewmodel

                        class SummaryViewModel
                        """.trimIndent(),
                ),
                SourceFile(
                    path = "feature/collections/src/commonMain/kotlin/com/po4yka/bitesizereader/feature/collections/data/repository/CollectionRepositoryImpl.kt",
                    content =
                        """
                        package com.po4yka.bitesizereader.feature.collections.data.repository

                        class CollectionRepositoryImpl
                        """.trimIndent(),
                ),
                SourceFile(
                    path = "feature/digest/src/commonMain/kotlin/com/po4yka/bitesizereader/feature/digest/presentation/viewmodel/DigestViewModel.kt",
                    content =
                        """
                        package com.po4yka.bitesizereader.feature.digest.presentation.viewmodel

                        import com.po4yka.bitesizereader.feature.summary.presentation.viewmodel.SummaryViewModel
                        import com.po4yka.bitesizereader.feature.collections.data.repository.CollectionRepositoryImpl

                        class DigestViewModel
                        """.trimIndent(),
                ),
            )

        val violations = ArchitectureBoundaryRules.findFeatureBoundaryViolations(featureFiles)

        assertEquals(2, violations.size)
    }
}
