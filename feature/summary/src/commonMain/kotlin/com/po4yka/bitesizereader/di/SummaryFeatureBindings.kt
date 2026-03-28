package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.presentation.viewmodel.RecommendationsViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SearchViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import org.koin.dsl.module

val summaryFeatureBindingsModule =
    module {
        factory {
            RecommendationsViewModel(
                getRecommendationsUseCase = get(),
                refreshRecommendationsUseCase = get(),
                dismissRecommendationUseCase = get(),
            )
        }
        factory {
            SearchViewModel(
                searchSummariesUseCase = get(),
                semanticSearchUseCase = get(),
                searchHistoryManager = get(),
                getSearchInsightsUseCase = get(),
            )
        }
        factory {
            SubmitURLViewModel(
                processingService = get(),
                getRequestsUseCase = get(),
                retryRequestUseCase = get(),
                checkDuplicateUrlUseCase = get(),
            )
        }
        factory {
            SummaryDetailViewModel(
                readingSessionDelegate = get(),
                audioDelegate = get(),
                highlightDelegate = get(),
                feedbackDelegate = get(),
                collectionDelegate = get(),
                getSummaryByIdUseCase = get(),
                getSummaryContentUseCase = get(),
                refreshFullContentUseCase = get(),
                deleteSummaryUseCase = get(),
                toggleFavoriteUseCase = get(),
                readingPreferencesRepository = get(),
                networkMonitor = get(),
                exportSummaryUseCase = get(),
                shareManager = get(),
            )
        }
        factory {
            SummaryListViewModel(
                getFilteredSummariesUseCase = get(),
                searchSummariesUseCase = get(),
                markSummaryAsReadUseCase = get(),
                deleteSummaryUseCase = get(),
                archiveSummaryUseCase = get(),
                getAvailableTagsUseCase = get(),
                searchHistoryManager = get(),
                syncDataUseCase = get(),
                toggleFavoriteUseCase = get(),
                logoutUseCase = get(),
                networkMonitor = get(),
            )
        }
    }
