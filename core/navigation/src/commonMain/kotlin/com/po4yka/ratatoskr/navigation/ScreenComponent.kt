package com.po4yka.ratatoskr.navigation

/**
 * Marker interface every Decompose component composed into a routed screen
 * descriptor must implement. Using this in [MainChildDescriptor.component] and
 * [RootChildDescriptor.component] (instead of `Any`) preserves Decompose's
 * typed-composition guarantee at the navigation seam without forcing the
 * navigation layer to depend on every concrete feature component.
 */
interface ScreenComponent
