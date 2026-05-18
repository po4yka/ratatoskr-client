/* 
 * NOTE: This file is auto generated. Do not edit the file manually!
 * 
 * Ratatoskr Mobile API
 * RESTful API for Android/iOS mobile clients
 * Version 1.0.0
 * 
 * Generated Mon, 18 May 2026 10:35:05 GMT
 * OpenAPI KMP Gen (version 1.3.0) by kroegerama
 */
@file:Suppress("ArrayInDataClass", "RedundantVisibilityModifier", "unused", "ConstPropertyName")

package com.po4yka.ratatoskr.api.generated

import com.kroegerama.openapi.kmp.gen.`companion`.ApiHolder
import io.ktor.http.Url
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.listOf

public object Api : ApiHolder() {
  public const val title: String = "Ratatoskr Mobile API"

  public const val description: String = "RESTful API for Android/iOS mobile clients"

  public const val version: String = "1.0.0"


  public val servers: List<Url> = listOf(
    Url("https://ratatoskrapi.po4yka.com/"),
    Url("https://staging-ratatoskrapi.po4yka.com/"),
    Url("http://localhost:8000/"),
  )

  override var baseUrl: Url = servers.first()

  public fun setAuthProvider(auth: Auth) {
    setAuthProvider(auth.key, auth::provideAuthItem)
  }

  public fun clearAuthProvider(auth: Auth) {
    clearAuthProvider(auth.key)
  }
}
