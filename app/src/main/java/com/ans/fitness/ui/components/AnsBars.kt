package com.ans.fitness.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ans.fitness.R
import com.ans.fitness.data.model.WorkoutMode
import com.ans.fitness.ui.theme.*

@Composable
fun AnsTopBar(
    activeMode: WorkoutMode,
    onModeToggle: (WorkoutMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Static "Ans" Logo
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ans_logo),
                contentDescription = "Ans Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Ans",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "अंस • Fitness",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Right: Mode Switch Toggle (Home Mode vs. Gym Mode)
        Row(
            modifier = Modifier
                .background(SurfaceVariantDark, RoundedCornerShape(20.dp))
                .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModePill(
                title = "Home",
                isSelected = activeMode == WorkoutMode.HOME,
                onClick = { onModeToggle(WorkoutMode.HOME) }
            )
            ModePill(
                title = "Gym",
                isSelected = activeMode == WorkoutMode.GYM,
                onClick = { onModeToggle(WorkoutMode.GYM) }
            )
        }
    }
}

@Composable
private fun ModePill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) AccentPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

sealed class BottomTab(val route: String, val title: String, val icon: ImageVector) {
    data object Home : BottomTab("home", "Home", Icons.Default.FitnessCenter)
    data object Track : BottomTab("track", "Track", Icons.AutoMirrored.Filled.ShowChart)
    data object Profile : BottomTab("profile", "Profile", Icons.Default.Person)
}

@Composable
fun AnsBottomBar(
    currentRoute: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(BottomTab.Home, BottomTab.Track, BottomTab.Profile)

    NavigationBar(
        modifier = modifier.border(width = 1.dp, color = SurfaceBorder, shape = RoundedCornerShape(0.dp)),
        containerColor = SurfaceDark,
        tonalElevation = 0.dp
    ) {
        tabs.forEach { tab ->
            val isSelected = currentRoute == tab.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab.route) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentPrimary,
                    selectedTextColor = AccentPrimary,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = SurfaceVariantDark
                )
            )
        }
    }
}
