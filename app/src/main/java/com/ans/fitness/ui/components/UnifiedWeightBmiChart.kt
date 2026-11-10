package com.ans.fitness.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ans.fitness.data.model.WeightEntry
import com.ans.fitness.ui.theme.*

@Composable
fun UnifiedWeightBmiChart(
    entries: List<WeightEntry>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "WEIGHT & BMI PROGRESSION",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                // Legend
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(AccentPrimary, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Weight", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(AccentSuccess.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Healthy Band", color = AccentSuccess, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(SurfaceVariantDark, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No Weight/BMI Entries Yet",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reference healthy BMI target: 18.5 – 24.9",
                            color = AccentSuccess,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                val latest = entries.last()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Latest Weight", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            text = "%.1f kg".format(latest.weightKg),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Calculated BMI", color = TextSecondary, fontSize = 11.sp)
                        val bmiColor = if (latest.bmi in 18.5f..24.9f) AccentSuccess else AccentPrimary
                        Text(
                            text = "%.1f".format(latest.bmi),
                            color = bmiColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Line / Area Chart with Healthy BMI Reference Band (18.5 - 24.9)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val paddingBottom = 20.dp.toPx()
                        val chartHeight = h - paddingBottom

                        // Value range calculation
                        val minBmiScale = 15f
                        val maxBmiScale = 32f
                        val bmiRange = maxBmiScale - minBmiScale

                        // 1. Draw Healthy BMI Reference Band (18.5 to 24.9)
                        val healthyTopY = chartHeight - ((24.9f - minBmiScale) / bmiRange * chartHeight)
                        val healthyBottomY = chartHeight - ((18.5f - minBmiScale) / bmiRange * chartHeight)
                        val bandHeight = healthyBottomY - healthyTopY

                        // Shaded Green Healthy BMI Reference Band
                        drawRect(
                            color = AccentSuccess.copy(alpha = 0.18f),
                            topLeft = Offset(0f, healthyTopY),
                            size = Size(w, bandHeight)
                        )
                        // Upper guideline (24.9)
                        drawLine(
                            color = AccentSuccess.copy(alpha = 0.5f),
                            start = Offset(0f, healthyTopY),
                            end = Offset(w, healthyTopY),
                            strokeWidth = 1.dp.toPx()
                        )
                        // Lower guideline (18.5)
                        drawLine(
                            color = AccentSuccess.copy(alpha = 0.5f),
                            start = Offset(0f, healthyBottomY),
                            end = Offset(w, healthyBottomY),
                            strokeWidth = 1.dp.toPx()
                        )

                        // 2. Draw Weight Points and Connecting Line
                        val stepX = if (entries.size > 1) w / (entries.size - 1) else w / 2f
                        val points = mutableListOf<Offset>()

                        entries.forEachIndexed { i, entry ->
                            val x = if (entries.size == 1) w / 2f else i * stepX
                            // Map BMI to the chart's vertical scale
                            val clampedBmi = entry.bmi.coerceIn(minBmiScale, maxBmiScale)
                            val y = chartHeight - ((clampedBmi - minBmiScale) / bmiRange * chartHeight)
                            points.add(Offset(x, y))
                        }

                        // Connecting line
                        if (points.size > 1) {
                            val linePath = Path().apply {
                                moveTo(points[0].x, points[0].y)
                                for (i in 1 until points.size) {
                                    lineTo(points[i].x, points[i].y)
                                }
                            }
                            drawPath(
                                path = linePath,
                                color = AccentPrimary,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }

                        // Draw Point Circles
                        points.forEach { pt ->
                            drawCircle(
                                color = BgDark,
                                radius = 6.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = AccentPrimary,
                                radius = 4.dp.toPx(),
                                center = pt
                            )
                        }
                    }

                    // Overlay Reference Band Label
                    Text(
                        text = "Healthy BMI Range (18.5 – 24.9)",
                        color = AccentSuccess,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
