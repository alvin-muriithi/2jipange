package com.strathmore.groupworkmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.strathmore.groupworkmanager.R
import com.strathmore.groupworkmanager.data.model.TaskPriority
import com.strathmore.groupworkmanager.ui.viewmodel.GroupDetailViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Screen for adding a new task to a group. Uses the [GroupDetailViewModel] to
 * persist the new task.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    groupId: Int,
    viewModel: GroupDetailViewModel,
    onTaskAdded: () -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expandedPriority by remember { mutableStateOf(false) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var expandedMember by remember { mutableStateOf(false) }
    var selectedMemberId by remember { mutableStateOf<Int?>(null) }
    var deadlineText by remember { mutableStateOf("") }
    val members by viewModel.members.collectAsState(initial = emptyList())

    val priorities = TaskPriority.values().toList()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.add_task_button), fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(id = R.string.task_title_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.task_description_hint)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Priority dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPriority,
                onExpandedChange = { expandedPriority = !expandedPriority }
            ) {
                OutlinedTextField(
                    value = selectedPriority.name.lowercase().replaceFirstChar { it.uppercaseChar() },
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.task_priority_hint)) },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedPriority,
                    onDismissRequest = { expandedPriority = false }
                ) {
                    priorities.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority.name.lowercase().replaceFirstChar { it.uppercaseChar() }) },
                            onClick = {
                                selectedPriority = priority
                                expandedPriority = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Assigned member dropdown
            ExposedDropdownMenuBox(
                expanded = expandedMember,
                onExpandedChange = { expandedMember = !expandedMember }
            ) {
                OutlinedTextField(
                    value = members.find { it.id == selectedMemberId }?.memberName ?: "Unassigned",
                    onValueChange = {},
                    label = { Text("Assigned to") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMember) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedMember,
                    onDismissRequest = { expandedMember = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Unassigned") },
                        onClick = {
                            selectedMemberId = null
                            expandedMember = false
                        }
                    )
                    members.forEach { member ->
                        DropdownMenuItem(
                            text = { Text(member.memberName) },
                            onClick = {
                                selectedMemberId = member.id
                                expandedMember = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = deadlineText,
                onValueChange = { deadlineText = it },
                label = { Text(stringResource(id = R.string.task_deadline_hint) + " (yyyy-mm-dd)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Parse date string
                    val deadlineMillis = if (deadlineText.isNotBlank()) {
                        try {
                            val date = LocalDate.parse(deadlineText, DateTimeFormatter.ISO_LOCAL_DATE)
                            date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        } catch (e: Exception) {
                            null
                        }
                    } else null
                    viewModel.addTask(
                        title = title,
                        description = description,
                        assignedToMemberId = selectedMemberId,
                        priority = selectedPriority,
                        deadline = deadlineMillis
                    )
                    onTaskAdded()
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.save_button))
            }
        }
    }
}