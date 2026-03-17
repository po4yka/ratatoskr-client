package com.po4yka.bitesizereader.ui.screens

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.progressbar.IndeterminateProgressBar
import com.po4yka.bitesizereader.domain.model.CustomDigestStatus
import com.po4yka.bitesizereader.presentation.navigation.CustomDigestViewComponent
import com.po4yka.bitesizereader.ui.components.ScreenHeader
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing

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
                .background(Carbon.theme.background),
    ) {
        ScreenHeader(
            title = state.digest?.title ?: "Digest",
            isDetailScreen = true,
            onBackClick = component::onBackClicked,
            actions = {
                if (state.digest != null) {
                    IconButton(
                        onClick = {
                            viewModel.deleteDigest(component.digestId) { component.onBackClicked() }
                        },
                    ) {
                        Icon(
                            imageVector = CarbonIcons.TrashCan,
                            contentDescription = "Delete digest",
                            tint = Carbon.theme.supportError,
                            modifier = Modifier.size(IconSizes.md),
                        )
                    }
                }
            },
        )

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    SmallLoading()
                }
            }

            state.digest?.status == CustomDigestStatus.GENERATING ||
                state.digest?.status == CustomDigestStatus.PENDING -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    IndeterminateProgressBar(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = "Generating your digest...",
                        style = Carbon.typography.body01,
                        color = Carbon.theme.textSecondary,
                    )
                }
            }

            state.digest?.status == CustomDigestStatus.COMPLETED &&
                state.digest?.content != null -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                ) {
                    item {
                        Text(
                            text = state.digest!!.content!!,
                            style = Carbon.typography.body01,
                            color = Carbon.theme.textPrimary,
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
                        imageVector = CarbonIcons.WarningAlt,
                        contentDescription = "Error",
                        tint = Carbon.theme.supportError,
                        modifier = Modifier.size(IconSizes.xl),
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = "Failed to generate digest",
                        style = Carbon.typography.heading03,
                        color = Carbon.theme.supportError,
                    )
                    state.error?.let { errorText ->
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = errorText,
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textSecondary,
                        )
                    }
                }
            }

            state.digest == null && !state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Digest not found",
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textSecondary,
                    )
                }
            }
        }
    }
}
