package com.po4yka.ratatoskr

import com.po4yka.ratatoskr.app.AppCompositionRoot
import com.po4yka.ratatoskr.app.assembleAppCompositionRoot
import org.koin.mp.KoinPlatform

private val sharedIosCompositionRoot by lazy { assembleAppCompositionRoot(KoinPlatform.getKoin()) }

internal fun iosCompositionRoot(): AppCompositionRoot = sharedIosCompositionRoot
