package com.example.todoapp.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.model.Task

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskCard(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    // We use a constant true here because the LazyColumn handles the actual removal animation
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (task.isLate) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = onCheckedChange
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                            fontWeight = if (task.isLate) FontWeight.Bold else FontWeight.Normal
                        )
                    )

                    if (task.deadline != null) {
                        Text(
                            text = "Due: ${task.deadline}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (task.isLate) Color.Red else Color.Unspecified
                        )
                    }

                    // Late Status Label
                    if (task.isLate) {
                        Text(
                            text = "OVERDUE",
                            color = Color.Red,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Text("❌", fontSize = 12.sp) // Simple delete icon
                }
            }
        }
    }
}