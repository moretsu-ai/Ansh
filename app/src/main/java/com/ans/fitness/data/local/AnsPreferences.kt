package com.ans.fitness.data.local

import android.content.Context
import android.content.SharedPreferences
import com.ans.fitness.data.model.UserProfile
import com.ans.fitness.data.model.WorkoutMode
import java.time.DayOfWeek

class AnsPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ans_fitness_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_ONBOARDED = "is_onboarded"
        private const val KEY_WEIGHT = "user_weight"
        private const val KEY_HEIGHT = "user_height"
        private const val KEY_REASON = "user_reason"
        private const val KEY_BMI = "user_bmi"
        private const val KEY_NAME = "user_name"
        private const val KEY_PHOTO_URI = "user_photo_uri"
        private const val KEY_REST_DAYS = "rest_days"
        private const val KEY_ACTIVE_MODE = "active_mode"
    }

    fun isOnboarded(): Boolean = prefs.getBoolean(KEY_IS_ONBOARDED, false)

    fun saveOnboardingProfile(weightKg: Float, heightCm: Float, reason: String) {
        val heightM = heightCm / 100f
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0f
        prefs.edit()
            .putBoolean(KEY_IS_ONBOARDED, true)
            .putFloat(KEY_WEIGHT, weightKg)
            .putFloat(KEY_HEIGHT, heightCm)
            .putString(KEY_REASON, reason)
            .putFloat(KEY_BMI, bmi)
            .apply()
    }

    fun getUserProfile(): UserProfile? {
        if (!isOnboarded()) return null
        val weight = prefs.getFloat(KEY_WEIGHT, 0f)
        val height = prefs.getFloat(KEY_HEIGHT, 0f)
        val reason = prefs.getString(KEY_REASON, "") ?: ""
        val bmi = prefs.getFloat(KEY_BMI, 0f)
        val name = prefs.getString(KEY_NAME, "Athlete") ?: "Athlete"
        val photo = prefs.getString(KEY_PHOTO_URI, null)
        val restDaysStr = prefs.getString(KEY_REST_DAYS, "SUNDAY") ?: "SUNDAY"
        val restDays = restDaysStr.split(",")
            .mapNotNull { nameStr -> runCatching { DayOfWeek.valueOf(nameStr.trim()) }.getOrNull() }
            .toSet().ifEmpty { setOf(DayOfWeek.SUNDAY) }
        val modeStr = prefs.getString(KEY_ACTIVE_MODE, WorkoutMode.GYM.name) ?: WorkoutMode.GYM.name
        val mode = WorkoutMode.fromString(modeStr)

        return UserProfile(
            weightKg = weight,
            heightCm = height,
            reason = reason,
            bmi = bmi,
            name = name,
            photoUri = photo,
            restDays = restDays,
            activeMode = mode
        )
    }

    fun updateName(name: String) {
        prefs.edit().putString(KEY_NAME, name).apply()
    }

    fun updatePhotoUri(uriString: String?) {
        prefs.edit().putString(KEY_PHOTO_URI, uriString).apply()
    }

    fun updateRestDays(days: Set<DayOfWeek>): Boolean {
        if (days.isEmpty()) return false // Hard validation rule: at least 1 rest day required!
        val str = days.joinToString(",") { it.name }
        prefs.edit().putString(KEY_REST_DAYS, str).apply()
        return true
    }

    fun getRestDays(): Set<DayOfWeek> {
        val str = prefs.getString(KEY_REST_DAYS, "SUNDAY") ?: "SUNDAY"
        return str.split(",")
            .mapNotNull { runCatching { DayOfWeek.valueOf(it.trim()) }.getOrNull() }
            .toSet().ifEmpty { setOf(DayOfWeek.SUNDAY) }
    }

    fun updateActiveMode(mode: WorkoutMode) {
        prefs.edit().putString(KEY_ACTIVE_MODE, mode.name).apply()
    }

    fun getActiveMode(): WorkoutMode {
        val modeStr = prefs.getString(KEY_ACTIVE_MODE, WorkoutMode.GYM.name) ?: WorkoutMode.GYM.name
        return WorkoutMode.fromString(modeStr)
    }

    fun updateWeight(weightKg: Float, heightCm: Float? = null): Float {
        val currentHeight = heightCm ?: prefs.getFloat(KEY_HEIGHT, 175f)
        val heightM = currentHeight / 100f
        val newBmi = if (heightM > 0) weightKg / (heightM * heightM) else 0f
        prefs.edit()
            .putFloat(KEY_WEIGHT, weightKg)
            .putFloat(KEY_BMI, newBmi)
            .apply()
        return newBmi
    }
}

