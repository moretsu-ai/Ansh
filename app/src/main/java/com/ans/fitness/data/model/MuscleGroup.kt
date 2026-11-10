package com.ans.fitness.data.model

enum class MuscleGroup(val displayName: String) {
    CHEST("Chest"),
    BICEPS("Biceps"),
    BACK("Back"),
    SHOULDERS("Shoulders"),
    TRICEPS("Triceps"),
    LEGS("Legs"),
    CORE("Core");

    companion object {
        fun fromString(value: String): MuscleGroup {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) } ?: CHEST
        }
    }
}

