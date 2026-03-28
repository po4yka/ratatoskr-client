package com.po4yka.bitesizereader

import com.po4yka.bitesizereader.app.AppCompositionRoot
import org.koin.mp.KoinPlatform

private val sharedIosCompositionRoot by lazy { AppCompositionRoot(KoinPlatform.getKoin()) }

internal fun iosCompositionRoot(): AppCompositionRoot = sharedIosCompositionRoot
