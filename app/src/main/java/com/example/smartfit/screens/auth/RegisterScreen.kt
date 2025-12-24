package com.example.smartfit.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartfit.Routes
import com.example.smartfit.ui.designsystem.Shapes
import com.example.smartfit.viewmodel.UserViewModel
import kotlinx.coroutines.launch

// Validation functions
private fun isValidEmail(email: String): Boolean {
    return email.contains("@") && 
           email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
}

private fun isValidPassword(password: String): Boolean {
    val hasMinLength = password.length >= 6
    val hasCapital = password.any { it.isUpperCase() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }
    return hasMinLength && hasCapital && hasSpecial
}

private fun getPasswordError(password: String): String? {
    return when {
        password.isEmpty() -> null
        password.length < 6 -> "Password must be at least 6 characters"
        !password.any { it.isUpperCase() } -> "Password must contain at least 1 capital letter"
        !password.any { !it.isLetterOrDigit() } -> "Password must contain at least 1 special character"
        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Error states
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    
    // Duplicate checking states
    var isCheckingUsername by remember { mutableStateOf(false) }
    var isCheckingEmail by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Create Account",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Step 1: Account Details",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(40.dp))

            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                errorContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )

            // Username Field
            TextField(
                value = username,
                onValueChange = { newValue ->
                    username = newValue
                    // Basic validation
                    if (newValue.isEmpty()) {
                        usernameError = null
                    } else if (newValue.length < 3) {
                        usernameError = "Username must be at least 3 characters"
                    } else {
                        // Check for duplicates
                        coroutineScope.launch {
                            isCheckingUsername = true
                            val exists = userViewModel.isUsernameExists(newValue)
                            if (exists) {
                                usernameError = "Username '$newValue' is already taken"
                            } else {
                                usernameError = null
                            }
                            isCheckingUsername = false
                        }
                    }
                },
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                trailingIcon = if (isCheckingUsername) {
                    { CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) }
                } else null,
                shape = Shapes.md,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                isError = usernameError != null,
                supportingText = usernameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            TextField(
                value = email,
                onValueChange = { newValue ->
                    email = newValue
                    // Basic validation
                    if (newValue.isEmpty()) {
                        emailError = null
                    } else if (!isValidEmail(newValue)) {
                        emailError = "Invalid email format"
                    } else {
                        // Check for duplicates
                        coroutineScope.launch {
                            isCheckingEmail = true
                            val exists = userViewModel.isEmailExists(newValue)
                            if (exists) {
                                emailError = "Email '$newValue' is already registered"
                            } else {
                                emailError = null
                            }
                            isCheckingEmail = false
                        }
                    }
                },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                trailingIcon = if (isCheckingEmail) {
                    { CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) }
                } else null,
                shape = Shapes.md,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                isError = emailError != null,
                supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            TextField(
                value = password,
                onValueChange = { 
                    password = it
                    passwordError = getPasswordError(it)
                    if (confirmPassword.isNotEmpty()) {
                        confirmPasswordError = if (it != confirmPassword) "Passwords do not match" else null
                    }
                },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = Shapes.md,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                isError = passwordError != null,
                supportingText = passwordError?.let { { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            TextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    confirmPasswordError = if (it.isEmpty()) null else if (it != password) "Passwords do not match" else null
                },
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            modifier = Modifier.clickable { confirmPasswordVisible = !confirmPasswordVisible }
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = Shapes.md,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                isError = confirmPasswordError != null,
                supportingText = confirmPasswordError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Validation requirements text
            Text(
                text = "Password must have: 6+ characters, 1 capital letter, 1 special character",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val isFormValid = username.length >= 3 && 
                             isValidEmail(email) && 
                             isValidPassword(password) && 
                             password == confirmPassword &&
                             usernameError == null &&
                             emailError == null &&
                             passwordError == null &&
                             confirmPasswordError == null

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    if (isFormValid) {
                        // Navigate to BioData screen with credentials
                        navController.currentBackStackEntry?.savedStateHandle?.set("username", username)
                        navController.currentBackStackEntry?.savedStateHandle?.set("email", email)
                        navController.currentBackStackEntry?.savedStateHandle?.set("password", password)
                        navController.navigate(Routes.biodata)
                    }
                },
                enabled = isFormValid
            ) {
                Text(
                    text = "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Login",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.login) {
                            popUpTo(Routes.login) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}