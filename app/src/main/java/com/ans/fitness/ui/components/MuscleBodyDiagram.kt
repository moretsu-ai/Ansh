package com.ans.fitness.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ans.fitness.data.model.MuscleGroup
import com.ans.fitness.ui.theme.*

@Composable
fun MuscleBodyDiagram(
    activeMuscle: MuscleGroup,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(220.dp)
                .height(260.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val centerX = w / 2f

                // Draw Head & Neck
                drawCircle(
                    color = Color(0xFF2A313C),
                    radius = 16.dp.toPx(),
                    center = Offset(centerX, 28.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFF2A313C),
                    topLeft = Offset(centerX - 6.dp.toPx(), 44.dp.toPx()),
                    size = Size(12.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )

                // 1. SHOULDERS (Deltoids)
                val shoulderColor = if (activeMuscle == MuscleGroup.SHOULDERS) MuscleHighlight else MuscleDefault
                // Left shoulder
                drawRoundRect(
                    color = shoulderColor,
                    topLeft = Offset(centerX - 46.dp.toPx(), 54.dp.toPx()),
                    size = Size(20.dp.toPx(), 22.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                // Right shoulder
                drawRoundRect(
                    color = shoulderColor,
                    topLeft = Offset(centerX + 26.dp.toPx(), 54.dp.toPx()),
                    size = Size(20.dp.toPx(), 22.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )

                // 2. CHEST (Pectorals) or BACK (if back exercise)
                if (activeMuscle == MuscleGroup.BACK) {
                    // Back (Traps & Lats)
                    val backColor = MuscleHighlight
                    val backPath = Path().apply {
                        moveTo(centerX - 24.dp.toPx(), 56.dp.toPx())
                        lineTo(centerX + 24.dp.toPx(), 56.dp.toPx())
                        lineTo(centerX + 32.dp.toPx(), 88.dp.toPx())
                        lineTo(centerX + 18.dp.toPx(), 108.dp.toPx())
                        lineTo(centerX - 18.dp.toPx(), 108.dp.toPx())
                        lineTo(centerX - 32.dp.toPx(), 88.dp.toPx())
                        close()
                    }
                    drawPath(backPath, color = backColor)
                } else {
                    // Chest
                    val chestColor = if (activeMuscle == MuscleGroup.CHEST) MuscleHighlight else MuscleDefault
                    // Left Pec
                    drawRoundRect(
                        color = chestColor,
                        topLeft = Offset(centerX - 24.dp.toPx(), 58.dp.toPx()),
                        size = Size(22.dp.toPx(), 24.dp.toPx()),
                        cornerRadius = CornerRadius(6.dp.toPx())
                    )
                    // Right Pec
                    drawRoundRect(
                        color = chestColor,
                        topLeft = Offset(centerX + 2.dp.toPx(), 58.dp.toPx()),
                        size = Size(22.dp.toPx(), 24.dp.toPx()),
                        cornerRadius = CornerRadius(6.dp.toPx())
                    )
                }

                // 3. ARMS: BICEPS & TRICEPS
                val bicepsColor = if (activeMuscle == MuscleGroup.BICEPS) MuscleHighlight else MuscleDefault
                val tricepsColor = if (activeMuscle == MuscleGroup.TRICEPS) MuscleHighlight else MuscleDefault
                
                // Left Upper Arm
                drawRoundRect(
                    color = if (activeMuscle == MuscleGroup.BICEPS) bicepsColor else if (activeMuscle == MuscleGroup.TRICEPS) tricepsColor else MuscleDefault,
                    topLeft = Offset(centerX - 50.dp.toPx(), 78.dp.toPx()),
                    size = Size(16.dp.toPx(), 34.dp.toPx()),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
                // Left Forearm
                drawRoundRect(
                    color = Color(0xFF21262D),
                    topLeft = Offset(centerX - 52.dp.toPx(), 114.dp.toPx()),
                    size = Size(14.dp.toPx(), 38.dp.toPx()),
                    cornerRadius = CornerRadius(5.dp.toPx())
                )

                // Right Upper Arm
                drawRoundRect(
                    color = if (activeMuscle == MuscleGroup.BICEPS) bicepsColor else if (activeMuscle == MuscleGroup.TRICEPS) tricepsColor else MuscleDefault,
                    topLeft = Offset(centerX + 34.dp.toPx(), 78.dp.toPx()),
                    size = Size(16.dp.toPx(), 34.dp.toPx()),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
                // Right Forearm
                drawRoundRect(
                    color = Color(0xFF21262D),
                    topLeft = Offset(centerX + 38.dp.toPx(), 114.dp.toPx()),
                    size = Size(14.dp.toPx(), 38.dp.toPx()),
                    cornerRadius = CornerRadius(5.dp.toPx())
                )

                // 4. CORE / ABS
                val coreColor = if (activeMuscle == MuscleGroup.CORE) MuscleHighlight else MuscleDefault
                // Upper abs
                drawRoundRect(
                    color = coreColor,
                    topLeft = Offset(centerX - 16.dp.toPx(), 84.dp.toPx()),
                    size = Size(14.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
                drawRoundRect(
                    color = coreColor,
                    topLeft = Offset(centerX + 2.dp.toPx(), 84.dp.toPx()),
                    size = Size(14.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
                // Mid abs
                drawRoundRect(
                    color = coreColor,
                    topLeft = Offset(centerX - 16.dp.toPx(), 98.dp.toPx()),
                    size = Size(14.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
                drawRoundRect(
                    color = coreColor,
                    topLeft = Offset(centerX + 2.dp.toPx(), 98.dp.toPx()),
                    size = Size(14.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
                // Lower abs / pelvis
                drawRoundRect(
                    color = coreColor,
                    topLeft = Offset(centerX - 18.dp.toPx(), 112.dp.toPx()),
                    size = Size(36.dp.toPx(), 14.dp.toPx()),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )

                // 5. LEGS (Quads & Calves)
                val legsColor = if (activeMuscle == MuscleGroup.LEGS) MuscleHighlight else MuscleDefault
                // Left Thigh
                drawRoundRect(
                    color = legsColor,
                    topLeft = Offset(centerX - 24.dp.toPx(), 128.dp.toPx()),
                    size = Size(20.dp.toPx(), 54.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                // Left Calf
                drawRoundRect(
                    color = legsColor,
                    topLeft = Offset(centerX - 22.dp.toPx(), 184.dp.toPx()),
                    size = Size(16.dp.toPx(), 50.dp.toPx()),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )

                // Right Thigh
                drawRoundRect(
                    color = legsColor,
                    topLeft = Offset(centerX + 4.dp.toPx(), 128.dp.toPx()),
                    size = Size(20.dp.toPx(), 54.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                // Right Calf
                drawRoundRect(
                    color = legsColor,
                    topLeft = Offset(centerX + 6.dp.toPx(), 184.dp.toPx()),
                    size = Size(16.dp.toPx(), 50.dp.toPx()),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )

                // Static highlight border for active group to make it pop cleanly
                drawHighlightOutline(activeMuscle, centerX)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(MuscleHighlight, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "TARGET: ${activeMuscle.displayName.uppercase()}",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

private fun DrawScope.drawHighlightOutline(muscle: MuscleGroup, centerX: Float) {
    val highlightStroke = Stroke(width = 2.dp.toPx())
    when (muscle) {
        MuscleGroup.CHEST -> {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(centerX - 24.dp.toPx(), 58.dp.toPx()),
                size = Size(48.dp.toPx(), 24.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx()),
                style = highlightStroke
            )
        }
        MuscleGroup.BICEPS, MuscleGroup.TRICEPS -> {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(centerX - 50.dp.toPx(), 78.dp.toPx()),
                size = Size(16.dp.toPx(), 34.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx()),
                style = highlightStroke
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(centerX + 34.dp.toPx(), 78.dp.toPx()),
                size = Size(16.dp.toPx(), 34.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx()),
                style = highlightStroke
            )
        }
        MuscleGroup.SHOULDERS -> {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(centerX - 46.dp.toPx(), 54.dp.toPx()),
                size = Size(20.dp.toPx(), 22.dp.toPx()),
                cornerRadius = CornerRadius(8.dp.toPx()),
                style = highlightStroke
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(centerX + 26.dp.toPx(), 54.dp.toPx()),
                size = Size(20.dp.toPx(), 22.dp.toPx()),
                cornerRadius = CornerRadius(8.dp.toPx()),
                style = highlightStroke
            )
        }
        MuscleGroup.CORE -> {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(centerX - 18.dp.toPx(), 84.dp.toPx()),
                size = Size(36.dp.toPx(), 42.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx()),
                style = highlightStroke
            )
        }
        MuscleGroup.LEGS -> {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(centerX - 24.dp.toPx(), 128.dp.toPx()),
                size = Size(48.dp.toPx(), 106.dp.toPx()),
                cornerRadius = CornerRadius(8.dp.toPx()),
                style = highlightStroke
            )
        }
        MuscleGroup.BACK -> {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(centerX - 30.dp.toPx(), 56.dp.toPx()),
                size = Size(60.dp.toPx(), 52.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx()),
                style = highlightStroke
            )
        }
    }
}

