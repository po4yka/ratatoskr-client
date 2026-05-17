# Ratatoskr Client Android ProGuard Rules

# Keep source file names and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep models for kotlinx.serialization
-keep class com.po4yka.ratatoskr.**.dto.** { *; }
-keep class com.po4yka.ratatoskr.**.model.** { *; }

# Keep Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-dontwarn io.ktor.**

# Keep SQLDelight
-keep class app.cash.sqldelight.** { *; }
-keep class com.po4yka.ratatoskr.database.** { *; }

# Keep Koin
-keep class org.koin.** { *; }
-keep class kotlin.Metadata { *; }

# Keep Decompose
-keep class com.arkivanov.decompose.** { *; }

# Keep kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.po4yka.ratatoskr.**$$serializer { *; }
-keepclassmembers class com.po4yka.ratatoskr.** {
    *** Companion;
}
-keepclasseswithmembers class com.po4yka.ratatoskr.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Strip debug/trace logging in release builds. R8 removes call sites
# annotated as -assumenosideeffects entirely, so neither the call nor
# the lambda body executes — saving both APK size and runtime allocation.
# `error`, `warn`, `info` calls remain so Crashlytics / Logcat still
# receive the intended signals.
-assumenosideeffects class io.github.oshai.kotlinlogging.KLogger {
    public *** debug(...);
    public *** trace(...);
}
-assumenosideeffects class io.github.oshai.kotlinlogging.Logger {
    public *** debug(...);
    public *** trace(...);
}

# kotlin-logging's KLoggerKt extension functions used by some call sites.
-assumenosideeffects class io.github.oshai.kotlinlogging.KLoggerKt {
    public static *** debug(...);
    public static *** trace(...);
}

# Strip android.util.Log debug/verbose entries too — any direct Log.d
# / Log.v calls in androidMain code paths.
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# Obfuscate everything else
-repackageclasses 'o'
