package com.example.smartfit.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.navigation.NavController
import com.example.smartfit.R
import com.example.smartfit.screens.profile.components.BMIIndicator
import com.example.smartfit.ui.designsystem.Alpha
import com.example.smartfit.ui.designsystem.AppCard
import com.example.smartfit.ui.designsystem.AppScaffold
import com.example.smartfit.ui.designsystem.AppTypography
import com.example.smartfit.ui.designsystem.BorderWidth
import com.example.smartfit.ui.designsystem.CompactTopAppBar
import com.example.smartfit.ui.designsystem.SectionHeader
import com.example.smartfit.ui.designsystem.Shapes
import com.example.smartfit.ui.designsystem.Sizing
import com.example.smartfit.ui.designsystem.Spacing
import com.example.smartfit.utils.ValidationUtils
import com.example.smartfit.viewmodel.ActivityViewModel
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    userViewModel: UserViewModel,
    activityViewModel: ActivityViewModel
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    // Load persisted user data
    val userName by userViewModel.name.collectAsState()
    val userAge by userViewModel.age.collectAsState()
    val userWeight by userViewModel.weight.collectAsState()
    val userHeight by userViewModel.height.collectAsState()
    val userGender by userViewModel.gender.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Calculate BMI from persisted data
    val bmi = remember(userWeight, userHeight) {
        val weight = userWeight.toFloatOrNull() ?: 70f
        val height = userHeight.toFloatOrNull() ?: 170f
        if (height > 0) {
            val heightInMeters = height / 100
            weight / (heightInMeters * heightInMeters)
        } else 0f
    }

    // Weekly report data
    val currentWeekDuration = activityViewModel.getWeeklyWorkoutDuration(0)
    val lastWeekDuration = activityViewModel.getWeeklyWorkoutDuration(-1)
    val durationDiff = currentWeekDuration - lastWeekDuration

    val currentWeekSteps = activityViewModel.getWeeklyAverageSteps(0)
    val lastWeekSteps = activityViewModel.getWeeklyAverageSteps(-1)
    val stepsDiff = currentWeekSteps - lastWeekSteps

    AppScaffold(
        topBar = {
            CompactTopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
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

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    Text(
                        text = userName,
                        style = AppTypography.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(Spacing.sm))
                }
            }

            item {
                // BMI Indicator
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 1
                ) {
                    BMIIndicator(bmi = bmi)
                }
            }

            item {
                // User Stats Card
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 1
                ) {
                    SectionHeader(title = "Personal Information")
                    Spacer(modifier = Modifier.height(Spacing.xs))

                    InfoRow("Age", "$userAge years")
                    InfoRow("Weight", "$userWeight kg")
                    InfoRow("Height", "$userHeight cm")
                    InfoRow("Gender", userGender)
                }
            }
            item {
                // Weekly Report Card - Enhanced with comparisons
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("weekly_report/0") },
                    elevation = 1
                ) {
                    SectionHeader(title = "Weekly Report")
                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        // Workout Duration Card
                        ReportCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Workout",
                            value = "$currentWeekDuration min",
                            comparisonValue = if (durationDiff >= 0)
                                "+$durationDiff min" else "$durationDiff min",
                            comparisonColor = if (durationDiff >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                            icon = Icons.Default.FitnessCenter,
                            iconTint = Color(0xFF2196F3), // Blue
                            onClick = { navController.navigate("weekly_report/0") }
                        )

                        // Average Steps Card
                        ReportCard(
                            modifier = Modifier.weight(1f),
                            title = "Avg Daily Steps",
                            value = "$currentWeekSteps",
                            comparisonValue = if (stepsDiff >= 0) "+$stepsDiff" else "$stepsDiff",
                            comparisonColor = if (stepsDiff >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                            iconTint = Color(0xFF4CAF50), // Green
                            onClick = { navController.navigate("weekly_report/0") }
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = userName,
            currentAge = userAge,
            currentWeight = userWeight,
            currentHeight = userHeight,
            currentGender = userGender, // Passed the current gender here
            onDismiss = { showEditDialog = false },
            onSave = { name, age, weight, height, gender ->
                // Save user data to database (replaces old data, no duplication)
                userViewModel.saveProfile(name, age, weight, height, gender)
                showEditDialog = false
            }
        )
    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        userViewModel.logout()
                    }
                ) {
                    Text("Yes")

                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("No")

                }
            }
        )
    }

    if (showSettingsSheet) {
        AlertDialog(
            onDismissRequest = { showSettingsSheet = false },
            title = { Text("Settings") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Theme Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Switch Mode",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Toggle dark / light theme",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                            )
                        }
                        Switch(
                            checked = isDarkTheme ?: isSystemInDarkTheme(),
                            onCheckedChange = { isChecked ->
                                themeViewModel.toggleTheme(isChecked)
                            }
                        )
                    }

                    HorizontalDivider()

                    // Edit Profile Button
                    TextButton(
                        onClick = {
                            showSettingsSheet = false
                            showEditDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Text("Edit Profile")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsSheet = false }) {
                    Text("Close")
                }
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
    iconTint: Color,
    comparisonValue: String? = null,
    comparisonColor: Color? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.clickable(
            enabled = onClick != null,
            onClick = onClick ?: {}
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = value,
                style = AppTypography.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = title,
                style = AppTypography.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (comparisonValue != null) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = comparisonValue,
                    style = AppTypography.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = comparisonColor ?: MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
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
    currentGender: String, // Added missing parameter
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit // Updated signature
) {
    var name by remember { mutableStateOf(currentName) }
    var gender by remember { mutableStateOf(currentGender) } // Now works correctly
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
                // Now works because we added validateGender to ValidationUtils
                val genderValidation = ValidationUtils.validateGender(gender)
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
                // Fixed logic for Gender field
                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it }, // Corrected assignment
                    label = { Text("Gender") },
                    singleLine = true,
                    isError = !genderValidation.isValid, // Corrected validation check
                    supportingText = if (!genderValidation.isValid) {
                        { Text(genderValidation.errorMessage) }
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
                // Passed all 5 arguments including gender
                onClick = { onSave(name, age, weight, height, gender) },
                enabled = ValidationUtils.validateName(name).isValid &&
                        ValidationUtils.validateGender(gender).isValid &&
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