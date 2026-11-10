package com.ans.fitness.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ans.fitness.ui.theme.*

@Composable
fun OnboardingScreen(
    onComplete: (weight: Float, height: Float, reason: String) -> Unit
) {
    var weightText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var selectedReason by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val reasons = listOf(
        "To Lose Weight",
        "To Gain Muscle",
        "For Athlete / Cycling / Sports Performance"
    )

    // Real-time BMI calculation preview if inputs are valid
    val weightVal = weightText.toFloatOrNull() ?: 0f
    val heightVal = heightText.toFloatOrNull() ?: 0f
    val heightM = heightVal / 100f
    val calculatedBmi = if (weightVal > 0f && heightM > 0f) {
        weightVal / (heightM * heightM)
    } else null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to Ans",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Set up your profile to calibrate your workouts and metrics.",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 1. Weight Input
            Text(
                text = "1. Current Weight (kg)",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = weightText,
                onValueChange = {
                    weightText = it
                    errorMessage = null
                },
                placeholder = { Text("e.g. 70.0", color = TextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Height Input
            Text(
                text = "2. Height (cm)",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = heightText,
                onValueChange = {
                    heightText = it
                    errorMessage = null
                },
                placeholder = { Text("e.g. 175", color = TextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // BMI Preview Chip
            if (calculatedBmi != null && calculatedBmi > 10f && calculatedBmi < 60f) {
                Spacer(modifier = Modifier.height(12.dp))
                val (category, catColor) = when {
                    calculatedBmi < 18.5f -> "Underweight" to BmiUnderweight
                    calculatedBmi <= 24.9f -> "Healthy (Normal)" to BmiNormal
                    calculatedBmi <= 29.9f -> "Overweight" to BmiOverweight
                    else -> "Obese" to BmiObese
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceDark, RoundedCornerShape(8.dp))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Calculated Initial BMI:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "%.1f (%s)".format(calculatedBmi, category),
                        color = catColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Reason for using app
            Text(
                text = "3. Reason for using this app",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            reasons.forEach { reason ->
                val isSelected = selectedReason == reason
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SurfaceVariantDark else SurfaceDark)
                        .border(
                            1.dp,
                            if (isSelected) AccentPrimary else SurfaceBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            selectedReason = reason
                            errorMessage = null
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            selectedReason = reason
                            errorMessage = null
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AccentPrimary,
                            unselectedColor = TextMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = reason,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage!!,
                    color = AccentPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val w = weightText.toFloatOrNull()
                    val h = heightText.toFloatOrNull()
                    val r = selectedReason

                    when {
                        w == null || w <= 20f || w > 350f -> {
                            errorMessage = "Please enter a valid weight in kg (e.g. 70.0)"
                        }
                        h == null || h <= 50f || h > 260f -> {
                            errorMessage = "Please enter a valid height in cm (e.g. 175)"
                        }
                        r == null -> {
                            errorMessage = "Please select your primary reason for using this app"
                        }
                        else -> {
                            onComplete(w, h, r)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Complete Profile & Start",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

