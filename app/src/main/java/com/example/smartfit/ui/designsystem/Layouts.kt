package com.example.smartfit.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Pre-designed layouts following the design system
 */

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.large),
        shape = Shapes.xLarge
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(colors = colors),
                    shape = Shapes.xLarge
                )
                .clip(Shapes.xLarge)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                content = content
            )
        }
    }
}

@Composable
fun StatsRow(
    modifier: Modifier = Modifier,
    stats: List<StatItem>
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        stats.forEach { stat ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = stat.label,
                    tint = stat.color,
                    modifier = Modifier.size(Sizing.iconLarge)
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = stat.value,
                    style = AppTypography.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = stat.textColor ?: MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stat.label,
                    style = AppTypography.typography.bodySmall,
                    color = stat.textColor?.copy(alpha = Alpha.medium) 
                        ?: MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                )
            }
        }
    }
}

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String = "Loading..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(Sizing.iconXLarge),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = message,
                style = AppTypography.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
            )
        }
    }
}

@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Spacing.lg)
        ) {
            Text(
                text = "Oops!",
                style = AppTypography.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = message,
                style = AppTypography.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium),
                modifier = Modifier.padding(horizontal = Spacing.lg)
            )
            
            onRetry?.let { retry ->
                Spacer(modifier = Modifier.height(Spacing.lg))
                AppButton(
                    text = "Retry",
                    onClick = retry
                )
            }
        }
    }
}

@Composable
fun ResponsiveLayout(
    modifier: Modifier = Modifier,
    phoneContent: @Composable () -> Unit,
    tabletContent: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= Breakpoints.tablet.value.toInt()
    
    Box(modifier = modifier) {
        if (isTablet) {
            tabletContent()
        } else {
            phoneContent()
        }
    }
}

data class StatItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val value: String,
    val color: Color,
    val textColor: Color? = null
)