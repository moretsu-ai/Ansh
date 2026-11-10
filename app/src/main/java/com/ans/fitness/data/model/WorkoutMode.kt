package com.ans.fitness.data.model

enum class WorkoutMode(val displayName: String) {
    HOME("Home"),
    GYM("Gym");

    companion object {
        fun fromString(value: String): WorkoutMode {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: GYM
        }
    }
}

