package com.ans.fitness.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ans.fitness.ui.theme.*
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SetRestDaysDialog(
    initialRestDays: Set<DayOfWeek>,
    onDismiss: () -> Unit,
    onSave: (Set<DayOfWeek>) -> Unit
) {
    var selectedDays by remember { mutableStateOf(initialRestDays.toMutableSet()) }
    val allDays = DayOfWeek.entries

    // HARD VALIDATION RULE: At least 1 (one or more) rest days required!
    val isValid = selectedDays.isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Configure Weekly Rest Days",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Select your weekly recovery days. Muscle recovery is essential for progress.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                allDays.forEach { day ->
                    val isChecked = selectedDays.contains(day)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isChecked) SurfaceVariantDark else Color.Transparent)
                            .clickable {
                                if (isChecked) {
                                    selectedDays.remove(day)
                                } else {
                                    selectedDays.add(day)
                                }
                                selectedDays = selectedDays.toMutableSet()
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) selectedDays.add(day) else selectedDays.remove(day)
                                selectedDays = selectedDays.toMutableSet()
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AccentPrimary,
                                uncheckedColor = TextMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                            color = if (isChecked) TextPrimary else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }

                if (!isValid) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF3B1E1E), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFFF85149), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFFF85149),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hard Rule: You MUST select at least 1 rest day per week.",
                            color = Color(0xFFF85149),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onSave(selectedDays)
                    }
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPrimary,
                    disabledContainerColor = SurfaceVariantDark
                )
            ) {
                Text(
                    text = "Save Rest Days",
                    color = if (isValid) Color.White else TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = TextSecondary)
            }
        },
        containerColor = SurfaceDark
    )
}

