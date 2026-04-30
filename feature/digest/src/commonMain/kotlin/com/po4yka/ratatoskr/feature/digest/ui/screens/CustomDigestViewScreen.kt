package com.po4yka.ratatoskr.feature.digest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.custom_digest_view_default_title
import ratatoskr.core.ui.generated.resources.custom_digest_view_delete
import ratatoskr.core.ui.generated.resources.custom_digest_view_error
import ratatoskr.core.ui.generated.resources.custom_digest_view_failed
import ratatoskr.core.ui.generated.resources.custom_digest_view_generating
import ratatoskr.core.ui.generated.resources.custom_digest_view_not_found
import com.po4yka.ratatoskr.core.ui.components.AppSmallSpinner
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.CustomDigestStatus
import com.po4yka.ratatoskr.presentation.navigation.CustomDigestViewComponent
import com.po4yka.ratatoskr.core.ui.components.AppIconButton
import com.po4yka.ratatoskr.core.ui.components.ScreenHeader
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming", "LongMethod", "CyclomaticComplexMethod")
@Composable
fun CustomDigestViewScreen(
    component: CustomDigestViewComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
    ) {
        ScreenHeader(
            title = state.digest?.title ?: stringResource(Res.string.custom_digest_view_default_title),
            isDetailScreen = true,
            onBackClick = component::onBackClicked,
            actions = {
                if (state.digest != null) {
                    AppIconButton(
                        imageVector = AppIcons.TrashCan,
                        contentDescription = stringResource(Res.string.custom_digest_view_delete),
                        onClick = {
                            viewModel.deleteDigest(component.digestId) { component.onBackClicked() }
                        },
                        tint = AppTheme.colors.supportError,
                        iconSize = IconSizes.md,
                    )
                }
            },
        )

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    AppSmallSpinner()
                }
            }

            state.digest?.status == CustomDigestStatus.GENERATING ||
                state.digest?.status == CustomDigestStatus.PENDING -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    // Two-color ink progress bar — no M3 LinearProgressIndicator
                    Box(
                        modifier =
                            Modifier.fillMaxWidth().height(
                                2.dp,
                            ).background(AppTheme.frostColors.ink.copy(alpha = AppTheme.border.separatorAlpha)),
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    FrostText(
                        text = stringResource(Res.string.custom_digest_view_generating),
                        style = AppTheme.type.body01,
                        color = AppTheme.colors.textSecondary,
                    )
                }
            }

            state.digest?.status == CustomDigestStatus.COMPLETED &&
                state.digest?.content != null -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                ) {
                    item {
                        FrostText(
                            text = state.digest!!.content!!,
                            style = AppTheme.type.body01,
                            color = AppTheme.colors.textPrimary,
                            modifier = Modifier.padding(vertical = Spacing.md),
                        )
                    }
                }
            }

            state.digest?.status == CustomDigestStatus.FAILED -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = AppIcons.WarningAlt,
                        contentDescription = stringResource(Res.string.custom_digest_view_error),
                        tint = AppTheme.colors.supportError,
                        modifier = Modifier.size(IconSizes.xl),
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    FrostText(
                        text = stringResource(Res.string.custom_digest_view_failed),
                        style = AppTheme.type.heading03,
                        color = AppTheme.colors.supportError,
                    )
                    state.error?.let { errorText ->
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        FrostText(
                            text = errorText,
                            style = AppTheme.type.bodyCompact01,
                            color = AppTheme.colors.textSecondary,
                        )
                    }
                }
            }

            state.digest == null && !state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    FrostText(
                        text = stringResource(Res.string.custom_digest_view_not_found),
                        style = AppTheme.type.bodyCompact01,
                        color = AppTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}
