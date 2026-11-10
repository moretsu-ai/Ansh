package com.ans.fitness.data.model

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: MuscleGroup,
    val mode: WorkoutMode,
    val isTimeBased: Boolean = false,
    val defaultReps: Int = 10,
    val defaultDurationSeconds: Int = 45,
    val isCustom: Boolean = false
)

object PreloadedCatalog {
    val exercises: List<Exercise> = listOf(
        // Chest - Gym
        Exercise("gym_chest_1", "Bench Press", MuscleGroup.CHEST, WorkoutMode.GYM, isTimeBased = false, defaultReps = 10),
        Exercise("gym_chest_2", "Incline Dumbbell Press", MuscleGroup.CHEST, WorkoutMode.GYM, isTimeBased = false, defaultReps = 10),
        Exercise("gym_chest_3", "Chest Fly Machine", MuscleGroup.CHEST, WorkoutMode.GYM, isTimeBased = false, defaultReps = 12),
        // Chest - Home
        Exercise("home_chest_1", "Push-ups", MuscleGroup.CHEST, WorkoutMode.HOME, isTimeBased = false, defaultReps = 15),
        Exercise("home_chest_2", "Wide Push-ups", MuscleGroup.CHEST, WorkoutMode.HOME, isTimeBased = false, defaultReps = 12),
        Exercise("home_chest_3", "Decline Push-ups", MuscleGroup.CHEST, WorkoutMode.HOME, isTimeBased = false, defaultReps = 12),

        // Biceps - Gym
        Exercise("gym_biceps_1", "Barbell Curl", MuscleGroup.BICEPS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 10),
        Exercise("gym_biceps_2", "Hammer Curl", MuscleGroup.BICEPS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 12),
        Exercise("gym_biceps_3", "Preacher Curl Machine", MuscleGroup.BICEPS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 10),
        // Biceps - Home
        Exercise("home_biceps_1", "Chin-ups", MuscleGroup.BICEPS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 8),
        Exercise("home_biceps_2", "Resistance/Towel Curl", MuscleGroup.BICEPS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 15),
        Exercise("home_biceps_3", "Diamond Push-ups", MuscleGroup.BICEPS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 10),

        // Back - Gym
        Exercise("gym_back_1", "Lat Pulldown", MuscleGroup.BACK, WorkoutMode.GYM, isTimeBased = false, defaultReps = 10),
        Exercise("gym_back_2", "Seated Cable Row", MuscleGroup.BACK, WorkoutMode.GYM, isTimeBased = false, defaultReps = 12),
        Exercise("gym_back_3", "Deadlift", MuscleGroup.BACK, WorkoutMode.GYM, isTimeBased = false, defaultReps = 8),
        // Back - Home
        Exercise("home_back_1", "Pull-ups", MuscleGroup.BACK, WorkoutMode.HOME, isTimeBased = false, defaultReps = 8),
        Exercise("home_back_2", "Superman Hold", MuscleGroup.BACK, WorkoutMode.HOME, isTimeBased = true, defaultDurationSeconds = 45),
        Exercise("home_back_3", "Inverted Rows", MuscleGroup.BACK, WorkoutMode.HOME, isTimeBased = false, defaultReps = 10),

        // Shoulders - Gym
        Exercise("gym_shoulders_1", "Overhead Press", MuscleGroup.SHOULDERS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 8),
        Exercise("gym_shoulders_2", "Lateral Raises", MuscleGroup.SHOULDERS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 12),
        Exercise("gym_shoulders_3", "Shoulder Press Machine", MuscleGroup.SHOULDERS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 10),
        // Shoulders - Home
        Exercise("home_shoulders_1", "Pike Push-ups", MuscleGroup.SHOULDERS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 10),
        Exercise("home_shoulders_2", "Plank Shoulder Taps", MuscleGroup.SHOULDERS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 20),

        // Triceps - Gym
        Exercise("gym_triceps_1", "Tricep Rope Pushdown", MuscleGroup.TRICEPS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 12),
        Exercise("gym_triceps_2", "Skull Crushers", MuscleGroup.TRICEPS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 10),
        // Triceps - Home
        Exercise("home_triceps_1", "Bench/Chair Dips", MuscleGroup.TRICEPS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 12),
        Exercise("home_triceps_2", "Close-Grip Push-ups", MuscleGroup.TRICEPS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 12),

        // Legs - Gym
        Exercise("gym_legs_1", "Barbell Squat", MuscleGroup.LEGS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 10),
        Exercise("gym_legs_2", "Leg Press", MuscleGroup.LEGS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 10),
        Exercise("gym_legs_3", "Leg Extension", MuscleGroup.LEGS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 12),
        Exercise("gym_legs_4", "Hamstring Curl", MuscleGroup.LEGS, WorkoutMode.GYM, isTimeBased = false, defaultReps = 12),
        // Legs - Home
        Exercise("home_legs_1", "Bodyweight Squats", MuscleGroup.LEGS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 20),
        Exercise("home_legs_2", "Bulgarian Split Squats", MuscleGroup.LEGS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 12),
        Exercise("home_legs_3", "Lunges", MuscleGroup.LEGS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 16),
        Exercise("home_legs_4", "Calf Raises", MuscleGroup.LEGS, WorkoutMode.HOME, isTimeBased = false, defaultReps = 25),

        // Core - Gym
        Exercise("gym_core_1", "Cable Crunch", MuscleGroup.CORE, WorkoutMode.GYM, isTimeBased = false, defaultReps = 15),
        // Core - Home
        Exercise("home_core_1", "Plank", MuscleGroup.CORE, WorkoutMode.HOME, isTimeBased = true, defaultDurationSeconds = 60),
        Exercise("home_core_2", "Crunches", MuscleGroup.CORE, WorkoutMode.HOME, isTimeBased = false, defaultReps = 20),
        Exercise("home_core_3", "Leg Raises", MuscleGroup.CORE, WorkoutMode.HOME, isTimeBased = false, defaultReps = 15)
    )

    fun getDefaultForMuscle(muscle: MuscleGroup, mode: WorkoutMode): List<Exercise> {
        return exercises.filter { it.muscleGroup == muscle && it.mode == mode }
    }
}

