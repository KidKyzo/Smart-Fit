package com.example.smartfit.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartfit.R
import com.example.smartfit.ui.designsystem.Alpha
import com.example.smartfit.ui.designsystem.AppButton
import com.example.smartfit.ui.designsystem.AppCard
import com.example.smartfit.ui.designsystem.AppScaffold
import com.example.smartfit.ui.designsystem.AppTypography
import com.example.smartfit.ui.designsystem.BorderWidth
import com.example.smartfit.ui.designsystem.ButtonVariant
import com.example.smartfit.ui.designsystem.SectionHeader
import com.example.smartfit.ui.designsystem.Shapes
import com.example.smartfit.ui.designsystem.Sizing
import com.example.smartfit.ui.designsystem.Spacing
import com.example.smartfit.utils.ValidationUtils
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    themeViewModel: ThemeViewModel,
    userViewModel: UserViewModel

) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    var userName by remember { mutableStateOf("Hanni Pham") }
    var userAge by remember { mutableStateOf("25") }
    var userWeight by remember { mutableStateOf("55") }
    var userHeight by remember { mutableStateOf("165") }
    var showEditDialog by remember { mutableStateOf(false) }

    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            item {
                // Profile Header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(Sizing.profileImageSizeLarge)
                            .clip(Shapes.circle)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                BorderStroke(BorderWidth.thick, MaterialTheme.colorScheme.primary),
                                Shapes.circle
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hanni_profile),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(Sizing.profileImageSize)
                                .clip(Shapes.circle)
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Text(
                        text = userName,
                        style = AppTypography.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Fitness Enthusiast",
                        style = AppTypography.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                    )
                }
            }

            item {
                // User Stats Card
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 1
                ) {
                    SectionHeader(title = "Personal Information")
                    Spacer(modifier = Modifier.height(Spacing.sm))

                    InfoRow("Age", "$userAge years")
                    InfoRow("Weight", "$userWeight kg")
                    InfoRow("Height", "$userHeight cm")
                    InfoRow("BMI", calculateBMI(userWeight.toFloatOrNull() ?: 0f, userHeight.toFloatOrNull() ?: 0f))
                }
            }

            item {
                // Theme Settings Card
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 1
                ) {
                    SectionHeader(title = "Settings")
                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Switch Theme",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Toggle dark / light mode",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                            )
                        }
                        Switch(
                            // If isDarkTheme (saved pref) is null, fall back to system setting
                            checked = isDarkTheme ?: isSystemInDarkTheme(),
                            onCheckedChange = { isChecked ->
                                themeViewModel.toggleTheme(isChecked)
                            }
                        )
                    }
                }
            }

            item {
                // Weekly Report Card - Refactored to match other cards
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 1
                ) {
                    SectionHeader(title = "Weekly Report")
                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Calories Card
                        ReportCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Calories",
                            value = "1200 kcal",
                            icon = Icons.Default.LocalFireDepartment,
                            iconTint = Color(0xFFFF5722) // Orange/Red
                        )

                        // Steps Card
                        ReportCard(
                            modifier = Modifier.weight(1f),
                            title = "Avg Steps",
                            value = "10,000 steps",
                            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                            iconTint = Color(0xFF4CAF50) // Green
                        )
                    }
                }
            }

            item {
                AppButton(
                    text = "Logout",
                    onClick = { userViewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Secondary
                )
            }
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = userName,
            currentAge = userAge,
            currentWeight = userWeight,
            currentHeight = userHeight,
            onDismiss = { showEditDialog = false },
            onSave = { name, age, weight, height ->
                userName = name
                userAge = age
                userWeight = weight
                userHeight = height
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ReportCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = AppTypography.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
        )
        Text(
            text = value,
            style = AppTypography.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentAge: String,
    currentWeight: String,
    currentHeight: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var age by remember { mutableStateOf(currentAge) }
    var weight by remember { mutableStateOf(currentWeight) }
    var height by remember { mutableStateOf(currentHeight) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                val nameValidation = ValidationUtils.validateName(name)
                val ageValidation = ValidationUtils.validateAge(age)
                val weightValidation = ValidationUtils.validateWeight(weight)
                val heightValidation = ValidationUtils.validateHeight(height)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    isError = !nameValidation.isValid,
                    supportingText = if (!nameValidation.isValid) {
                        { Text(nameValidation.errorMessage) }
                    } else null
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    singleLine = true,
                    isError = !ageValidation.isValid,
                    supportingText = if (!ageValidation.isValid) {
                        { Text(ageValidation.errorMessage) }
                    } else null
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    isError = !weightValidation.isValid,
                    supportingText = if (!weightValidation.isValid) {
                        { Text(weightValidation.errorMessage) }
                    } else null
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    singleLine = true,
                    isError = !heightValidation.isValid,
                    supportingText = if (!heightValidation.isValid) {
                        { Text(heightValidation.errorMessage) }
                    } else null
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, age, weight, height) },
                enabled = ValidationUtils.validateName(name).isValid &&
                        ValidationUtils.validateAge(age).isValid &&
                        ValidationUtils.validateWeight(weight).isValid &&
                        ValidationUtils.validateHeight(height).isValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun calculateBMI(weight: Float, height: Float): String {
    if (height == 0f) return "N/A"
    val heightInMeters = height / 100
    val bmi = weight / (heightInMeters * heightInMeters)
    return String.format("%.1f", bmi)
}
