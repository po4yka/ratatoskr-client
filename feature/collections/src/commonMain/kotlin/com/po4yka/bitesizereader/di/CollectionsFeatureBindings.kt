package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.presentation.viewmodel.CollectionViewViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.CollectionsViewModel
import org.koin.dsl.module

val collectionsFeatureBindingsModule =
    module {
        factory {
            CollectionsViewModel(
                collectionRepository = get(),
                createCollectionUseCase = get(),
            )
        }
        factory {
            CollectionViewViewModel(
                getCollectionUseCase = get(),
                getCollectionItemsUseCase = get(),
                updateCollectionUseCase = get(),
                deleteCollectionUseCase = get(),
                getCollectionAclUseCase = get(),
                manageCollaboratorUseCase = get(),
                createInviteLinkUseCase = get(),
            )
        }
    }
