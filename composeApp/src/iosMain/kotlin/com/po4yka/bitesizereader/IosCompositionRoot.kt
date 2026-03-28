package com.po4yka.bitesizereader

import com.po4yka.bitesizereader.app.AppCompositionRoot
import com.po4yka.bitesizereader.app.assembleAppCompositionRoot
import org.koin.mp.KoinPlatform

private val sharedIosCompositionRoot by lazy { assembleAppCompositionRoot(KoinPlatform.getKoin()) }

internal fun iosCompositionRoot(): AppCompositionRoot = sharedIosCompositionRoot
