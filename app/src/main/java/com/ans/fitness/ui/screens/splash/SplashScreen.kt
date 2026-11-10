package com.ans.fitness.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ans.fitness.R
import com.ans.fitness.ui.theme.BgDark
import com.ans.fitness.ui.theme.SurfaceBorder
import com.ans.fitness.ui.theme.TextPrimary
import com.ans.fitness.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isOnboarded: Boolean,
    onNavigateNext: (Boolean) -> Unit
) {
    // Hold for exactly 5000ms (5 seconds)
    LaunchedEffect(Unit) {
        delay(5000L)
        onNavigateNext(isOnboarded)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Completely static logo - ZERO animation
            Image(
                painter = painterResource(id = R.drawable.ans_logo),
                contentDescription = "Ans Logo",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, SurfaceBorder, RoundedCornerShape(24.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "अंस",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ANS • PURE FITNESS",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp
            )
        }
    }
}

