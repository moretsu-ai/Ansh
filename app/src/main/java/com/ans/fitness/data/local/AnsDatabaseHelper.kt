package com.ans.fitness.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ans.fitness.data.model.*
import java.time.DayOfWeek
import java.time.LocalDate

class AnsDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "ans_fitness.db"
        const val DATABASE_VERSION = 1

        // Tables
        const val TABLE_CUSTOM_EXERCISES = "custom_exercises"
        const val TABLE_DAY_ROUTINES = "day_routines"
        const val TABLE_WEIGHTS = "weights"
        const val TABLE_WATER_LOGS = "water_logs"
        const val TABLE_PERSONAL_RECORDS = "personal_records"
        const val TABLE_COMPLETED_DAYS = "completed_days"
        const val TABLE_WORKOUT_LOGS = "workout_logs"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_CUSTOM_EXERCISES (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                muscle_group TEXT NOT NULL,
                mode TEXT NOT NULL,
                is_time_based INTEGER NOT NULL DEFAULT 0,
                default_reps INTEGER NOT NULL DEFAULT 10,
                default_duration INTEGER NOT NULL DEFAULT 45
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_DAY_ROUTINES (
                day_of_week TEXT PRIMARY KEY,
                muscle_groups TEXT NOT NULL,
                exercise_ids TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_WEIGHTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date_millis INTEGER NOT NULL,
                date_str TEXT NOT NULL,
                weight_kg REAL NOT NULL,
                bmi REAL NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_WATER_LOGS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date_millis INTEGER NOT NULL,
                date_str TEXT NOT NULL UNIQUE,
                glasses INTEGER NOT NULL DEFAULT 0,
                ml INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_PERSONAL_RECORDS (
                exercise_id TEXT PRIMARY KEY,
                exercise_name TEXT NOT NULL,
                max_weight_kg REAL NOT NULL,
                date_str TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_COMPLETED_DAYS (
                date_str TEXT PRIMARY KEY,
                completed_count INTEGER NOT NULL,
                total_count INTEGER NOT NULL,
                ratio REAL NOT NULL,
                is_streak INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_WORKOUT_LOGS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date_str TEXT NOT NULL,
                exercise_id TEXT NOT NULL,
                set_number INTEGER NOT NULL,
                reps INTEGER NOT NULL,
                weight_kg REAL NOT NULL,
                is_completed INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Seed default weekly routines (repeats every week/month until modified)
        seedDefaultRoutines(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOM_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DAY_ROUTINES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WEIGHTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WATER_LOGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PERSONAL_RECORDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMPLETED_DAYS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_LOGS")
        onCreate(db)
    }

    private fun seedDefaultRoutines(db: SQLiteDatabase) {
        val defaultSchedule = listOf(
            DayRoutine(DayOfWeek.MONDAY, listOf(MuscleGroup.CHEST, MuscleGroup.BICEPS), emptyList()),
            DayRoutine(DayOfWeek.TUESDAY, listOf(MuscleGroup.BACK, MuscleGroup.TRICEPS), emptyList()),
            DayRoutine(DayOfWeek.WEDNESDAY, listOf(MuscleGroup.SHOULDERS, MuscleGroup.CORE), emptyList()),
            DayRoutine(DayOfWeek.THURSDAY, listOf(MuscleGroup.LEGS), emptyList()),
            DayRoutine(DayOfWeek.FRIDAY, listOf(MuscleGroup.CHEST, MuscleGroup.BICEPS), emptyList()),
            DayRoutine(DayOfWeek.SATURDAY, listOf(MuscleGroup.BACK, MuscleGroup.TRICEPS), emptyList()),
            DayRoutine(DayOfWeek.SUNDAY, listOf(MuscleGroup.LEGS), emptyList())
        )

        defaultSchedule.forEach { routine ->
            val values = ContentValues().apply {
                put("day_of_week", routine.dayOfWeek.name)
                put("muscle_groups", routine.muscleGroups.joinToString(",") { it.name })
                put("exercise_ids", "")
            }
            db.insertWithOnConflict(TABLE_DAY_ROUTINES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    // --- Custom Exercises ---
    fun insertCustomExercise(exercise: Exercise) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", exercise.id)
            put("name", exercise.name)
            put("muscle_group", exercise.muscleGroup.name)
            put("mode", exercise.mode.name)
            put("is_time_based", if (exercise.isTimeBased) 1 else 0)
            put("default_reps", exercise.defaultReps)
            put("default_duration", exercise.defaultDurationSeconds)
        }
        db.insertWithOnConflict(TABLE_CUSTOM_EXERCISES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllCustomExercises(): List<Exercise> {
        val list = mutableListOf<Exercise>()
        val db = readableDatabase
        val cursor = db.query(TABLE_CUSTOM_EXERCISES, null, null, null, null, null, null)
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow("id"))
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val muscleStr = it.getString(it.getColumnIndexOrThrow("muscle_group"))
                val modeStr = it.getString(it.getColumnIndexOrThrow("mode"))
                val isTime = it.getInt(it.getColumnIndexOrThrow("is_time_based")) == 1
                val reps = it.getInt(it.getColumnIndexOrThrow("default_reps"))
                val dur = it.getInt(it.getColumnIndexOrThrow("default_duration"))

                list.add(
                    Exercise(
                        id = id,
                        name = name,
                        muscleGroup = MuscleGroup.fromString(muscleStr),
                        mode = WorkoutMode.fromString(modeStr),
                        isTimeBased = isTime,
                        defaultReps = reps,
                        defaultDurationSeconds = dur,
                        isCustom = true
                    )
                )
            }
        }
        return list
    }

    // --- Day Routines ---
    fun getRoutineForDay(dayOfWeek: DayOfWeek): DayRoutine {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_DAY_ROUTINES,
            null,
            "day_of_week = ?",
            arrayOf(dayOfWeek.name),
            null, null, null
        )
        cursor.use {
            if (it.moveToNext()) {
                val muscleStr = it.getString(it.getColumnIndexOrThrow("muscle_groups"))
                val exerciseIdsStr = it.getString(it.getColumnIndexOrThrow("exercise_ids"))
                val muscles = muscleStr.split(",")
                    .mapNotNull { m -> runCatching { MuscleGroup.valueOf(m.trim()) }.getOrNull() }
                    .ifEmpty { listOf(MuscleGroup.CHEST) }
                val exerciseIds = exerciseIdsStr.split(",")
                    .map { id -> id.trim() }
                    .filter { id -> id.isNotEmpty() }
                return DayRoutine(dayOfWeek, muscles, exerciseIds)
            }
        }
        return DayRoutine(dayOfWeek, listOf(MuscleGroup.CHEST, MuscleGroup.BICEPS), emptyList())
    }

    fun saveRoutineForDay(routine: DayRoutine) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("day_of_week", routine.dayOfWeek.name)
            put("muscle_groups", routine.muscleGroups.joinToString(",") { it.name })
            put("exercise_ids", routine.exerciseIds.joinToString(","))
        }
        db.insertWithOnConflict(TABLE_DAY_ROUTINES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // --- Weights & BMI ---
    fun insertWeight(weightKg: Float, bmi: Float, dateMillis: Long, dateString: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("date_millis", dateMillis)
            put("date_str", dateString)
            put("weight_kg", weightKg)
            put("bmi", bmi)
        }
        return db.insert(TABLE_WEIGHTS, null, values)
    }

    fun getAllWeightEntries(): List<WeightEntry> {
        val list = mutableListOf<WeightEntry>()
        val db = readableDatabase
        val cursor = db.query(TABLE_WEIGHTS, null, null, null, null, null, "date_millis ASC")
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val millis = it.getLong(it.getColumnIndexOrThrow("date_millis"))
                val dateStr = it.getString(it.getColumnIndexOrThrow("date_str"))
                val weight = it.getFloat(it.getColumnIndexOrThrow("weight_kg"))
                val bmi = it.getFloat(it.getColumnIndexOrThrow("bmi"))
                list.add(WeightEntry(id, millis, dateStr, weight, bmi))
            }
        }
        return list
    }

    // --- Water Logs ---
    fun logWater(dateString: String, dateMillis: Long, deltaGlasses: Int, mlPerGlass: Int = 250): WaterEntry {
        val db = writableDatabase
        var currentGlasses = 0
        var currentMl = 0
        val cursor = db.query(
            TABLE_WATER_LOGS,
            null,
            "date_str = ?",
            arrayOf(dateString),
            null, null, null
        )
        cursor.use {
            if (it.moveToNext()) {
                currentGlasses = it.getInt(it.getColumnIndexOrThrow("glasses"))
                currentMl = it.getInt(it.getColumnIndexOrThrow("ml"))
            }
        }
        val newGlasses = (currentGlasses + deltaGlasses).coerceAtLeast(0)
        val newMl = (currentMl + (deltaGlasses * mlPerGlass)).coerceAtLeast(0)

        val values = ContentValues().apply {
            put("date_str", dateString)
            put("date_millis", dateMillis)
            put("glasses", newGlasses)
            put("ml", newMl)
        }
        db.insertWithOnConflict(TABLE_WATER_LOGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return WaterEntry(dateMillis = dateMillis, dateString = dateString, glasses = newGlasses, ml = newMl)
    }

    fun getWaterForDate(dateString: String): WaterEntry {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_WATER_LOGS,
            null,
            "date_str = ?",
            arrayOf(dateString),
            null, null, null
        )
        cursor.use {
            if (it.moveToNext()) {
                val millis = it.getLong(it.getColumnIndexOrThrow("date_millis"))
                val glasses = it.getInt(it.getColumnIndexOrThrow("glasses"))
                val ml = it.getInt(it.getColumnIndexOrThrow("ml"))
                return WaterEntry(dateMillis = millis, dateString = dateString, glasses = glasses, ml = ml)
            }
        }
        return WaterEntry(dateMillis = System.currentTimeMillis(), dateString = dateString, glasses = 0, ml = 0)
    }

    fun getAllWaterEntries(): List<WaterEntry> {
        val list = mutableListOf<WaterEntry>()
        val db = readableDatabase
        val cursor = db.query(TABLE_WATER_LOGS, null, null, null, null, null, "date_millis DESC")
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val millis = it.getLong(it.getColumnIndexOrThrow("date_millis"))
                val dateStr = it.getString(it.getColumnIndexOrThrow("date_str"))
                val glasses = it.getInt(it.getColumnIndexOrThrow("glasses"))
                val ml = it.getInt(it.getColumnIndexOrThrow("ml"))
                list.add(WaterEntry(id, millis, dateStr, glasses, ml))
            }
        }
        return list
    }

    // --- Personal Records (PRs) ---
    fun recordWeightLifted(exerciseId: String, exerciseName: String, weightKg: Float, dateString: String) {
        if (weightKg <= 0f) return
        val db = writableDatabase
        var currentMax = 0f
        val cursor = db.query(
            TABLE_PERSONAL_RECORDS,
            arrayOf("max_weight_kg"),
            "exercise_id = ?",
            arrayOf(exerciseId),
            null, null, null
        )
        cursor.use {
            if (it.moveToNext()) {
                currentMax = it.getFloat(it.getColumnIndexOrThrow("max_weight_kg"))
            }
        }
        if (weightKg > currentMax) {
            val values = ContentValues().apply {
                put("exercise_id", exerciseId)
                put("exercise_name", exerciseName)
                put("max_weight_kg", weightKg)
                put("date_str", dateString)
            }
            db.insertWithOnConflict(TABLE_PERSONAL_RECORDS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    fun getAllPersonalRecords(): List<PersonalRecord> {
        val list = mutableListOf<PersonalRecord>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PERSONAL_RECORDS, null, null, null, null, null, "max_weight_kg DESC")
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow("exercise_id"))
                val name = it.getString(it.getColumnIndexOrThrow("exercise_name"))
                val max = it.getFloat(it.getColumnIndexOrThrow("max_weight_kg"))
                val dateStr = it.getString(it.getColumnIndexOrThrow("date_str"))
                list.add(PersonalRecord(id, name, max, dateStr))
            }
        }
        return list
    }

    // --- Completed Days & Streaks ---
    fun recordDayProgress(
        dateString: String,
        completedCount: Int,
        totalCount: Int
    ): CompletedDay {
        val db = writableDatabase
        val ratio = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
        val isStreak = ratio >= 0.5f // 50% streak completion rule

        val values = ContentValues().apply {
            put("date_str", dateString)
            put("completed_count", completedCount)
            put("total_count", totalCount)
            put("ratio", ratio)
            put("is_streak", if (isStreak) 1 else 0)
        }
        db.insertWithOnConflict(TABLE_COMPLETED_DAYS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return CompletedDay(dateString, completedCount, totalCount, ratio, isStreak)
    }

    fun getDayProgress(dateString: String): CompletedDay? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_COMPLETED_DAYS,
            null,
            "date_str = ?",
            arrayOf(dateString),
            null, null, null
        )
        cursor.use {
            if (it.moveToNext()) {
                val completed = it.getInt(it.getColumnIndexOrThrow("completed_count"))
                val total = it.getInt(it.getColumnIndexOrThrow("total_count"))
                val ratio = it.getFloat(it.getColumnIndexOrThrow("ratio"))
                val isStreak = it.getInt(it.getColumnIndexOrThrow("is_streak")) == 1
                return CompletedDay(dateString, completed, total, ratio, isStreak)
            }
        }
        return null
    }

    fun getAllCompletedDates(): Set<String> {
        val set = mutableSetOf<String>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_COMPLETED_DAYS,
            arrayOf("date_str"),
            "is_streak = 1",
            null, null, null, null
        )
        cursor.use {
            while (it.moveToNext()) {
                set.add(it.getString(it.getColumnIndexOrThrow("date_str")))
            }
        }
        return set
    }

    fun calculateCurrentStreak(): Int {
        val completedDates = getAllCompletedDates()
        if (completedDates.isEmpty()) return 0

        var streak = 0
        var checkDate = LocalDate.now()

        // If today is completed, count it; if not yet completed today, check if yesterday was completed
        if (!completedDates.contains(checkDate.toString())) {
            checkDate = checkDate.minusDays(1)
        }

        while (completedDates.contains(checkDate.toString())) {
            streak++
            checkDate = checkDate.minusDays(1)
        }

        return streak
    }

    // --- Workout Logs (Per-Set tracking) ---
    fun logSet(dateString: String, exerciseId: String, setNumber: Int, reps: Int, weightKg: Float, isCompleted: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("date_str", dateString)
            put("exercise_id", exerciseId)
            put("set_number", setNumber)
            put("reps", reps)
            put("weight_kg", weightKg)
            put("is_completed", if (isCompleted) 1 else 0)
        }
        db.insert(TABLE_WORKOUT_LOGS, null, values)
    }

    fun getCompletedExerciseIdsForDate(dateString: String): Set<String> {
        val set = mutableSetOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT DISTINCT exercise_id FROM $TABLE_WORKOUT_LOGS WHERE date_str = ? AND is_completed = 1",
            arrayOf(dateString)
        )
        cursor.use {
            while (it.moveToNext()) {
                set.add(it.getString(0))
            }
        }
        return set
    }
}

