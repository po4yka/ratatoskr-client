package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable

/**
 * Desktop has no reduce-motion source of truth and is a development
 * target — always return `false`. A `-Dratatoskr.reduceMotion=true`
 * system property opts in for manual UI checks.
 */
@Composable
actual fun rememberReduceMotion(): Boolean =
    System.getProperty("ratatoskr.reduceMotion")?.equals("true", ignoreCase = true) == true
