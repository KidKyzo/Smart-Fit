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
import com.example.smartfit.viewmodel.FoodViewModel
import com.example.smartfit.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    foodViewModel: FoodViewModel? = null
) {
    var isVisible by remember { mutableStateOf(true) }
    var isApiLoaded by remember { mutableStateOf(false) }
    var isMinTimeElapsed by remember { mutableStateOf(false) }
    val exitDuration = 500.milliseconds
    
    // Track API loading status
    val isSearching by foodViewModel?.isSearching?.collectAsState() ?: remember { mutableStateOf(false) }
    
    // Mark API as loaded when search completes
    LaunchedEffect(isSearching) {
        if (!isSearching) {
            isApiLoaded = true
        }
    }
    
    // Minimum animation time (heartbeat plays during this)
    LaunchedEffect(Unit) {
        delay(3500) // Minimum time for smooth animation (extended by 1s)
        isMinTimeElapsed = true
    }
    
    // Exit only when BOTH conditions are met: API loaded AND minimum time elapsed
    LaunchedEffect(isApiLoaded, isMinTimeElapsed) {
        if (isApiLoaded && isMinTimeElapsed) {
            // Give a small buffer for smooth animation completion
            delay(300)
            isVisible = false
        }
    }

    HeartBeatAnimation(
        isVisible = isVisible,
        exitAnimationDuration = exitDuration,
        onStartExitAnimation = {
            // Check if user is logged in AND has valid user data
            val isLoggedIn = userViewModel.isLoggedIn.value
            val hasUserProfile = userViewModel.userProfile.value != null
            
            // Route to home only if logged in AND has profile data
            // Otherwise, route to login screen
            val destination = if (isLoggedIn && hasUserProfile) Routes.home else Routes.login
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
    // Add extra padding (1.5x) to ensure the circle covers rounded corners on modern devices
    val screenDiagonal = sqrt((screenWidth * screenWidth + screenHeight * screenHeight).toFloat()) * 1.5f

    val snappyEasing = CubicBezierEasing(0.2f, 0.0f, 0.2f, 1.0f)
    val exitAnimationScale by animateFloatAsState(
        targetValue = if (isExitAnimationStarted) screenDiagonal / baseSize.value else 0f,
        animationSpec = tween(
            durationMillis = exitAnimationDuration.inWholeMilliseconds.toInt(),
            easing = snappyEasing
        ),
        label = "exitScale"
    )
    
    // Fade out animation for the heartbeat content
    val contentAlpha by animateFloatAsState(
        targetValue = if (isExitAnimationStarted) 0f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "contentFade"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "heartbeatTransition")

    // The root Box now uses the theme's background color
    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Show the ripple animation and icon with fade-out
        if (isVisible && contentAlpha > 0f) {
            Box(
                modifier = Modifier
                    .size(containerSize)
                    .graphicsLayer { alpha = contentAlpha },
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
