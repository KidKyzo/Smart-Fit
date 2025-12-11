package com.example.smartfit.ui.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Reusable UI components following the design system
 */

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    elevation: Int = 0,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    val elevationValue = when (elevation) {
        0 -> Elevation.none
        1 -> Elevation.small
        2 -> Elevation.medium
        3 -> Elevation.large
        else -> Elevation.xLarge
    }
    
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevationValue),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            content = content
        )
    }
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    icon: ImageVector? = null
) {
    val buttonColors = when (variant) {
        ButtonVariant.Primary -> ButtonDefaults.buttonColors()
        ButtonVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
        ButtonVariant.Outline -> ButtonDefaults.outlinedButtonColors()
        ButtonVariant.Text -> ButtonDefaults.textButtonColors()
    }
    
    Button(
        onClick = onClick,
        modifier = modifier.height(Sizing.buttonHeight),
        enabled = enabled,
        colors = buttonColors,
        shape = Shapes.medium
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(Sizing.iconMedium)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
        }
        Text(
            text = text,
            style = AppTypography.typography.labelLarge
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        elevation = 1
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = AppTypography.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = value,
                    style = AppTypography.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(Sizing.iconLarge)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier
            .size(Sizing.quickActionCardSize)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.medium),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(Sizing.iconLarge)
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = title,
                style = AppTypography.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTypography.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        actionText?.let { text ->
            if (onActionClick != null) {
                TextButton(onClick = onActionClick) {
                    Text(
                        text = text,
                        style = AppTypography.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = text,
                    style = AppTypography.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Sizing.iconXXLarge),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
        )
        Spacer(modifier = Modifier.height(Spacing.lg))
        Text(
            text = title,
            style = AppTypography.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = description,
            style = AppTypography.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium),
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )
        
        actionText?.let { text ->
            if (onActionClick != null) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                AppButton(
                    text = text,
                    onClick = onActionClick
                )
            }
        }
    }
}

enum class ButtonVariant {
    Primary,
    Secondary,
    Outline,
    Text
}