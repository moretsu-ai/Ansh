package com.ans.fitness.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
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
import com.ans.fitness.data.repository.FitnessRepository
import com.ans.fitness.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutOverviewScreen(
    repository: FitnessRepository,
    onBack: () -> Unit,
    onSelectExercise: (exerciseId: String) -> Unit,
    onFinishWorkout: () -> Unit
) {
    val activeMode = repository.getActiveMode()
    val exercises = remember { repository.getTodayExercises(activeMode) }
    var completedIds by remember { mutableStateOf(repository.getTodayCompletedExerciseIds()) }

    val total = exercises.size
    val completed = completedIds.size
    val ratio = if (total > 0) completed.toFloat() / total else 0f
    val isStreakUnlocked = ratio >= 0.5f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Today's Workout Session",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${activeMode.displayName} Mode • $total Scheduled",
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
                .padding(16.dp)
        ) {
            // 50% Streak Completion Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isStreakUnlocked) AccentSuccess else SurfaceBorder,
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = if (isStreakUnlocked) AccentSuccess else AccentPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "STREAK REQUIREMENT",
                                color = if (isStreakUnlocked) AccentSuccess else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "$completed / $total (${(ratio * 100).toInt()}%)",
                            color = if (isStreakUnlocked) AccentSuccess else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { ratio.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isStreakUnlocked) AccentSuccess else AccentPrimary,
                        trackColor = SurfaceVariantDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isStreakUnlocked) {
                            "✓ Streak Qualified! You completed >= 50% of today's exercises."
                        } else {
                            "Complete at least 50% (${(total + 1) / 2} exercises) to increment today's streak."
                        },
                        color = if (isStreakUnlocked) AccentSuccess else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = if (isStreakUnlocked) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SELECT EXERCISE TO EXECUTE",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(exercises) { index, exercise ->
                    val isDone = completedIds.contains(exercise.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(
                                1.dp,
                                if (isDone) AccentSuccess else SurfaceBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSelectExercise(exercise.id) }
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
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) AccentSuccess else SurfaceVariantDark),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${index + 1}",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = exercise.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${exercise.muscleGroup.displayName} • " +
                                            if (exercise.isTimeBased) "${exercise.defaultDurationSeconds}s hold" else "${exercise.defaultReps} reps/set",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Go",
                            tint = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    repository.evaluateStreakForToday(total)
                    onFinishWorkout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isStreakUnlocked) AccentSuccess else SurfaceVariantDark,
                    contentColor = TextPrimary
                )
            ) {
                Text(
                    text = if (isStreakUnlocked) "Finish Workout & Save Streak" else "Finish & Return Home",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

