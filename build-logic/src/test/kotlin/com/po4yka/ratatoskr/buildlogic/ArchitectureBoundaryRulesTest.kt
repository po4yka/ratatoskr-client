package com.po4yka.ratatoskr.buildlogic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArchitectureBoundaryRulesTest {
    @Test
    fun `direct koin resolution matches get getAll and getKoin but ignores comments`() {
        val violations =
            ArchitectureBoundaryRules.findDirectDiViolations(
                listOf(
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/navigation/Root.kt",
                        content = "val entry = koin.get<MyEntry>()",
                    ),
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/navigation/Main.kt",
                        content = "val entries = koin.getAll<FeatureRouteEntry>()",
                    ),
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/navigation/Shell.kt",
                        content = "val dep = getKoin().get<RootDependency>()",
                    ),
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/navigation/CommentOnly.kt",
                        content = "// inject() should not count inside comments",
                    ),
                ),
            )

        assertEquals(3, violations.size)
    }

    @Test
    fun `allowlisted paths can resolve dependencies directly`() {
        assertTrue(
            ArchitectureBoundaryRules.isAllowedDirectDiPath(
                "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/App.kt",
            ),
        )
        assertTrue(
            ArchitectureBoundaryRules.isAllowedDirectDiPath(
                "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/app/AppCompositionAssembly.kt",
            ),
        )
        assertTrue(
            ArchitectureBoundaryRules.isAllowedDirectDiPath(
                "composeApp/src/iosMain/kotlin/com/po4yka/ratatoskr/IosAppHost.kt",
            ),
        )
        assertTrue(
            ArchitectureBoundaryRules.isAllowedDirectDiPath(
                "androidApp/src/main/kotlin/com/po4yka/ratatoskr/worker/SyncWorker.kt",
            ),
        )
        assertFalse(
            ArchitectureBoundaryRules.isAllowedDirectDiPath(
                "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/navigation/NavigationRegistry.kt",
            ),
        )
    }

    @Test
    fun `raw AppRoute creation is only allowed in owner route helpers`() {
        val violations =
            ArchitectureBoundaryRules.findRawAppRouteCreationViolations(
                listOf(
                    SourceFile(
                        path = "feature/summary/src/commonMain/kotlin/com/po4yka/ratatoskr/feature/summary/navigation/SummaryRoutes.kt",
                        content = "fun detail(id: String) = AppRoute(\"summary\", \"detail\", id)",
                    ),
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/navigation/MainComponent.kt",
                        content = "val route = AppRoute(featureId = \"summary\", screenId = \"detail\")",
                    ),
                    SourceFile(
                        path = "core/navigation/src/commonMain/kotlin/com/po4yka/ratatoskr/navigation/MainNavigation.kt",
                        content = "data class AppRoute(val featureId: String, val screenId: String)",
                    ),
                ),
            )

        assertEquals(
            listOf(
                "Route ownership: composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/navigation/MainComponent.kt must use owner feature route helpers instead of raw AppRoute(...) construction",
            ),
            violations,
        )
    }

    @Test
    fun `shell cannot import feature data di or viewmodel implementations`() {
        val featureFiles =
            listOf(
                SourceFile(
                    path = "feature/summary/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/navigation/SummaryListComponent.kt",
                    content =
                        """
                        package com.po4yka.ratatoskr.presentation.navigation

                        interface SummaryListComponent
                        """.trimIndent(),
                ),
                SourceFile(
                    path = "feature/summary/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/viewmodel/SummaryListViewModel.kt",
                    content =
                        """
                        package com.po4yka.ratatoskr.presentation.viewmodel

                        class SummaryListViewModel
                        """.trimIndent(),
                ),
                SourceFile(
                    path = "feature/summary/src/commonMain/kotlin/com/po4yka/ratatoskr/data/repository/SummaryRepositoryImpl.kt",
                    content =
                        """
                        package com.po4yka.ratatoskr.data.repository

                        class SummaryRepositoryImpl
                        """.trimIndent(),
                ),
            )

        val violations =
            ArchitectureBoundaryRules.findShellBoundaryViolations(
                shellFiles =
                    listOf(
                        SourceFile(
                            path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/App.kt",
                            content =
                                """
                                import com.po4yka.ratatoskr.presentation.navigation.SummaryListComponent
                                import com.po4yka.ratatoskr.presentation.viewmodel.SummaryListViewModel
                                import com.po4yka.ratatoskr.data.repository.SummaryRepositoryImpl
                                """.trimIndent(),
                        ),
                    ),
                featureTypeOwners = ArchitectureBoundaryRules.buildFeatureTypeOwners(featureFiles),
            )

        assertEquals(2, violations.size)
    }

    @Test
    fun `features cannot import other feature presentation data or di implementations`() {
        val featureFiles =
            listOf(
                SourceFile(
                    path = "feature/summary/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/viewmodel/SummaryViewModel.kt",
                    content =
                        """
                        package com.po4yka.ratatoskr.presentation.viewmodel

                        class SummaryViewModel
                        """.trimIndent(),
                ),
                SourceFile(
                    path = "feature/collections/src/commonMain/kotlin/com/po4yka/ratatoskr/data/repository/CollectionRepositoryImpl.kt",
                    content =
                        """
                        package com.po4yka.ratatoskr.data.repository

                        class CollectionRepositoryImpl
                        """.trimIndent(),
                ),
                SourceFile(
                    path = "feature/settings/src/commonMain/kotlin/com/po4yka/ratatoskr/di/SettingsBindings.kt",
                    content =
                        """
                        package com.po4yka.ratatoskr.di

                        class SettingsBindings
                        """.trimIndent(),
                ),
                SourceFile(
                    path = "feature/digest/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/viewmodel/DigestViewModel.kt",
                    content =
                        """
                        package com.po4yka.ratatoskr.presentation.viewmodel

                        import com.po4yka.ratatoskr.presentation.viewmodel.SummaryViewModel
                        import com.po4yka.ratatoskr.data.repository.CollectionRepositoryImpl
                        import com.po4yka.ratatoskr.di.SettingsBindings

                        class DigestViewModel
                        """.trimIndent(),
                ),
            )

        val violations = ArchitectureBoundaryRules.findFeatureBoundaryViolations(featureFiles)

        assertEquals(3, violations.size)
    }

    @Test
    fun `module dependency rules reject core to feature and non allowlisted feature edges`() {
        val violations =
            ArchitectureBoundaryRules.findModuleDependencyViolations(
                listOf(
                    SourceFile(
                        path = "core/data/build.gradle.kts",
                        content = "dependencies { implementation(projects.feature.summary) }",
                    ),
                    SourceFile(
                        path = "feature/auth/build.gradle.kts",
                        content = "dependencies { implementation(projects.feature.summary) }",
                    ),
                    SourceFile(
                        path = "feature/settings/build.gradle.kts",
                        content = "dependencies { implementation(projects.feature.auth) implementation(projects.feature.summary) }",
                    ),
                ),
            )

        assertEquals(
            listOf(
                "Module boundary: core/data must not depend on feature/summary",
                "Module boundary: feature/auth must not depend on feature/summary",
            ),
            violations,
        )
    }

    @Test
    fun `composeApp cannot keep feature route ui outside shell host`() {
        val violations =
            ArchitectureBoundaryRules.findComposeAppFeatureUiViolations(
                listOf(
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/screens/MainScreen.kt",
                        content = "",
                    ),
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/screens/SummaryListScreen.kt",
                        content = "",
                    ),
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/auth/TelegramAuthScreen.kt",
                        content = "",
                    ),
                    SourceFile(
                        path = "composeApp/src/androidInstrumentedTest/kotlin/com/po4yka/ratatoskr/ui/SummaryListScreenTest.kt",
                        content = "",
                    ),
                    SourceFile(
                        path = "composeApp/src/androidDeviceTest/kotlin/com/po4yka/ratatoskr/ui/SummaryListScreenTest.kt",
                        content = "",
                    ),
                    SourceFile(
                        path = "composeApp/src/androidHostTest/kotlin/com/po4yka/ratatoskr/ui/SummaryListUiHostTest.kt",
                        content = "",
                    ),
                ),
            )

        assertEquals(
            listOf(
                "Shell UI: composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/screens/SummaryListScreen.kt must live in core/ui or an owning feature module",
                "Shell UI: composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/auth/TelegramAuthScreen.kt must live in core/ui or an owning feature module",
            ),
            violations,
        )
    }

    @Test
    fun `shell cannot import feature route ui types`() {
        val violations =
            ArchitectureBoundaryRules.findShellRouteUiImportViolations(
                listOf(
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/screens/MainScreen.kt",
                        content =
                            """
                            import com.po4yka.ratatoskr.feature.summary.ui.screens.SummaryListScreen
                            import com.po4yka.ratatoskr.feature.auth.ui.auth.TelegramAuthScreen
                            """.trimIndent(),
                    ),
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/app/AppCompositionAssembly.kt",
                        content = "import com.po4yka.ratatoskr.feature.summary.api.SummaryEntry",
                    ),
                ),
            )

        assertEquals(
            listOf(
                "Shell boundary: composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/screens/MainScreen.kt must not import feature route UI type com.po4yka.ratatoskr.feature.summary.ui.screens.SummaryListScreen",
                "Shell boundary: composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/screens/MainScreen.kt must not import feature route UI type com.po4yka.ratatoskr.feature.auth.ui.auth.TelegramAuthScreen",
            ),
            violations,
        )
    }

    @Test
    fun `route entries must not be registered through koin bindings`() {
        val violations =
            ArchitectureBoundaryRules.findDiManagedRouteRegistrationViolations(
                listOf(
                    SourceFile(
                        path = "feature/auth/src/commonMain/kotlin/com/po4yka/ratatoskr/di/AuthBindings.kt",
                        content = "module { single<AuthEntry> { createAuthEntry() } }",
                    ),
                    SourceFile(
                        path = "feature/summary/src/commonMain/kotlin/com/po4yka/ratatoskr/di/SummaryBindings.kt",
                        content = "module { single { summaryEntry() } bind MainRouteEntry::class }",
                    ),
                    SourceFile(
                        path = "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/app/AppCompositionAssembly.kt",
                        content = "val entries = summaryRouteEntries(koin)",
                    ),
                ),
            )

        assertEquals(
            listOf(
                "Route registration: feature/auth/src/commonMain/kotlin/com/po4yka/ratatoskr/di/AuthBindings.kt must export entries explicitly instead of binding them through Koin",
                "Route registration: feature/summary/src/commonMain/kotlin/com/po4yka/ratatoskr/di/SummaryBindings.kt must export entries explicitly instead of binding them through Koin",
            ),
            violations,
        )
    }
}
