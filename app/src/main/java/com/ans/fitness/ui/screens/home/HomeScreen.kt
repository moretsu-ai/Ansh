package com.ans.fitness.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Weekend
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
import com.ans.fitness.data.model.PersonalRecord
import com.ans.fitness.data.model.WorkoutMode
import com.ans.fitness.data.repository.FitnessRepository
import com.ans.fitness.ui.components.AnsTopBar
import com.ans.fitness.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    repository: FitnessRepository,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(repository.getActiveMode()) }
    var streak by remember { mutableIntStateOf(repository.getCurrentStreak()) }
    var isRestDay by remember { mutableStateOf(repository.isTodayRestDay()) }
    var todayExercises by remember { mutableStateOf(repository.getTodayExercises(activeMode)) }
    var personalRecords by remember { mutableStateOf(repository.getPersonalRecords()) }
    val todayCompletedIds by remember { mutableStateOf(repository.getTodayCompletedExerciseIds()) }
    val completedDates by remember { mutableStateOf(repository.getAllCompletedDates()) }

    // Refresh when mode or state changes
    fun refresh() {
        activeMode = repository.getActiveMode()
        streak = repository.getCurrentStreak()
        isRestDay = repository.isTodayRestDay()
        todayExercises = repository.getTodayExercises(activeMode)
        personalRecords = repository.getPersonalRecords()
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    val todayDate = LocalDate.now()
    val todayDayOfWeek = todayDate.dayOfWeek
    val todayMuscles = repository.getTodayMuscleGroups()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // Top Bar with Logo & Mode Switch
        AnsTopBar(
            activeMode = activeMode,
            onModeToggle = { newMode ->
                repository.setActiveMode(newMode)
                refresh()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // SECTION 1: WORKOUT STREAK TRACKER (Top Section)
            StreakTrackerSection(
                streak = streak,
                todayDate = todayDate,
                completedDates = completedDates,
                restDays = repository.getRestDays()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 2: TODAY'S WORKOUT CARD (Middle Section)
            TodayWorkoutSection(
                dayOfWeek = todayDayOfWeek,
                isRestDay = isRestDay,
                muscles = todayMuscles.map { it.displayName },
                activeMode = activeMode,
                exercises = todayExercises,
                completedExerciseIds = todayCompletedIds,
                onStartWorkout = onStartWorkout
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 3: ACHIEVEMENTS SECTION (Bottom Section)
            AchievementsSection(
                personalRecords = personalRecords
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StreakTrackerSection(
    streak: Int,
    todayDate: LocalDate,
    completedDates: Set<String>,
    restDays: Set<DayOfWeek>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak Fire",
                        tint = AccentPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$streak DAY STREAK",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Weekly Schedule",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontal streak/calendar strip showing current week (Mon - Sun)
            val startOfWeek = todayDate.minusDays((todayDate.dayOfWeek.value - 1).toLong())
            val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekDays.forEach { date ->
                    val isToday = date == todayDate
                    val isCompleted = completedDates.contains(date.toString())
                    val isRest = restDays.contains(date.dayOfWeek)
                    val isPast = date.isBefore(todayDate)

                    StreakDayItem(
                        dayLetter = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).take(1),
                        dayOfMonth = date.dayOfMonth,
                        isToday = isToday,
                        isCompleted = isCompleted,
                        isRest = isRest,
                        isPast = isPast
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakDayItem(
    dayLetter: String,
    dayOfMonth: Int,
    isToday: Boolean,
    isCompleted: Boolean,
    isRest: Boolean,
    isPast: Boolean
) {
    val bgColor = when {
        isCompleted -> AccentSuccess
        isToday -> SurfaceVariantDark
        else -> Color.Transparent
    }

    val borderColor = when {
        isToday -> AccentPrimary
        isCompleted -> AccentSuccess
        else -> SurfaceBorder
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(42.dp)
    ) {
        Text(
            text = dayLetter,
            color = if (isToday) AccentPrimary else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(if (isToday) 2.dp else 1.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCompleted -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                isRest -> {
                    Text(
                        text = "REST",
                        color = TextMuted,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                else -> {
                    Text(
                        text = dayOfMonth.toString(),
                        color = if (isToday) TextPrimary else if (isPast) TextMuted else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayWorkoutSection(
    dayOfWeek: DayOfWeek,
    isRestDay: Boolean,
    muscles: List<String>,
    activeMode: WorkoutMode,
    exercises: List<Exercise>,
    completedExerciseIds: Set<String>,
    onStartWorkout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "TODAY'S WORKOUT",
                        color = AccentPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)}: " +
                                if (isRestDay) "Rest & Recovery" else muscles.joinToString(" & "),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .background(SurfaceVariantDark, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = activeMode.displayName.uppercase(),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isRestDay) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Weekend,
                        contentDescription = "Rest Day",
                        tint = TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Scheduled Rest Day",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Active recovery, stretching and proper hydration.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Exercise List
                Text(
                    text = "${exercises.size} Exercises Assigned (${activeMode.displayName} Mode)",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                exercises.forEachIndexed { index, exercise ->
                    val isDone = completedExerciseIds.contains(exercise.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(SurfaceVariantDark, RoundedCornerShape(6.dp))
                            .border(1.dp, if (isDone) AccentSuccess else SurfaceBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${index + 1}.",
                                color = TextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
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
                        }

                        if (isDone) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(AccentSuccess),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Prominent "Start Workout" Button
                Button(
                    onClick = onStartWorkout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Workout",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementsSection(
    personalRecords: List<PersonalRecord>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "PR Trophy",
                    tint = AccentPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PERSONAL RECORDS (PRs)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (personalRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "0 PRs Logged. Max weights lifted during workouts will automatically appear here.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            } else {
                personalRecords.forEach { pr ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(SurfaceVariantDark, RoundedCornerShape(6.dp))
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = pr.exerciseName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Recorded: ${pr.dateString}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "%.1f kg".format(pr.maxWeightKg),
                            color = AccentPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

