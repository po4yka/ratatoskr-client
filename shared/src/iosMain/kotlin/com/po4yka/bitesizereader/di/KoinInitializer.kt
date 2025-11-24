package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.util.config.AppConfig
import com.po4yka.bitesizereader.di.appModules
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initialize Koin for iOS
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(listOf(iosModule) + appModules())
        properties(
            mapOf(
                "api.base.url" to AppConfig.Api.baseUrl,
                "api.logging.enabled" to AppConfig.Api.loggingEnabled.toString(),
                "telegram.bot.username" to AppConfig.Telegram.botUsername,
                "telegram.bot.id" to AppConfig.Telegram.botId,
            ),
        )
    }
