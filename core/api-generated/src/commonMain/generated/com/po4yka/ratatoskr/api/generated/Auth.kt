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

import com.kroegerama.openapi.kmp.gen.`companion`.AuthItem
import kotlin.String
import kotlin.Suppress

public sealed interface Auth {
  public val key: String

  public suspend fun provideAuthItem(): AuthItem?

  public data class HTTPBearer(
    public val getBearer: suspend () -> AuthItem.Bearer?,
  ) : Auth {
    override val key: String = ID

    override suspend fun provideAuthItem(): AuthItem? = getBearer()

    public companion object {
      public const val ID: String = "HTTPBearer"
    }
  }
}
