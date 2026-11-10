package com.ans.fitness.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.ans.fitness.data.repository.FitnessRepository
import com.ans.fitness.ui.components.AnsBottomBar
import com.ans.fitness.ui.screens.home.HomeScreen
import com.ans.fitness.ui.screens.onboarding.OnboardingScreen
import com.ans.fitness.ui.screens.profile.DayRoutineEditScreen
import com.ans.fitness.ui.screens.profile.ManageExercisesScreen
import com.ans.fitness.ui.screens.profile.ProfileScreen
import com.ans.fitness.ui.screens.splash.SplashScreen
import com.ans.fitness.ui.screens.track.TrackScreen
import com.ans.fitness.ui.screens.track.WaterHistoryScreen
import com.ans.fitness.ui.screens.workout.ExerciseExecutionScreen
import com.ans.fitness.ui.screens.workout.WorkoutOverviewScreen
import com.ans.fitness.ui.theme.BgDark
import java.time.DayOfWeek

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val MAIN_SHELL = "main_shell"

    // Sub screens
    const val WORKOUT_OVERVIEW = "workout_overview"
    const val EXERCISE_EXECUTION = "exercise_execution/{exerciseId}"
    const val WATER_HISTORY = "water_history"
    const val MANAGE_EXERCISES = "manage_exercises"
    const val EDIT_DAY_ROUTINE = "edit_day_routine/{dayOfWeek}"

    fun exerciseExecution(exerciseId: String) = "exercise_execution/$exerciseId"
    fun editDayRoutine(day: DayOfWeek) = "edit_day_routine/${day.name}"
}

@Composable
fun AppNavigation(repository: FitnessRepository) {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = Routes.SPLASH
    ) {
        // 1. Static 5-second Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                isOnboarded = repository.isOnboarded(),
                onNavigateNext = { isOnboarded ->
                    if (isOnboarded) {
                        rootNavController.navigate(Routes.MAIN_SHELL) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    } else {
                        rootNavController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        // 2. First-Time Onboarding
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onComplete = { weight, height, reason ->
                    repository.completeOnboarding(weight, height, reason)
                    rootNavController.navigate(Routes.MAIN_SHELL) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // 3. Main Shell with 3 Tabs Only (Home, Track, Profile)
        composable(Routes.MAIN_SHELL) {
            MainShell(
                repository = repository,
                onStartWorkout = { rootNavController.navigate(Routes.WORKOUT_OVERVIEW) },
                onNavigateWaterHistory = { rootNavController.navigate(Routes.WATER_HISTORY) },
                onNavigateManageExercises = { rootNavController.navigate(Routes.MANAGE_EXERCISES) }
            )
        }

        // Workout Overview
        composable(Routes.WORKOUT_OVERVIEW) {
            WorkoutOverviewScreen(
                repository = repository,
                onBack = { rootNavController.popBackStack() },
                onSelectExercise = { exerciseId ->
                    rootNavController.navigate(Routes.exerciseExecution(exerciseId))
                },
                onFinishWorkout = { rootNavController.popBackStack() }
            )
        }

        // Exercise Execution Screen
        composable(
            route = Routes.EXERCISE_EXECUTION,
            arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
            ExerciseExecutionScreen(
                exerciseId = exerciseId,
                repository = repository,
                onBack = { rootNavController.popBackStack() }
            )
        }

        // Water History Screen
        composable(Routes.WATER_HISTORY) {
            WaterHistoryScreen(
                repository = repository,
                onBack = { rootNavController.popBackStack() }
            )
        }

        // Manage Exercises Screen (7-Day Weekly Routine Builder)
        composable(Routes.MANAGE_EXERCISES) {
            ManageExercisesScreen(
                repository = repository,
                onBack = { rootNavController.popBackStack() },
                onEditDay = { day ->
                    rootNavController.navigate(Routes.editDayRoutine(day))
                }
            )
        }

        // Day Routine Edit Screen
        composable(
            route = Routes.EDIT_DAY_ROUTINE,
            arguments = listOf(navArgument("dayOfWeek") { type = NavType.StringType })
        ) { backStackEntry ->
            val dayStr = backStackEntry.arguments?.getString("dayOfWeek") ?: DayOfWeek.MONDAY.name
            val day = runCatching { DayOfWeek.valueOf(dayStr) }.getOrDefault(DayOfWeek.MONDAY)
            DayRoutineEditScreen(
                dayOfWeek = day,
                repository = repository,
                onBack = { rootNavController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainShell(
    repository: FitnessRepository,
    onStartWorkout: () -> Unit,
    onNavigateWaterHistory: () -> Unit,
    onNavigateManageExercises: () -> Unit
) {
    val shellNavController = rememberNavController()
    var currentTab by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            AnsBottomBar(
                currentRoute = currentTab,
                onTabSelected = { route ->
                    currentTab = route
                    shellNavController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        containerColor = BgDark
    ) { innerPadding ->
        NavHost(
            navController = shellNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    repository = repository,
                    onStartWorkout = onStartWorkout
                )
            }
            composable("track") {
                TrackScreen(
                    repository = repository,
                    onNavigateWaterHistory = onNavigateWaterHistory
                )
            }
            composable("profile") {
                ProfileScreen(
                    repository = repository,
                    onNavigateManageExercises = onNavigateManageExercises
                )
            }
        }
    }
}

