package com.ans.fitness.data.model

import java.time.DayOfWeek

data class UserProfile(
    val weightKg: Float,
    val heightCm: Float,
    val reason: String,
    val bmi: Float,
    val name: String = "Athlete",
    val photoUri: String? = null,
    val restDays: Set<DayOfWeek> = setOf(DayOfWeek.SUNDAY),
    val activeMode: WorkoutMode = WorkoutMode.GYM
)

data class WorkoutSet(
    val setNumber: Int,
    val targetReps: Int = 10,
    val targetDurationSeconds: Int = 45,
    val weightKg: Float = 0f,
    val isCompleted: Boolean = false,
    val isTimeBased: Boolean = false
)

data class WeightEntry(
    val id: Long = 0,
    val dateMillis: Long,
    val dateString: String, // YYYY-MM-DD
    val weightKg: Float,
    val bmi: Float
)

data class WaterEntry(
    val id: Long = 0,
    val dateMillis: Long,
    val dateString: String, // YYYY-MM-DD
    val glasses: Int,
    val ml: Int
)

data class PersonalRecord(
    val exerciseId: String,
    val exerciseName: String,
    val maxWeightKg: Float,
    val dateString: String
)

data class DayRoutine(
    val dayOfWeek: DayOfWeek,
    val muscleGroups: List<MuscleGroup>,
    val exerciseIds: List<String>
)

data class CompletedDay(
    val dateString: String,
    val completedExercisesCount: Int,
    val totalExercisesCount: Int,
    val completionPercentage: Float,
    val isStreakCounted: Boolean
)

