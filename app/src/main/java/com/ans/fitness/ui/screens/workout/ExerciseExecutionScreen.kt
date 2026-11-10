package com.ans.fitness.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ans.fitness.data.model.Exercise
import com.ans.fitness.data.model.WorkoutSet
import com.ans.fitness.data.repository.FitnessRepository
import com.ans.fitness.ui.components.MuscleBodyDiagram
import com.ans.fitness.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseExecutionScreen(
    exerciseId: String,
    repository: FitnessRepository,
    onBack: () -> Unit
) {
    val exercise: Exercise = remember {
        repository.getExerciseById(exerciseId) ?: repository.getAllExercises().first()
    }

    // Default 3 sets
    var sets by remember {
        mutableStateOf(
            listOf(
                WorkoutSet(setNumber = 1, targetReps = exercise.defaultReps, targetDurationSeconds = exercise.defaultDurationSeconds, isTimeBased = exercise.isTimeBased),
                WorkoutSet(setNumber = 2, targetReps = exercise.defaultReps, targetDurationSeconds = exercise.defaultDurationSeconds, isTimeBased = exercise.isTimeBased),
                WorkoutSet(setNumber = 3, targetReps = exercise.defaultReps, targetDurationSeconds = exercise.defaultDurationSeconds, isTimeBased = exercise.isTimeBased)
            )
        )
    }

    // Mandatory 30-Second Cooldown Timer State
    var cooldownSeconds by remember { mutableIntStateOf(0) }

    // Run countdown timer when cooldownSeconds > 0
    LaunchedEffect(cooldownSeconds) {
        if (cooldownSeconds > 0) {
            delay(1000L)
            cooldownSeconds -= 1
        }
    }

    val allSetsDone = sets.all { it.isCompleted }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = exercise.name,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${exercise.muscleGroup.displayName} • ${exercise.mode.displayName}",
                            color = TextSecondary,
                            fontSize = 12.sp
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // TOP HALF: Clean 2D anatomical human body SVG diagram with dynamic target muscle highlight
            MuscleBodyDiagram(
                activeMuscle = exercise.muscleGroup,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Mandatory 30-Second Cooldown Alert Banner
            if (cooldownSeconds > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, AccentPrimary, RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Cooldown Timer",
                                tint = AccentPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "30-SECOND REST COOLDOWN",
                                    color = AccentPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Buttons locked to prevent accidental double-taps.",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = "00:%02d".format(cooldownSeconds),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // BOTTOM HALF: Set Logger
            Text(
                text = "SET LOGGER",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(sets) { index, setItem ->
                    SetRowItem(
                        set = setItem,
                        isLockedByCooldown = cooldownSeconds > 0 && !setItem.isCompleted,
                        cooldownFormatted = "00:%02d".format(cooldownSeconds),
                        onWeightChange = { newWeight ->
                            sets = sets.toMutableList().also {
                                it[index] = it[index].copy(weightKg = newWeight)
                            }
                        },
                        onDoneClick = {
                            // Mark set completed
                            sets = sets.toMutableList().also {
                                it[index] = it[index].copy(isCompleted = true)
                            }

                            // Save to database
                            repository.logSetCompletion(
                                exerciseId = exercise.id,
                                exerciseName = exercise.name,
                                setNumber = setItem.setNumber,
                                reps = setItem.targetReps,
                                weightKg = setItem.weightKg
                            )

                            // Check if this was the last set or if there are remaining incomplete sets
                            val hasRemaining = sets.any { !it.isCompleted }
                            if (hasRemaining) {
                                // Trigger mandatory 30-second cooldown lock
                                cooldownSeconds = 30
                            } else {
                                // Exercise fully completed!
                                val todayExercises = repository.getTodayExercises()
                                repository.evaluateStreakForToday(todayExercises.size)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Done / Return Button
            Button(
                onClick = {
                    if (allSetsDone) {
                        val todayExercises = repository.getTodayExercises()
                        repository.evaluateStreakForToday(todayExercises.size)
                    }
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (allSetsDone) AccentSuccess else SurfaceVariantDark,
                    contentColor = TextPrimary
                )
            ) {
                Text(
                    text = if (allSetsDone) "✓ Exercise Completed (Return)" else "Return to Overview",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SetRowItem(
    set: WorkoutSet,
    isLockedByCooldown: Boolean,
    cooldownFormatted: String,
    onWeightChange: (Float) -> Unit,
    onDoneClick: () -> Unit
) {
    var weightInput by remember { mutableStateOf(if (set.weightKg > 0f) set.weightKg.toString() else "") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(
                1.dp,
                if (set.isCompleted) AccentSuccess else SurfaceBorder,
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Set label and target
        Column(modifier = Modifier.width(90.dp)) {
            Text(
                text = "Set ${set.setNumber}",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (set.isTimeBased) "${set.targetDurationSeconds} sec" else "${set.targetReps} reps",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Weight Input (for weighted exercises)
        if (!set.isTimeBased) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(110.dp)
            ) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = {
                        weightInput = it
                        it.toFloatOrNull()?.let { w -> onWeightChange(w) }
                    },
                    placeholder = { Text("0", color = TextMuted) },
                    singleLine = true,
                    enabled = !set.isCompleted,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = SurfaceBorder,
                        disabledBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        disabledTextColor = TextSecondary,
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark,
                        disabledContainerColor = SurfaceVariantDark
                    ),
                    shape = RoundedCornerShape(6.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "kg", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            Spacer(modifier = Modifier.width(110.dp))
        }

        // Action Button: Done / Wait / Completed
        when {
            set.isCompleted -> {
                Button(
                    onClick = {},
                    enabled = false,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = AccentSuccess,
                        disabledContentColor = Color.White
                    ),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            isLockedByCooldown -> {
                // Mandatory lock during 30s cooldown
                Button(
                    onClick = {},
                    enabled = false,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = SurfaceVariantDark,
                        disabledContentColor = TextMuted
                    ),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(text = cooldownFormatted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            else -> {
                Button(
                    onClick = onDoneClick,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(text = "Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

