package com.strathmore.groupworkmanager.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.strathmore.groupworkmanager.data.model.TaskPriority
import com.strathmore.groupworkmanager.data.model.TaskStatus

/**
 * Filter chip group for tasks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFilterBar(
    selectedStatus: TaskStatus?,
    onStatusSelected: (TaskStatus?) -> Unit,
    selectedPriority: TaskPriority?,
    onPrioritySelected: (TaskPriority?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Status Filters
        Text(
            text = "Status",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusSelected(null) },
                label = { Text("All") },
                leadingIcon = {
                    if (selectedStatus == null) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            )
            TaskStatus.values().forEach { status ->
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { onStatusSelected(status) },
                    label = { Text(status.name.lowercase().replaceFirstChar { it.uppercaseChar() }) },
                    leadingIcon = {
                        if (selectedStatus == status) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Priority Filters
        Text(
            text = "Priority",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedPriority == null,
                onClick = { onPrioritySelected(null) },
                label = { Text("All") },
                leadingIcon = {
                    if (selectedPriority == null) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            )
            TaskPriority.values().forEach { priority ->
                FilterChip(
                    selected = selectedPriority == priority,
                    onClick = { onPrioritySelected(priority) },
                    label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercaseChar() }) },
                    leadingIcon = {
                        if (selectedPriority == priority) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (priority) {
                            TaskPriority.HIGH -> MaterialTheme.colorScheme.errorContainer
                            TaskPriority.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
                            TaskPriority.LOW -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                )
            }
        }
    }
}