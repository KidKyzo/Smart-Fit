package com.example.smartfit.screens.profile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartfit.Routes
import com.example.smartfit.screens.profile.components.AvatarSelector
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.PasswordChangeResult
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    themeViewModel: ThemeViewModel,
    userViewModel: UserViewModel? = null
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val isChecked = isDarkTheme ?: systemDark

    // User data for edit profile
    val userName by userViewModel?.name?.collectAsState() ?: remember { mutableStateOf("User") }
    val avatarType by userViewModel?.avatarType?.collectAsState() ?: remember { mutableStateOf("preset") }
    val avatarId by userViewModel?.avatarId?.collectAsState() ?: remember { mutableStateOf(0) }
    val customAvatarPath by userViewModel?.customAvatarPath?.collectAsState() ?: remember { mutableStateOf<String?>(null) }

    // Dialog states
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    AppScaffold(
        topBar = {
            CompactTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Spacer(modifier = Modifier.height(Spacing.sm))

            // ========== Account Section ==========
            SettingsSection(title = "Account") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // Edit Profile
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        subtitle = "Change name and avatar",
                        onClick = { showEditProfileDialog = true }
                    )

                    // Change Password
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        subtitle = "Update your password",
                        onClick = { showChangePasswordDialog = true }
                    )
                }
            }

            // ========== Preferences Section ==========
            SettingsSection(title = "Preferences") {
                // Theme Toggle
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small),
                    shape = Shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isChecked) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = if (isChecked) "Dark Mode" else "Light Mode",
                                    style = AppTypography.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (isChecked) "Dark theme enabled" else "Light theme enabled",
                                    style = AppTypography.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isChecked,
                            onCheckedChange = { themeViewModel.toggleTheme(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }

            // ========== Information Section ==========
            SettingsSection(title = "Information") {
                Column(
                    modifier = Modifier.padding(horizontal = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // FAQ Items
                    ExpandableInfoSection(
                        title = "How do I track my activities?",
                        content = "Navigate to the Home screen and tap on 'Log Activity' or use the quick action buttons. You can log various activities like running, cycling, hiking, and more. Each activity tracks duration, calories burned, and distance covered."
                    )

                    ExpandableInfoSection(
                        title = "How does calorie counting work?",
                        content = "SmartFit calculates calories based on your activity type, duration, and personal metrics (weight, height, age). For food tracking, search for foods in the Plan section and log your intake. The app sums up your daily consumption and burned calories."
                    )

                    ExpandableInfoSection(
                        title = "How do I set daily goals?",
                        content = "Your daily step goal can be customized from the Home screen. Tap on the step counter to adjust your target. Activity and calorie goals are based on your profile information and recommended daily values."
                    )

                    ExpandableInfoSection(
                        title = "Is my data secure?",
                        content = "Yes! All your data is stored locally on your device using encrypted storage. Your password is securely hashed using SHA-256 encryption. We do not share your personal information with third parties."
                    )

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    // Privacy Policy
                    ExpandableInfoSection(
                        title = "Privacy Policy",
                        content = """
SmartFit Privacy Policy

Last Updated: December 2024

1. Data Collection
We collect only the information you provide: name, age, weight, height, gender, and activity data. This data is stored locally on your device.

2. Data Usage
Your data is used solely to provide personalized fitness tracking and recommendations. We do not sell or share your data with third parties.

3. Data Storage
All data is stored locally using Android's Room database with encryption. Your password is hashed using industry-standard SHA-256 algorithm.

4. Your Rights
You can export, modify, or delete your data at any time through the app settings.

5. Contact
For privacy concerns, please contact the app developer through the app store listing.
                        """.trimIndent()
                    )
                }
            }

            // ========== Account Actions Section ==========
            SettingsSection(title = "Account Actions") {
                // Logout Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small),
                    shape = Shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    onClick = { showLogoutConfirmDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Logout",
                            style = AppTypography.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }

    // ========== Dialogs ==========

    // Edit Profile Dialog
    if (showEditProfileDialog && userViewModel != null) {
        EditProfileNameAvatarDialog(
            currentName = userName,
            currentAvatarType = avatarType,
            currentAvatarId = avatarId,
            currentCustomPath = customAvatarPath,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, newAvatarType, newAvatarId, newCustomPath ->
                userViewModel.updateName(name)
                if (newAvatarType == "preset") {
                    userViewModel.updatePresetAvatar(newAvatarId)
                } else if (newCustomPath != null) {
                    userViewModel.updateCustomAvatar(newCustomPath)
                }
                showEditProfileDialog = false
            }
        )
    }

    // Change Password Dialog
    if (showChangePasswordDialog && userViewModel != null) {
        ChangePasswordDialog(
            userViewModel = userViewModel,
            onDismiss = { showChangePasswordDialog = false }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out of your account?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmDialog = false
                        userViewModel?.logout()
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
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = AppTypography.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.xs)
        )
        content()
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small),
        shape = Shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTypography.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = AppTypography.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChangePasswordDialog(
    userViewModel: UserViewModel,
    onDismiss: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isLoading by userViewModel.isLoading.collectAsState()
    val passwordChangeResult by userViewModel.passwordChangeResult.collectAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Handle password change result
    LaunchedEffect(passwordChangeResult) {
        when (passwordChangeResult) {
            is PasswordChangeResult.Success -> {
                userViewModel.clearPasswordChangeResult()
                onDismiss()
            }
            is PasswordChangeResult.Error -> {
                errorMessage = (passwordChangeResult as PasswordChangeResult.Error).message
            }
            null -> {}
        }
    }

    AlertDialog(
        onDismissRequest = {
            userViewModel.clearPasswordChangeResult()
            onDismiss()
        },
        icon = { Icon(Icons.Default.Lock, contentDescription = null) },
        title = { Text("Change Password") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { 
                        currentPassword = it
                        errorMessage = null
                    },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = newPassword != confirmPassword && confirmPassword.isNotEmpty(),
                    supportingText = if (newPassword != confirmPassword && confirmPassword.isNotEmpty()) {
                        { Text("Passwords do not match") }
                    } else null
                )

                errorMessage?.let { error ->
                    Text(
                        text = error,
                        style = AppTypography.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Text(
                    text = "Password must be at least 6 characters",
                    style = AppTypography.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        currentPassword.isEmpty() -> errorMessage = "Current password is required"
                        newPassword.length < 6 -> errorMessage = "New password must be at least 6 characters"
                        newPassword != confirmPassword -> errorMessage = "Passwords do not match"
                        else -> userViewModel.changePassword(currentPassword, newPassword)
                    }
                },
                enabled = !isLoading && currentPassword.isNotEmpty() && 
                          newPassword.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Change Password")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                userViewModel.clearPasswordChangeResult()
                onDismiss()
            }) {
                Text("Cancel")
            }
        }
    )
}