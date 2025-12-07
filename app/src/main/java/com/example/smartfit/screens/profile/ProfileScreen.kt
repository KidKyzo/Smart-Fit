package com.example.smartfit.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.smartfit.R
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.utils.ValidationUtils
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    themeViewModel: ThemeViewModel,
    userViewModel: UserViewModel,
    onBack: () -> Unit
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
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
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
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
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

                    Spacer(modifier = Modifier.height(Spacing.lg))

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
                    Spacer(modifier = Modifier.height(Spacing.md))

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
                    Spacer(modifier = Modifier.height(Spacing.md))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dark Theme",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Toggle dark mode",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        /*Switch(
                            checked = isDarkTheme ?: false,
                            onCheckedChange = { themeViewModel.toggleTheme() }
                        )*/
                    }
                }
            }

            item {
                // Achievement Card
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 1
                ) {
                    SectionHeader(title = "Achievements")
                    Spacer(modifier = Modifier.height(Spacing.md))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AchievementItem(
                            icon = Icons.Default.EmojiEvents,
                            title = "7 Day Streak",
                            color = MaterialTheme.colorScheme.primary
                        )
                        AchievementItem(
                            icon = Icons.Default.LocalFireDepartment,
                            title = "1000 Calories",
                            color = MaterialTheme.colorScheme.error
                        )
                                                    AchievementItem(
                                                        icon = Icons.AutoMirrored.Filled.DirectionsRun,
                                                        title = "50km Total",
                                                        color = MaterialTheme.colorScheme.secondary
                                                    )                    }
                }
            }
            item {
                // Account Card
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 1
                ) {
                    SectionHeader(title = "Account")
                    Spacer(modifier = Modifier.height(Spacing.md))
                    AppButton(
                        text = "Logout",
                        onClick = { userViewModel.logout() },
                        modifier = Modifier.fillMaxWidth(),
                        variant = ButtonVariant.Secondary
                    )
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
fun AchievementItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(Sizing.iconLarge)
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = title,
            style = AppTypography.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
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
