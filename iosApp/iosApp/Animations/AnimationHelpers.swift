import SwiftUI

/// Animation constants and helpers for iOS
struct AnimationHelpers {

    /// Standard animation duration
    static let standardDuration: Double = 0.3

    /// Fast animation duration
    static let fastDuration: Double = 0.15

    /// Slow animation duration
    static let slowDuration: Double = 0.5

    /// Standard easing animation
    static let standardEasing: Animation = .easeInOut(duration: standardDuration)

    /// Fast easing animation
    static let fastEasing: Animation = .easeInOut(duration: fastDuration)

    /// Spring animation
    static let spring: Animation = .spring(response: 0.5, dampingFraction: 0.7, blendDuration: 0)

    /// Bouncy spring animation
    static let bouncySpring: Animation = .spring(response: 0.6, dampingFraction: 0.6, blendDuration: 0)
}

/// SwiftUI View extension for animations
extension View {
    /// Animate appearance with fade
    func animateAppearance(delay: Double = 0) -> some View {
        self
            .transition(.opacity)
            .animation(AnimationHelpers.standardEasing.delay(delay), value: UUID())
    }

    /// Animate with spring effect
    func animateWithSpring() -> some View {
        self.animation(AnimationHelpers.spring, value: UUID())
    }

    /// Slide in from bottom transition
    func slideInFromBottom() -> some View {
        self.transition(.move(edge: .bottom).combined(with: .opacity))
    }

    /// Slide in from trailing edge
    func slideInFromTrailing() -> some View {
        self.transition(.move(edge: .trailing).combined(with: .opacity))
    }

    /// Scale and fade transition
    func scaleAndFade() -> some View {
        self.transition(.scale.combined(with: .opacity))
    }
}

/// List row animation helper
extension View {
    /// Animate list row appearance with staggered delay
    func animateListRow(index: Int) -> some View {
        let delay = Double(index) * 0.05 // 50ms delay between items
        return self
            .opacity(0)
            .offset(y: 20)
            .onAppear {
                withAnimation(AnimationHelpers.standardEasing.delay(delay)) {
                    // Animation will be handled by the view's state
                }
            }
    }
}
