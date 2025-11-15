package com.strathmore.groupworkmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.strathmore.groupworkmanager.R
import com.strathmore.groupworkmanager.data.model.MemberEntity
import com.strathmore.groupworkmanager.data.model.TaskEntity
import com.strathmore.groupworkmanager.data.model.TaskStatus
import com.strathmore.groupworkmanager.ui.viewmodel.GroupDetailViewModel

/**
 * Screen that shows details of a specific group, including its tasks and members.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    viewModel: GroupDetailViewModel,
    onBack: () -> Unit,
    onAddTask: () -> Unit,
    onViewComments: () -> Unit
) {
    val group by viewModel.group.collectAsState(initial = null)
    val members by viewModel.members.collectAsState(initial = emptyList())
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group?.groupName ?: "Group Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTask,
                text = { Text(stringResource(id = R.string.add_task_button)) },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) }
            )
        }
    ) { paddingValues ->
        group?.let { groupEntity ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = groupEntity.groupName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(text = groupEntity.courseName, style = MaterialTheme.typography.bodyMedium)
                groupEntity.lecturerName?.let {
                    Text(text = "Lecturer: $it", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                val completed = tasks.count { it.status == TaskStatus.COMPLETED }
                val progress = if (tasks.isNotEmpty()) completed.toFloat() / tasks.size else 0f
                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
                Text(
                    text = "$completed of ${tasks.size} tasks completed",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Members list
                Text(
                    text = "Members",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                members.forEach { member ->
                    Text(
                        text = "• ${member.memberName} (${member.role})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Tasks list
                Text(
                    text = "Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LazyColumn(modifier = Modifier.heightIn(min = 0.dp, max = 400.dp)) {
                    items(tasks) { task ->
                        TaskItem(task = task, members = members) { newStatus ->
                            viewModel.markTaskStatus(task, newStatus)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onViewComments, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.view_chat_button))
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: TaskEntity,
    members: List<MemberEntity>,
    onStatusChange: (TaskStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            val assignee = members.find { it.id == task.assignedToMemberId }
            assignee?.let {
                Text(
                    text = "Assigned to: ${it.memberName}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (task.deadline != null) {
                val dateString = java.time.Instant.ofEpochMilli(task.deadline)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                    .toString()
                Text(text = "Deadline: $dateString", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Status buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TaskStatus.values().forEach { status ->
                    val selected = task.status == status
                    FilterChip(
                        selected = selected,
                        onClick = { onStatusChange(status) },
                        label = {
                            Text(
                                text = status.name
                                    .lowercase()
                                    .replaceFirstChar { it.uppercaseChar() }
                            )
                        }
                    )
                }
            }
        }
    }
}