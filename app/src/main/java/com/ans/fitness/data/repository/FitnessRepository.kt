package com.ans.fitness.data.repository

import android.content.Context
import com.ans.fitness.data.local.AnsDatabaseHelper
import com.ans.fitness.data.local.AnsPreferences
import com.ans.fitness.data.model.*
import java.time.DayOfWeek
import java.time.LocalDate

class FitnessRepository(context: Context) {
    private val prefs = AnsPreferences(context)
    private val db = AnsDatabaseHelper(context)

    // User Profile
    fun isOnboarded(): Boolean = prefs.isOnboarded()

    fun getUserProfile(): UserProfile? = prefs.getUserProfile()

    fun completeOnboarding(weightKg: Float, heightCm: Float, reason: String) {
        prefs.saveOnboardingProfile(weightKg, heightCm, reason)
        val heightM = heightCm / 100f
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0f
        val todayStr = LocalDate.now().toString()
        db.insertWeight(weightKg, bmi, System.currentTimeMillis(), todayStr)
    }

    fun updateName(name: String) = prefs.updateName(name)

    fun updatePhotoUri(uriString: String?) = prefs.updatePhotoUri(uriString)

    fun updateRestDays(days: Set<DayOfWeek>): Boolean = prefs.updateRestDays(days)

    fun getRestDays(): Set<DayOfWeek> = prefs.getRestDays()

    fun getActiveMode(): WorkoutMode = prefs.getActiveMode()

    fun setActiveMode(mode: WorkoutMode) = prefs.updateActiveMode(mode)

    // Exercises Catalog
    fun getAllExercises(): List<Exercise> {
        val custom = db.getAllCustomExercises()
        return PreloadedCatalog.exercises + custom
    }

    fun getExerciseById(id: String): Exercise? {
        return getAllExercises().firstOrNull { it.id == id }
    }

    fun addCustomExercise(
        name: String,
        muscleGroup: MuscleGroup,
        mode: WorkoutMode,
        isTimeBased: Boolean
    ): Exercise {
        val id = "custom_${System.currentTimeMillis()}"
        val exercise = Exercise(
            id = id,
            name = name,
            muscleGroup = muscleGroup,
            mode = mode,
            isTimeBased = isTimeBased,
            defaultReps = if (isTimeBased) 0 else 10,
            defaultDurationSeconds = if (isTimeBased) 45 else 0,
            isCustom = true
        )
        db.insertCustomExercise(exercise)
        return exercise
    }

    // Weekly Routines
    fun getRoutineForDay(dayOfWeek: DayOfWeek, mode: WorkoutMode): List<Exercise> {
        val routine = db.getRoutineForDay(dayOfWeek)
        val allExercises = getAllExercises()

        if (routine.exerciseIds.isNotEmpty()) {
            val list = routine.exerciseIds.mapNotNull { id ->
                allExercises.firstOrNull { it.id == id && it.mode == mode }
            }
            if (list.isNotEmpty()) return list
        }

        // Auto-populate default exercises for assigned muscle groups based on active mode
        val defaults = mutableListOf<Exercise>()
        routine.muscleGroups.forEach { muscle ->
            defaults.addAll(allExercises.filter { it.muscleGroup == muscle && it.mode == mode })
        }
        return defaults
    }

    fun getDayRoutineConfig(dayOfWeek: DayOfWeek): DayRoutine {
        return db.getRoutineForDay(dayOfWeek)
    }

    fun updateDayMuscles(dayOfWeek: DayOfWeek, muscles: List<MuscleGroup>, mode: WorkoutMode) {
        val allExercises = getAllExercises()
        val defaultExerciseIds = mutableListOf<String>()
        muscles.forEach { muscle ->
            val matching = allExercises.filter { it.muscleGroup == muscle && it.mode == mode }
            defaultExerciseIds.addAll(matching.map { it.id })
        }
        val routine = DayRoutine(dayOfWeek, muscles, defaultExerciseIds)
        db.saveRoutineForDay(routine)
    }

    fun removeExerciseFromDay(dayOfWeek: DayOfWeek, exerciseId: String) {
        val routine = db.getRoutineForDay(dayOfWeek)
        val currentExercises = if (routine.exerciseIds.isNotEmpty()) {
            routine.exerciseIds.toMutableList()
        } else {
            getRoutineForDay(dayOfWeek, getActiveMode()).map { it.id }.toMutableList()
        }
        currentExercises.remove(exerciseId)
        db.saveRoutineForDay(routine.copy(exerciseIds = currentExercises))
    }

    fun addExerciseToDay(dayOfWeek: DayOfWeek, exerciseId: String) {
        val routine = db.getRoutineForDay(dayOfWeek)
        val currentExercises = if (routine.exerciseIds.isNotEmpty()) {
            routine.exerciseIds.toMutableList()
        } else {
            getRoutineForDay(dayOfWeek, getActiveMode()).map { it.id }.toMutableList()
        }
        if (!currentExercises.contains(exerciseId)) {
            currentExercises.add(exerciseId)
        }
        db.saveRoutineForDay(routine.copy(exerciseIds = currentExercises))
    }

    // Today's Status
    fun isTodayRestDay(): Boolean {
        val todayDayOfWeek = LocalDate.now().dayOfWeek
        return getRestDays().contains(todayDayOfWeek)
    }

    fun getTodayMuscleGroups(): List<MuscleGroup> {
        val today = LocalDate.now().dayOfWeek
        return db.getRoutineForDay(today).muscleGroups
    }

    fun getTodayExercises(mode: WorkoutMode = getActiveMode()): List<Exercise> {
        if (isTodayRestDay()) return emptyList()
        return getRoutineForDay(LocalDate.now().dayOfWeek, mode)
    }

    // Workout Execution & 50% Streak Rule
    fun logSetCompletion(exerciseId: String, exerciseName: String, setNumber: Int, reps: Int, weightKg: Float) {
        val todayStr = LocalDate.now().toString()
        db.logSet(todayStr, exerciseId, setNumber, reps, weightKg, isCompleted = true)
        if (weightKg > 0f) {
            db.recordWeightLifted(exerciseId, exerciseName, weightKg, todayStr)
        }
    }

    fun getTodayCompletedExerciseIds(): Set<String> {
        val todayStr = LocalDate.now().toString()
        return db.getCompletedExerciseIdsForDate(todayStr)
    }

    fun evaluateStreakForToday(totalScheduled: Int): CompletedDay {
        val todayStr = LocalDate.now().toString()
        val completedCount = getTodayCompletedExerciseIds().size
        return db.recordDayProgress(todayStr, completedCount, totalScheduled)
    }

    fun getCurrentStreak(): Int = db.calculateCurrentStreak()

    fun getAllCompletedDates(): Set<String> = db.getAllCompletedDates()

    // PRs
    fun getPersonalRecords(): List<PersonalRecord> = db.getAllPersonalRecords()

    // Weight & BMI
    fun getWeightHistory(): List<WeightEntry> = db.getAllWeightEntries()

    fun logNewWeight(weightKg: Float): Float {
        val profile = getUserProfile()
        val heightCm = profile?.heightCm ?: 175f
        val newBmi = prefs.updateWeight(weightKg, heightCm)
        val todayStr = LocalDate.now().toString()
        db.insertWeight(weightKg, newBmi, System.currentTimeMillis(), todayStr)
        return newBmi
    }

    // Water
    fun getTodayWater(): WaterEntry {
        val todayStr = LocalDate.now().toString()
        return db.getWaterForDate(todayStr)
    }

    fun incrementWater(deltaGlasses: Int = 1): WaterEntry {
        val todayStr = LocalDate.now().toString()
        return db.logWater(todayStr, System.currentTimeMillis(), deltaGlasses, mlPerGlass = 250)
    }

    fun getWaterHistory(): List<WaterEntry> = db.getAllWaterEntries()
}

