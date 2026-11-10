package com.ans.fitness.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ans.fitness.data.model.MuscleGroup
import com.ans.fitness.data.model.UserProfile
import com.ans.fitness.data.model.WorkoutMode
import com.ans.fitness.data.repository.FitnessRepository
import com.ans.fitness.ui.theme.*
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ProfileScreen(
    repository: FitnessRepository,
    onNavigateManageExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    var profile by remember { mutableStateOf(repository.getUserProfile()) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showRestDaysDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    fun refresh() {
        profile = repository.getUserProfile()
    }

    // Local device gallery photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            repository.updatePhotoUri(uri.toString())
            refresh()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Profile & Settings",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Manage your athlete identity and workout routines.",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 1. USER IDENTITY CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Photo with local device gallery picker
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(SurfaceVariantDark)
                        .border(2.dp, AccentPrimary, CircleShape)
                        .clickable { photoPickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (profile?.photoUri != null) {
                        AsyncImage(
                            model = profile?.photoUri,
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Placeholder",
                            tint = TextMuted,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    // Camera badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(AccentPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Editable Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showEditNameDialog = true }
                ) {
                    Text(
                        text = profile?.name ?: "Athlete",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Name",
                        tint = AccentPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = profile?.reason ?: "Fitness Goal",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // User Metrics Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MetricItem(
                        label = "Weight",
                        value = "%.1f kg".format(profile?.weightKg ?: 0f)
                    )
                    VerticalDivider(
                        color = SurfaceBorder,
                        modifier = Modifier.height(28.dp),
                        thickness = 1.dp
                    )
                    MetricItem(
                        label = "Height",
                        value = "%.0f cm".format(profile?.heightCm ?: 0f)
                    )
                    VerticalDivider(
                        color = SurfaceBorder,
                        modifier = Modifier.height(28.dp),
                        thickness = 1.dp
                    )
                    MetricItem(
                        label = "BMI",
                        value = "%.1f".format(profile?.bmi ?: 0f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ROUTINE & EXERCISE MANAGEMENT",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        // 2. MANAGE EXERCISES (WEEKLY ROUTINE BUILDER)
        ProfileOptionCard(
            title = "Manage Exercises",
            subtitle = "7-Day Weekly Routine Builder (repeats weekly/monthly)",
            icon = Icons.Default.CalendarMonth,
            accentColor = AccentPrimary,
            onClick = onNavigateManageExercises
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 3. SET REST DAYS
        val restDaysStr = repository.getRestDays()
            .joinToString(", ") { it.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) }
        ProfileOptionCard(
            title = "Set Rest Days",
            subtitle = "Current: $restDaysStr (Hard Rule: >= 1 day required)",
            icon = Icons.Default.Weekend,
            accentColor = Color(0xFF58A6FF),
            onClick = { showRestDaysDialog = true }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 4. ADD CUSTOM EXERCISE
        ProfileOptionCard(
            title = "Add Custom Exercise",
            subtitle = "Add new movements to the master exercise database",
            icon = Icons.Default.FitnessCenter,
            accentColor = AccentSuccess,
            onClick = { showAddExerciseDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        var newName by remember { mutableStateOf(profile?.name ?: "") }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = {
                Text(text = "Edit Athlete Name", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    placeholder = { Text("Enter your name", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            repository.updateName(newName.trim())
                            refresh()
                        }
                        showEditNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
                ) {
                    Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Set Rest Days Dialog (With Hard Rule Validation)
    if (showRestDaysDialog) {
        SetRestDaysDialog(
            initialRestDays = repository.getRestDays(),
            onDismiss = { showRestDaysDialog = false },
            onSave = { updatedDays ->
                repository.updateRestDays(updatedDays)
                refresh()
                showRestDaysDialog = false
            }
        )
    }

    // Add Custom Exercise Dialog
    if (showAddExerciseDialog) {
        AddCustomExerciseDialog(
            onDismiss = { showAddExerciseDialog = false },
            onSave = { name, muscle, mode, isTimeBased ->
                repository.addCustomExercise(name, muscle, mode, isTimeBased)
                refresh()
                showAddExerciseDialog = false
            }
        )
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = TextMuted
            )
        }
    }
}
