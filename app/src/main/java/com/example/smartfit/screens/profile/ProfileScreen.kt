package com.example.smartfit.screens.profile

// REMOVED: import android.content.ContentValues.TAG (This was causing the wrong tag)
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartfit.R
import com.example.smartfit.Routes
import com.example.smartfit.screens.profile.components.AvatarSelector
import com.example.smartfit.screens.profile.components.BMIIndicator
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.ui.designsystem.Shapes
import com.example.smartfit.ui.theme.*
import com.example.smartfit.utils.ValidationUtils
import com.example.smartfit.viewmodel.ActivityViewModel
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

// --- ADDED THIS CONSTANT SO LOGCAT FILTER WORKS ---
private const val TAG = "DebugSmartApp"

// Preset avatar resource IDs
val presetAvatars = listOf(
    R.drawable.hanni_profile,
    R.drawable.big_bang,
    R.drawable.faker,
    R.drawable.haerin,
    R.drawable.kim_chaewon
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun ProfileScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    userViewModel: UserViewModel,
    activityViewModel: ActivityViewModel
) {
    // Load persisted user data
    val userName by userViewModel.name.collectAsState()
    val userAge by userViewModel.age.collectAsState()
    val userWeight by userViewModel.weight.collectAsState()
    val userHeight by userViewModel.height.collectAsState()
    val userGender by userViewModel.gender.collectAsState()
    val avatarType by userViewModel.avatarType.collectAsState()
    val avatarId by userViewModel.avatarId.collectAsState()
    val customAvatarPath by userViewModel.customAvatarPath.collectAsState()

    var showEditPersonalInfoDialog by remember { mutableStateOf(false) }
    var showEditNameAvatarDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val isDarkMode = isSystemInDarkTheme()

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
                title = { Text("Profile") }
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
                // Profile Header with Avatar - CLICKABLE to edit
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar with edit badge
                    Box(
                        modifier = Modifier.size(Sizing.profileImageSizeLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .size(Sizing.profileImageSizeLarge)
                                .clip(Shapes.circle)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    BorderStroke(
                                        BorderWidth.thick,
                                        MaterialTheme.colorScheme.primary
                                    ),
                                    Shapes.circle
                                )
                                .clickable { showEditNameAvatarDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            // Display avatar based on type
                            if (avatarType == "custom" && customAvatarPath != null) {
                                val bitmap = remember(customAvatarPath) {
                                    BitmapFactory.decodeFile(customAvatarPath)
                                }
                                bitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "Profile Picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(Sizing.profileImageSize)
                                            .clip(Shapes.circle)
                                    )
                                } ?: run {
                                    // Fallback if bitmap fails to load
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = "Profile",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                // Preset avatar
                                val resourceId =
                                    presetAvatars.getOrElse(avatarId) { presetAvatars[0] }
                                Image(
                                    painter = painterResource(id = resourceId),
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(Sizing.profileImageSize)
                                        .clip(Shapes.circle)
                                )
                            }
                        }

                        // Edit badge - positioned outside at bottom-right
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = (-4).dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                .clickable { showEditNameAvatarDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit profile",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    // Username (non-clickable)
                    Text(
                        text = userName,
                        style = AppTypography.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))
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
                // Personal Information Card - CLICKABLE
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEditPersonalInfoDialog = true },
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = Shapes.medium
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Personal Information",
                                style = AppTypography.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(Spacing.sm))

                        InfoRow("Age", "$userAge years")
                        InfoRow("Weight", "$userWeight kg")
                        InfoRow("Height", "$userHeight cm")
                        InfoRow("Gender", userGender)
                    }
                }
            }

            item {
                // Weekly Report Card
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("weekly_report/0") },
                    elevation = 1
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly Report",
                            style = AppTypography.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = { navController.navigate("weekly_report/0") },
                            contentPadding = PaddingValues(horizontal = Spacing.xs)
                        ) {
                            Text(
                                text = "View detail",
                                color = MaterialTheme.colorScheme.primary,
                                style = AppTypography.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "View detail",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        ReportCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Workout",
                            value = "$currentWeekDuration min",
                            comparisonValue = if (durationDiff >= 0)
                                "+$durationDiff min" else "$durationDiff min",
                            comparisonColor = if (durationDiff >= 0) md_theme_light_primary else MaterialTheme.colorScheme.error,
                            icon = Icons.Default.FitnessCenter,
                            iconTint = md_theme_light_primary,
                            onClick = { navController.navigate("weekly_report/0") }
                        )

                        ReportCard(
                            modifier = Modifier.weight(1f),
                            title = "Avg Daily Steps",
                            value = "$currentWeekSteps",
                            comparisonValue = if (stepsDiff >= 0) "+$stepsDiff" else "$stepsDiff",
                            comparisonColor = if (stepsDiff >= 0) md_theme_light_primary else MaterialTheme.colorScheme.error,
                            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                            iconTint = md_theme_light_primary,
                            onClick = { navController.navigate("weekly_report/0") }
                        )
                    }
                }
            }

            item {
                // Add spacing before navigation boxes
                Spacer(modifier = Modifier.height(Spacing.xs))

                // Settings Box - Full width, separate row
                NavigationBox(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = if (isDarkMode) 0.7f else 1f
                    ),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { navController.navigate(Routes.setting) }
                )
            }

            item {
                // Logout Box - Full width, separate row
                NavigationBox(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Logout",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                        alpha = if (isDarkMode) 0.7f else 1f
                    ),
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    onClick = { showLogoutDialog = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }

    // Edit Name and Avatar Dialog
    // Edit Name and Avatar Dialog
    if (showEditNameAvatarDialog) {
        EditProfileNameAvatarDialog(
            currentName = userName,
            currentAvatarType = avatarType,
            currentAvatarId = avatarId,
            currentCustomPath = customAvatarPath,
            onDismiss = { showEditNameAvatarDialog = false },
            onSave = { name, newAvatarType, newAvatarId, newCustomPath ->

                // --- DEBUG LOGS START (For Report) ---
                android.util.Log.d(TAG, "Profile Update: User initiated save action")
                android.util.Log.d(TAG, "Profile Update: Processing new name -> '$name'")
                android.util.Log.d(TAG, "Profile Update: Processing avatar -> Type: $newAvatarType, ID: $newAvatarId")
                android.util.Log.d(TAG, "Profile Update: Committing changes to ViewModel")
                // --- DEBUG LOGS END ---

                // Save name
                userViewModel.updateName(name)
                // Save avatar
                if (newAvatarType == "preset") {
                    userViewModel.updatePresetAvatar(newAvatarId)
                } else if (newCustomPath != null) {
                    userViewModel.updateCustomAvatar(newCustomPath)
                }
                showEditNameAvatarDialog = false
            }
        )
    }

    // Edit Personal Info Dialog (Age, Weight, Height, Gender - NOT Name)
    if (showEditPersonalInfoDialog) {
        EditPersonalInfoDialog(
            currentAge = userAge,
            currentWeight = userWeight,
            currentHeight = userHeight,
            currentGender = userGender,
            onDismiss = { showEditPersonalInfoDialog = false },
            onSave = { age, weight, height, gender ->
                userViewModel.updateBiodata(age, weight, height, gender)
                showEditPersonalInfoDialog = false
            }
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // --- DEBUG LOGS START ---
                        // These exact lines produce the logs for your report
                        android.util.Log.d(TAG, "Logout Action: User confirmed logout dialog")
                        android.util.Log.d(TAG, "Logout Action: Clearing user session data")
                        android.util.Log.d(
                            TAG,
                            "Logout Navigation: Navigating to Login & clearing backstack"
                        )
                        // --- DEBUG LOGS END ---

                        showLogoutDialog = false
                        userViewModel.logout()
                        navController.navigate(Routes.login) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun NavigationBox(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.medium),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Reduced padding to make the box height smaller
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                style = AppTypography.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
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
        shape = Shapes.lg,
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
fun EditProfileNameAvatarDialog(
    currentName: String,
    currentAvatarType: String,
    currentAvatarId: Int,
    currentCustomPath: String?,
    onDismiss: () -> Unit,
    onSave: (name: String, avatarType: String, avatarId: Int, customPath: String?) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedAvatarType by remember { mutableStateOf(currentAvatarType) }
    var selectedAvatarId by remember { mutableStateOf(currentAvatarId) }
    var selectedCustomPath by remember { mutableStateOf(currentCustomPath) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                AvatarSelector(
                    currentAvatarType = selectedAvatarType,
                    currentAvatarId = selectedAvatarId,
                    currentCustomPath = selectedCustomPath,
                    onPresetSelected = { id ->
                        selectedAvatarType = "preset"
                        selectedAvatarId = id
                        selectedCustomPath = null
                    },
                    onCustomSelected = { path ->
                        selectedAvatarType = "custom"
                        selectedCustomPath = path
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, selectedAvatarType, selectedAvatarId, selectedCustomPath) },
                enabled = name.isNotBlank()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPersonalInfoDialog(
    currentAge: String,
    currentWeight: String,
    currentHeight: String,
    currentGender: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var age by remember { mutableStateOf(currentAge) }
    var weight by remember { mutableStateOf(currentWeight) }
    var height by remember { mutableStateOf(currentHeight) }
    var gender by remember { mutableStateOf(currentGender) }
    var isGenderMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Personal Information") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "Update your health metrics",
                    style = AppTypography.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val ageValidation = ValidationUtils.validateAge(age)
                val weightValidation = ValidationUtils.validateWeight(weight)
                val heightValidation = ValidationUtils.validateHeight(height)

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    singleLine = true,
                    isError = !ageValidation.isValid,
                    supportingText = if (!ageValidation.isValid) {
                        { Text(ageValidation.errorMessage) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    isError = !weightValidation.isValid,
                    supportingText = if (!weightValidation.isValid) {
                        { Text(weightValidation.errorMessage) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    singleLine = true,
                    isError = !heightValidation.isValid,
                    supportingText = if (!heightValidation.isValid) {
                        { Text(heightValidation.errorMessage) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = isGenderMenuExpanded,
                    onExpandedChange = { isGenderMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderMenuExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    )

                    ExposedDropdownMenu(
                        expanded = isGenderMenuExpanded,
                        onDismissRequest = { isGenderMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Male") },
                            onClick = {
                                gender = "Male"
                                isGenderMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Female") },
                            onClick = {
                                gender = "Female"
                                isGenderMenuExpanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(age, weight, height, gender) },
                enabled = ValidationUtils.validateAge(age).isValid &&
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