package com.example.smartfit.screens.plan.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartfit.R
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.ui.designsystem.Shapes
import com.example.smartfit.ui.theme.*

@Composable
fun CalorieIntakeSection(
    onNavigateToList: () -> Unit
) {
    // Food/Nutrition Gradient (White to Vibrant Orange - 6 colors)
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.9f),
            FoodGradient5,
            FoodGradient4,
            FoodGradient3,
            FoodGradient2,
            FoodGradient1
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .shadow(elevation = 8.dp, shape = Shapes.lg)
                .clip(Shapes.lg)
                .background(gradientBrush)
                .clickable { onNavigateToList() }
                .padding(Spacing.lg)
        ) {
            // Title
             Text(
                 text = "Food & Nutrition List",
                 style = AppTypography.typography.headlineSmall.copy(
                     fontWeight = FontWeight.Bold,
                     color = MaterialTheme.colorScheme.onSurface
                 ),
                 modifier = Modifier.align(Alignment.BottomStart)
             )
        }
    }
}
