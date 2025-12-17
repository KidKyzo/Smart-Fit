package com.example.smartfit.screens.profile.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartfit.R
import com.example.smartfit.ui.designsystem.*
import java.io.File
import java.io.FileOutputStream

// Maximum constraints for custom images
const val MAX_IMAGE_SIZE_PIXELS = 512
const val MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024 // 2MB

/**
 * Avatar selection component with preset avatars and custom upload
 */
@Composable
fun AvatarSelector(
    currentAvatarType: String,
    currentAvatarId: Int,
    currentCustomPath: String?,
    onPresetSelected: (Int) -> Unit,
    onCustomSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCropDialog by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Preset avatar resource IDs
    val presetAvatars = listOf(
        R.drawable.hanni_profile,
        R.drawable.big_bang,
        R.drawable.faker,
        R.drawable.haerin,
        R.drawable.kim_chaewon
    )

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            // Check file size
            val fileSize = getFileSize(context, it)
            if (fileSize > MAX_FILE_SIZE_BYTES) {
                errorMessage = "Image too large. Maximum size is 2MB."
            } else {
                selectedUri = it
                showCropDialog = true
                errorMessage = null
            }
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Choose Avatar",
            style = AppTypography.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Preset avatars row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            contentPadding = PaddingValues(horizontal = Spacing.xs)
        ) {
            itemsIndexed(presetAvatars) { index, resourceId ->
                AvatarOption(
                    isSelected = currentAvatarType == "preset" && currentAvatarId == index,
                    onClick = { onPresetSelected(index) }
                ) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = "Avatar option ${index + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Custom upload option
            item {
                AvatarOption(
                    isSelected = currentAvatarType == "custom",
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    if (currentAvatarType == "custom" && currentCustomPath != null) {
                        val bitmap = remember(currentCustomPath) {
                            BitmapFactory.decodeFile(currentCustomPath)
                        }
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Custom avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Upload custom",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Upload",
                                style = AppTypography.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Error message
        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = error,
                style = AppTypography.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        // Size info
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "Max size: 2MB • Will be cropped to square",
            style = AppTypography.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    // Crop dialog
    if (showCropDialog && selectedUri != null) {
        ImageCropDialog(
            imageUri = selectedUri!!,
            onDismiss = {
                showCropDialog = false
                selectedUri = null
            },
            onConfirm = { croppedBitmap ->
                val savedPath = saveCroppedImage(context, croppedBitmap)
                if (savedPath != null) {
                    onCustomSelected(savedPath)
                } else {
                    errorMessage = "Failed to save image"
                }
                showCropDialog = false
                selectedUri = null
            }
        )
    }
}

@Composable
private fun AvatarOption(
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
        
        // Selection indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Image crop dialog for custom avatar upload
 */
@Composable
fun ImageCropDialog(
    imageUri: Uri,
    onDismiss: () -> Unit,
    onConfirm: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(true) }

    // Load and process image
    LaunchedEffect(imageUri) {
        bitmap = loadAndResizeBitmap(context, imageUri, MAX_IMAGE_SIZE_PIXELS)
        isProcessing = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Icon(Icons.Default.CropSquare, contentDescription = null)
                Text("Crop Image")
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text("Processing image...")
                } else {
                    bitmap?.let { bmp ->
                        // Show cropped preview (square)
                        val croppedBitmap = remember(bmp) { cropToSquare(bmp) }
                        Image(
                            bitmap = croppedBitmap.asImageBitmap(),
                            contentDescription = "Preview",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = "Image will be cropped to a square and resized to ${MAX_IMAGE_SIZE_PIXELS}x${MAX_IMAGE_SIZE_PIXELS}",
                            style = AppTypography.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    bitmap?.let { bmp ->
                        val cropped = cropToSquare(bmp)
                        onConfirm(cropped)
                    }
                },
                enabled = !isProcessing && bitmap != null
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper functions

private fun getFileSize(context: Context, uri: Uri): Long {
    return try {
        context.contentResolver.openInputStream(uri)?.use { it.available().toLong() } ?: 0L
    } catch (e: Exception) {
        0L
    }
}

private fun loadAndResizeBitmap(context: Context, uri: Uri, maxSize: Int): Bitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            if (originalBitmap != null) {
                // Calculate scale
                val scale = minOf(
                    maxSize.toFloat() / originalBitmap.width,
                    maxSize.toFloat() / originalBitmap.height
                )
                
                if (scale < 1f) {
                    val newWidth = (originalBitmap.width * scale).toInt()
                    val newHeight = (originalBitmap.height * scale).toInt()
                    Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                } else {
                    originalBitmap
                }
            } else null
        }
    } catch (e: Exception) {
        null
    }
}

private fun cropToSquare(bitmap: Bitmap): Bitmap {
    val size = minOf(bitmap.width, bitmap.height)
    val x = (bitmap.width - size) / 2
    val y = (bitmap.height - size) / 2
    return Bitmap.createBitmap(bitmap, x, y, size, size)
}

private fun saveCroppedImage(context: Context, bitmap: Bitmap): String? {
    return try {
        val filename = "avatar_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}
