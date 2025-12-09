package com.example.smartfit.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartfit.R // Ensure R is imported for drawable resources
import com.example.smartfit.Routes
import com.example.smartfit.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(navController: NavController, userViewModel: UserViewModel) {
    var isVisible by remember { mutableStateOf(true) }
    val exitDuration = 500.milliseconds

    // This side effect runs once when the composable is first displayed
    LaunchedEffect(Unit) {
        // Total time the splash screen is visible before starting to exit
        delay(2500)
        // Trigger the exit animation
        isVisible = false
    }

    HeartBeatAnimation(
        isVisible = isVisible,
        exitAnimationDuration = exitDuration,
        onStartExitAnimation = {
            // This lambda is called when the exit animation should begin.
            // We determine the destination based on the current login state.
            val destination = if (userViewModel.isLoggedIn.value) Routes.home else Routes.login
            // Navigate to the correct screen and remove the splash screen from history
            navController.navigate(destination) {
                popUpTo(Routes.splash) { inclusive = true }
            }
        }
    )
}

@Composable
fun HeartBeatAnimation(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    exitAnimationDuration: Duration = Duration.ZERO,
    onStartExitAnimation: () -> Unit = {}
) {
    val rippleCount = 4
    val rippleDurationMs = 3313
    val rippleDelayMs = rippleDurationMs / 8
    val baseSize = 144.dp
    val containerSize = 288.dp

    var isExitAnimationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        // When the isVisible state becomes false, start the exit animation logic
        if (!isVisible && !isExitAnimationStarted) {
            isExitAnimationStarted = true
            delay(exitAnimationDuration) // Wait for the exit animation to visually "start"
            onStartExitAnimation() // Trigger the navigation callback
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val screenDiagonal = sqrt((screenWidth * screenWidth + screenHeight * screenHeight).toFloat())

    val snappyEasing = CubicBezierEasing(0.2f, 0.0f, 0.2f, 1.0f)
    val exitAnimationScale by animateFloatAsState(
        targetValue = if (isExitAnimationStarted) screenDiagonal / baseSize.value else 0f,
        animationSpec = tween(
            durationMillis = exitAnimationDuration.inWholeMilliseconds.toInt(),
            easing = snappyEasing
        ),
        label = "exitScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "heartbeatTransition")

    // The root Box now uses the theme's background color
    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Show the ripple animation and icon only if the exit animation hasn't started
        if (isVisible && !isExitAnimationStarted) {
            Box(
                modifier = Modifier.size(containerSize),
                contentAlignment = Alignment.Center
            ) {
                // The ripple circles are in the background
                repeat(rippleCount) { index ->
                    RippleCircle(
                        infiniteTransition = infiniteTransition,
                        index = index,
                        rippleDurationMs = rippleDurationMs,
                        rippleDelayMs = rippleDelayMs,
                        baseSize = baseSize
                    )
                }

                // The App Icon is placed in the center, on top of the ripples
                Image(
                    painter = painterResource(id = R.drawable.icon_app_transparent),
                    contentDescription = "App Icon",
                    modifier = Modifier.size(size = 400.dp)
                )
            }
        }

        // Show the expanding circle for the exit animation
        if (isExitAnimationStarted) {
            Box(
                modifier = Modifier
                    .size(baseSize)
                    .graphicsLayer {
                        scaleX = exitAnimationScale
                        scaleY = exitAnimationScale
                    }
                    .background(
                        // The exit animation uses the theme's primary color
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun RippleCircle(
    infiniteTransition: InfiniteTransition,
    index: Int,
    rippleDurationMs: Int,
    rippleDelayMs: Int,
    baseSize: Dp
) {
    val totalDuration = rippleDurationMs + (rippleDelayMs * index)
    val easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = totalDuration,
                delayMillis = rippleDelayMs * index,
                easing = easing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale$index"
    )

    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = totalDuration,
                delayMillis = rippleDelayMs * index,
                easing = easing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleAlpha$index"
    )

    Box(
        modifier = Modifier
            .size(baseSize)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                alpha = animatedAlpha
            }
            .background(
                // The ripples use the theme's primary color
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    )
}
