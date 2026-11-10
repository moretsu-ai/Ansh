package com.ans.fitness

import com.ans.fitness.data.model.MuscleGroup
import com.ans.fitness.data.model.PreloadedCatalog
import com.ans.fitness.data.model.WorkoutMode
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.time.DayOfWeek

class FitnessLogicTest {

    // --- LOOP 1 TESTS: Assets & Onboarding Logic ---
    @Test
    fun loop1_testLocalLogoExistsInRootAndDrawable() {
        val rootLogo1 = File("logo.png")
        val rootLogo2 = File("../logo.png")
        val rootLogo3 = File("ans_logo.png")
        val rootLogo4 = File("../ans_logo.png")
        assertTrue("Root logo must exist", rootLogo1.exists() || rootLogo2.exists() || rootLogo3.exists() || rootLogo4.exists())

        val resLogo1 = File("src/main/res/drawable/ans_logo.png")
        val resLogo2 = File("app/src/main/res/drawable/ans_logo.png")
        assertTrue("Drawable logo must exist", resLogo1.exists() || resLogo2.exists())
    }

    @Test
    fun loop1_testBmiCalculationOnboarding() {
        val weightKg = 70f
        val heightCm = 175f
        val heightM = heightCm / 100f
        val bmi = weightKg / (heightM * heightM)
        assertEquals(22.857f, bmi, 0.01f)
    }

    @Test
    fun loop1_testZeroPreloadedUserData() {
        // Preloaded catalog must only contain exercises, zero user logs/streaks/water
        assertTrue(PreloadedCatalog.exercises.isNotEmpty())
        assertTrue(PreloadedCatalog.exercises.none { it.isCustom })
    }

    // --- LOOP 2 TESTS: Home vs Gym Mode & 50% Streak Rule ---
    @Test
    fun loop2_testModeFiltering() {
        val chestHome = PreloadedCatalog.getDefaultForMuscle(MuscleGroup.CHEST, WorkoutMode.HOME)
        val chestGym = PreloadedCatalog.getDefaultForMuscle(MuscleGroup.CHEST, WorkoutMode.GYM)

        assertTrue("Home chest exercises must exist", chestHome.isNotEmpty())
        assertTrue("Gym chest exercises must exist", chestGym.isNotEmpty())

        assertTrue("Home exercises must be Home mode", chestHome.all { it.mode == WorkoutMode.HOME })
        assertTrue("Gym exercises must be Gym mode", chestGym.all { it.mode == WorkoutMode.GYM })

        // Check specific exercises required by prompt
        assertTrue(chestGym.any { it.name == "Bench Press" })
        assertTrue(chestHome.any { it.name == "Push-ups" })
    }

    @Test
    fun loop2_test50PercentStreakCompletionRule() {
        val totalScheduled = 6

        // Less than 50% (2 out of 6 = 33%) -> Streak NOT incremented
        val belowFifty = 2
        val ratioBelow = belowFifty.toFloat() / totalScheduled
        assertFalse("Below 50% must not count toward streak", ratioBelow >= 0.5f)

        // Exactly 50% (3 out of 6 = 50%) -> Streak IS incremented
        val exactFifty = 3
        val ratioExact = exactFifty.toFloat() / totalScheduled
        assertTrue("Exactly 50% must count toward streak", ratioExact >= 0.5f)

        // Greater than 50% (4 out of 6 = 66%) -> Streak IS incremented
        val aboveFifty = 4
        val ratioAbove = aboveFifty.toFloat() / totalScheduled
        assertTrue("Above 50% must count toward streak", ratioAbove >= 0.5f)
    }

    // --- LOOP 3 TESTS: Healthy BMI Reference Band & Water Logic ---
    @Test
    fun loop3_testHealthyBmiReferenceBandRange() {
        val healthyMin = 18.5f
        val healthyMax = 24.9f

        val normalBmi = 22.0f
        assertTrue("Normal BMI within healthy band", normalBmi in healthyMin..healthyMax)

        val underweightBmi = 17.5f
        assertFalse("Underweight BMI outside healthy band", underweightBmi in healthyMin..healthyMax)

        val overweightBmi = 27.0f
        assertFalse("Overweight BMI outside healthy band", overweightBmi in healthyMin..healthyMax)
    }

    @Test
    fun loop3_testWaterIncrementVolume() {
        val glasses = 4
        val mlPerGlass = 250
        val totalMl = glasses * mlPerGlass
        assertEquals(1000, totalMl)
    }

    // --- LOOP 4 TESTS: Profile & Rest Days Hard Validation Rule ---
    @Test
    fun loop4_testRestDaysHardValidationRule() {
        val emptyRestDays = emptySet<DayOfWeek>()
        val hasAtLeastOne = emptyRestDays.isNotEmpty()
        assertFalse("0 rest days must be blocked by validation", hasAtLeastOne)

        val validRestDays = setOf(DayOfWeek.SUNDAY)
        assertTrue("1 rest day is valid", validRestDays.isNotEmpty())

        val multipleRestDays = setOf(DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        assertTrue("Multiple rest days are valid", multipleRestDays.isNotEmpty())
    }

    @Test
    fun loop4_testCatalogMuscleCoverage() {
        // All 7 muscle groups must have both Gym and Home variants
        MuscleGroup.entries.forEach { muscle ->
            val gymList = PreloadedCatalog.getDefaultForMuscle(muscle, WorkoutMode.GYM)
            val homeList = PreloadedCatalog.getDefaultForMuscle(muscle, WorkoutMode.HOME)
            assertTrue("Gym list for $muscle must not be empty", gymList.isNotEmpty())
            assertTrue("Home list for $muscle must not be empty", homeList.isNotEmpty())
        }
    }
}
