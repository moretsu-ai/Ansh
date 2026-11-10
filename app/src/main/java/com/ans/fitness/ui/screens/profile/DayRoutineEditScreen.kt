package com.ans.fitness.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ans.fitness.data.model.Exercise
import com.ans.fitness.data.model.MuscleGroup
import com.ans.fitness.data.repository.FitnessRepository
import com.ans.fitness.ui.theme.*
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayRoutineEditScreen(
    dayOfWeek: DayOfWeek,
    repository: FitnessRepository,
    onBack: () -> Unit
) {
    val currentMode = remember { repository.getActiveMode() }
    val initialConfig = remember { repository.getDayRoutineConfig(dayOfWeek) }

    var selectedMuscles by remember { mutableStateOf(initialConfig.muscleGroups.toSet()) }
    var currentExercises by remember { mutableStateOf(repository.getRoutineForDay(dayOfWeek, currentMode)) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    val dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    // Function to handle switching muscle group combination
    fun toggleMuscle(muscle: MuscleGroup) {
        val updated = selectedMuscles.toMutableSet()
        if (updated.contains(muscle)) {
            if (updated.size > 1) { // keep at least 1 muscle group
                updated.remove(muscle)
            }
        } else {
            updated.add(muscle)
        }
        selectedMuscles = updated

        // Switching muscle group auto-populates the default exercises for that group based on active mode
        repository.updateDayMuscles(dayOfWeek, updated.toList(), currentMode)
        currentExercises = repository.getRoutineForDay(dayOfWeek, currentMode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "$dayName Routine",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${currentMode.displayName} Mode • ${currentExercises.size} Exercises",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BgDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 1. Target Muscle Group Combination Selector
            Text(
                text = "TARGET MUSCLE GROUPS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Selecting/deselecting auto-populates default exercises for this day.",
                color = TextMuted,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            MuscleGroup.entries.chunked(4).forEach { chunk ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    chunk.forEach { muscle ->
                        val isSel = selectedMuscles.contains(muscle)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AccentPrimary else SurfaceVariantDark)
                                .clickable { toggleMuscle(muscle) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = muscle.displayName,
                                color = if (isSel) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Assigned Exercises with '-' (Minus) buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ASSIGNED EXERCISES",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${currentExercises.size} Total",
                    color = AccentPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentExercises) { exercise ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exercise.name,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${exercise.muscleGroup.displayName} • " +
                                        if (exercise.isTimeBased) "${exercise.defaultDurationSeconds}s" else "${exercise.defaultReps} reps",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // '-' (Minus) button to remove this specific exercise
                        IconButton(
                            onClick = {
                                repository.removeExerciseFromDay(dayOfWeek, exercise.id)
                                currentExercises = repository.getRoutineForDay(dayOfWeek, currentMode)
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantDark)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Remove Exercise",
                                tint = Color(0xFFF85149),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom '+ Add New Exercise' Button
            Button(
                onClick = { showAddExerciseDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceVariantDark,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(18.dp),
                    tint = AccentPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+ Add New Exercise to $dayName",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentPrimary
                )
            }
        }
    }

    // Picker Dialog to add an exercise to this day
    if (showAddExerciseDialog) {
        val availableExercises = repository.getAllExercises()
            .filter { it.mode == currentMode && !currentExercises.any { ce -> ce.id == it.id } }

        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = {
                Text(
                    text = "Add Exercise to $dayName",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (availableExercises.isEmpty()) {
                    Text(
                        text = "All available ${currentMode.displayName} exercises are already assigned to this day.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(availableExercises) { exercise ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceVariantDark)
                                    .clickable {
                                        repository.addExerciseToDay(dayOfWeek, exercise.id)
                                        currentExercises = repository.getRoutineForDay(dayOfWeek, currentMode)
                                        showAddExerciseDialog = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = exercise.name,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = exercise.muscleGroup.displayName,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = AccentPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

