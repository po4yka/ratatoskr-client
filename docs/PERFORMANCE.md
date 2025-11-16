# Performance Optimization Guide

This document outlines performance optimizations implemented in the Bite-Size Reader mobile application and best practices for maintaining optimal performance.

## Performance Goals

- **App Launch Time**: < 2 seconds (cold start)
- **Frame Rate**: 60 FPS scrolling with 5000+ items
- **Memory Usage**: < 100MB for typical usage
- **App Size**: < 50MB (APK/IPA)
- **Network Efficiency**: Aggressive caching, minimal redundant requests

## Implemented Optimizations

### 1. Image Caching

**Configuration** (`ImageCacheConfig.kt`):
- Memory cache: 50MB (25% of available memory)
- Disk cache: 250MB with 30-day expiration
- Crossfade animations: 300ms for smooth transitions

**Android (Coil 3)**:
```kotlin
ImageLoader.Builder(context)
    .memoryCache { MemoryCache.Builder()
        .maxSizePercent(context, 0.25)
        .build()
    }
    .diskCache { DiskCache.Builder()
        .directory(context.cacheDir.resolve("image_cache"))
        .maxSizeBytes(250 * 1024 * 1024L)
        .build()
    }
    .build()
```

**Benefits**:
- Reduced network requests for repeated images
- Faster image loading from disk/memory
- Improved scrolling performance in lists

### 2. Database Query Optimization

**Indexed Columns**:
- `is_read` - For filtering read/unread summaries
- `created_at` - For sorting by date
- `topic_tags` - For topic filtering

**FTS5 Virtual Table**:
- Full-text search on summary content
- Optimized search queries with ranking

**Best Practices**:
```kotlin
// ❌ BAD: Loading all data
SELECT * FROM summaries

// ✅ GOOD: Pagination with limit/offset
SELECT * FROM summaries
ORDER BY created_at DESC
LIMIT 20 OFFSET ?

// ✅ GOOD: Using indexes
SELECT * FROM summaries
WHERE is_read = 0
ORDER BY created_at DESC
```

### 3. Network Efficiency

**Retry Mechanism** (`RetryPolicy.kt`):
- Exponential backoff: 1s, 2s, 4s, 8s
- Max 3 attempts for failed requests
- Smart retry logic (only for retryable errors)

**Caching Strategy** (Store):
- Memory cache for recent data
- Disk cache with SQLDelight
- Cache-first approach with background refresh

**Connection Monitoring**:
- Real-time network status monitoring
- Offline indicators for user awareness
- Queue requests when offline, sync when online

### 4. Memory Management

**Lifecycle-Aware Components**:
- ViewModels properly scoped to lifecycle
- Coroutines cancelled when not needed
- Flows collected only when active

**Avoiding Memory Leaks**:
```kotlin
// ✅ GOOD: Using viewModelScope
viewModelScope.launch {
    // Automatically cancelled when ViewModel cleared
}

// ✅ GOOD: Weak references in callbacks
private weak var delegate: SomeDelegate?

// ✅ GOOD: Cancelling flows in deinit/onCleared
override fun onCleared() {
    job.cancel()
    super.onCleared()
}
```

### 5. Lazy Loading & Pagination

**Summary List**:
- Load 20 items initially
- Load 20 more when scrolling near end
- Cancel loading if user scrolls away

**Image Loading**:
- Only load images for visible items
- Placeholder while loading
- Cancel requests for off-screen items

### 6. Animations

**Performance-Optimized Animations**:

**Android**:
```kotlin
// Standard duration: 300ms
AnimationConstants.STANDARD_DURATION

// Use spring animations for bouncy effects
spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)
```

**iOS**:
```swift
// Standard easing with 0.3s duration
AnimationHelpers.standardEasing

// Spring animation
AnimationHelpers.spring
```

**Best Practices**:
- Avoid animating layout properties frequently
- Use opacity/scale transforms (GPU-accelerated)
- Reduce animation complexity for long lists

## Performance Monitoring

### Android

**Profiling Tools**:
- Android Studio Profiler (CPU, Memory, Network)
- Layout Inspector for UI performance
- LeakCanary for memory leak detection

**Metrics to Track**:
```kotlin
// App startup time
adb shell am start -W com.po4yka.bitesizereader/.MainActivity

// Frame rendering
adb shell dumpsys gfxinfo com.po4yka.bitesizereader
```

### iOS

**Profiling Tools**:
- Instruments (Time Profiler, Allocations, Leaks)
- Xcode Memory Graph Debugger
- MetricKit for production metrics

**Metrics to Track**:
- Launch time in Xcode Organizer
- Memory usage in Instruments
- Network requests in Network profiler

## Accessibility Performance

**TalkBack/VoiceOver Optimizations**:
- Semantic descriptions for complex views
- Proper heading hierarchy
- Minimal unnecessary announcements

```kotlin
// Android: Group related elements
modifier.semantics(mergeDescendants = true) {
    contentDescription = "Summary card: $title"
}
```

```swift
// iOS: Combine accessibility elements
.accessibilityElement(children: .combine)
```

## Code Size Optimization

### Android

**ProGuard/R8**:
```proguard
# Enable code shrinking
-dontobfuscate
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# Remove unused resources
android {
    buildTypes {
        release {
            shrinkResources true
            minifyEnabled true
        }
    }
}
```

**App Bundle**:
- Use Android App Bundle for dynamic delivery
- Split APKs by density, ABI, language

### iOS

**Bitcode & App Thinning**:
- Enable App Thinning in Xcode
- Use asset catalogs for images
- Remove unused assets

**SwiftUI Optimization**:
- Avoid massive view hierarchies
- Extract subviews for better diffing
- Use `@StateObject` and `@ObservedObject` correctly

## Best Practices

### 1. Avoid Premature Optimization

- Profile first, optimize second
- Focus on bottlenecks, not micro-optimizations
- Measure impact of changes

### 2. Batch Operations

```kotlin
// ❌ BAD: Multiple database inserts
summaries.forEach { database.insert(it) }

// ✅ GOOD: Batch insert
database.transaction {
    summaries.forEach { insert(it) }
}
```

### 3. Use Background Threads

```kotlin
// ❌ BAD: Heavy work on main thread
val processedData = heavyProcessing(data)

// ✅ GOOD: Use appropriate dispatcher
withContext(Dispatchers.Default) {
    val processedData = heavyProcessing(data)
}
```

### 4. Cache Expensive Computations

```kotlin
// ✅ GOOD: Memoize expensive calculations
private val expensiveResult by lazy {
    performExpensiveCalculation()
}
```

### 5. Optimize Compose/SwiftUI

**Android Compose**:
```kotlin
// ✅ Use remember for expensive calculations
val processedList = remember(rawList) {
    rawList.map { process(it) }
}

// ✅ Use derivedStateOf for computed values
val isScrolled by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 0 }
}
```

**iOS SwiftUI**:
```swift
// ✅ Use computed properties wisely
var filteredItems: [Item] {
    items.filter { $0.isVisible }
}

// ✅ Avoid creating new instances in body
struct ContentView: View {
    @StateObject private var viewModel = ViewModel()
}
```

## Monitoring in Production

### Key Metrics

1. **Crash-Free Rate**: > 99.5%
2. **ANR Rate** (Android): < 0.1%
3. **App Hang Rate** (iOS): < 0.1%
4. **Network Success Rate**: > 95%
5. **Average Response Time**: < 500ms (p95)

### Tools

- **Android**: Firebase Crashlytics, Firebase Performance Monitoring
- **iOS**: Firebase Crashlytics, MetricKit
- **Backend**: Monitor API response times, error rates

## Common Performance Issues & Solutions

### Issue: Slow List Scrolling

**Cause**: Too many views, heavy layouts, synchronous operations

**Solution**:
- Use pagination (load 20 items at a time)
- Optimize item layout (fewer nested views)
- Use `LazyColumn` (Android) / `List` (iOS) properly
- Avoid expensive operations in item builders

### Issue: High Memory Usage

**Cause**: Memory leaks, large images, cached data

**Solution**:
- Fix memory leaks (use profiler)
- Resize images before loading
- Implement cache eviction policies
- Use weak references where appropriate

### Issue: Slow App Launch

**Cause**: Heavy initialization, blocking operations

**Solution**:
- Defer non-critical initialization
- Use lazy loading for dependencies
- Avoid synchronous I/O on startup
- Optimize Koin module initialization

### Issue: Network Slowness

**Cause**: Large payloads, no caching, redundant requests

**Solution**:
- Implement pagination
- Use HTTP caching headers
- Compress API responses (gzip)
- Batch requests where possible

## Performance Checklist

Before release:

- [ ] Profile app on low-end devices
- [ ] Test with 1000+ summaries
- [ ] Verify memory usage < 100MB
- [ ] Check app size < 50MB
- [ ] Test on slow/offline networks
- [ ] Verify 60 FPS scrolling
- [ ] Run memory leak detector
- [ ] Profile app launch time
- [ ] Test with TalkBack/VoiceOver enabled
- [ ] Verify all animations run smoothly

## Resources

- [Android Performance](https://developer.android.com/topic/performance)
- [iOS Performance Best Practices](https://developer.apple.com/documentation/xcode/improving-your-app-s-performance)
- [Kotlin Coroutines Best Practices](https://kotlinlang.org/docs/coroutines-best-practices.html)
- [Compose Performance](https://developer.android.com/jetpack/compose/performance)
- [SwiftUI Performance](https://developer.apple.com/videos/play/wwdc2020/10149/)

---

**Last Updated**: 2025-11-16
**Review Frequency**: Quarterly
