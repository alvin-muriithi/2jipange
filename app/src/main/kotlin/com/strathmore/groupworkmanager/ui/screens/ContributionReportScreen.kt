package com.strathmore.groupworkmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.strathmore.groupworkmanager.data.model.ContributionEntity
import com.strathmore.groupworkmanager.data.model.MemberEntity
import com.strathmore.groupworkmanager.data.model.TaskEntity
import com.strathmore.groupworkmanager.data.model.TaskStatus
import com.strathmore.groupworkmanager.ui.viewmodel.GroupDetailViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Screen for viewing and managing member contributions in a group.
 * Displays contribution statistics and detailed contribution logs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionReportScreen(
    viewModel: GroupDetailViewModel,
    onBack: () -> Unit
) {
    val members by viewModel.members.collectAsState(initial = emptyList())
    val contributions by viewModel.contributions.collectAsState(initial = emptyList())
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())

    var selectedMemberId by remember { mutableStateOf<Int?>(null) }
    var showAddContributionDialog by remember { mutableStateOf(false) }

    // Calculate contribution statistics
    val contributionStats = remember(members, contributions, tasks) {
        members.map { member ->
            val memberContributions = contributions.filter { it.memberId == member.id }
            val completedTasks = tasks.filter {
                it.assignedToMemberId == member.id && it.status == TaskStatus.COMPLETED
            }

            MemberContributionStats(
                member = member,
                totalContributions = memberContributions.size,
                totalHours = memberContributions.sumOf { it.hoursSpent },
                completedTasks = completedTasks.size,
                contributions = memberContributions
            )
        }.sortedByDescending { it.totalHours }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contribution Report", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddContributionDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Contribution")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Cards
            item {
                ContributionSummaryCards(stats = contributionStats)
            }

            // Member Contributions
            item {
                Text(
                    text = "Team Members",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(contributionStats) { stat ->
                MemberContributionCard(
                    stat = stat,
                    isExpanded = selectedMemberId == stat.member.id,
                    onToggleExpand = {
                        selectedMemberId = if (selectedMemberId == stat.member.id) null else stat.member.id
                    },
                    onAddContribution = {
                        selectedMemberId = stat.member.id
                        showAddContributionDialog = true
                    }
                )
            }

            if (contributionStats.isEmpty()) {
                item {
                    EmptyContributionsState()
                }
            }
        }
    }

    // Add Contribution Dialog
    if (showAddContributionDialog) {
        AddContributionDialog(
            members = members,
            selectedMemberId = selectedMemberId,
            onDismiss = {
                showAddContributionDialog = false
                selectedMemberId = null
            },
            onConfirm = { memberId, description, hours ->
                viewModel.addContribution(memberId, description, hours)
                showAddContributionDialog = false
                selectedMemberId = null
            }
        )
    }
}

/**
 * Data class to hold member contribution statistics
 */
data class MemberContributionStats(
    val member: MemberEntity,
    val totalContributions: Int,
    val totalHours: Double,
    val completedTasks: Int,
    val contributions: List<ContributionEntity>
)

@Composable
private fun ContributionSummaryCards(stats: List<MemberContributionStats>) {
    val totalHours = stats.sumOf { it.totalHours }
    val totalContributions = stats.sumOf { it.totalContributions }
    val avgHoursPerMember = if (stats.isNotEmpty()) totalHours / stats.size else 0.0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Total Hours",
            value = String.format("%.1f", totalHours),
            icon = Icons.Default.Timer,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Avg Hours",
            value = String.format("%.1f", avgHoursPerMember),
            icon = Icons.Default.BarChart,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    SummaryCard(
        title = "Total Contributions Logged",
        value = totalContributions.toString(),
        icon = Icons.Default.Assignment,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MemberContributionCard(
    stat: MemberContributionStats,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onAddContribution: () -> Unit
) {
    val maxHours = 100.0 // Adjust based on your project scope
    val progress = (stat.totalHours / maxHours).coerceIn(0.0, 1.0).toFloat()

    Card(
        onClick = onToggleExpand,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Member Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stat.member.memberName.first().uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stat.member.memberName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stat.member.role,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    label = "Hours",
                    value = String.format("%.1f", stat.totalHours),
                    icon = Icons.Default.Timer
                )
                StatItem(
                    label = "Logs",
                    value = stat.totalContributions.toString(),
                    icon = Icons.Default.Description
                )
                StatItem(
                    label = "Tasks",
                    value = stat.completedTasks.toString(),
                    icon = Icons.Default.CheckCircle
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            // Expanded Content
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Contribution Log",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onAddContribution) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (stat.contributions.isEmpty()) {
                        Text(
                            text = "No contributions logged yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        stat.contributions.sortedByDescending { it.timestamp }.take(5).forEach { contribution ->
                            ContributionLogItem(contribution)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (stat.contributions.size > 5) {
                            Text(
                                text = "and ${stat.contributions.size - 5} more...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ContributionLogItem(contribution: ContributionEntity) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
    val dateTime = Instant.ofEpochMilli(contribution.timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contribution.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatter.format(dateTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Chip(text = "${contribution.hoursSpent}h")
        }
    }
}

@Composable
private fun Chip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyContributionsState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Assignment,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No contributions yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start tracking team contributions to ensure fair grading",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddContributionDialog(
    members: List<MemberEntity>,
    selectedMemberId: Int?,
    onDismiss: () -> Unit,
    onConfirm: (memberId: Int, description: String, hours: Double) -> Unit
) {
    var selectedMember by remember { mutableStateOf(selectedMemberId) }
    var description by remember { mutableStateOf("") }
    var hoursText by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Contribution") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Member Selection
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = members.find { it.id == selectedMember }?.memberName ?: "Select member",
                        onValueChange = {},
                        label = { Text("Team Member") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        members.forEach { member ->
                            DropdownMenuItem(
                                text = { Text(member.memberName) },
                                onClick = {
                                    selectedMember = member.id
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("What was done?") },
                    placeholder = { Text("e.g., Implemented login feature") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Hours
                OutlinedTextField(
                    value = hoursText,
                    onValueChange = { hoursText = it },
                    label = { Text("Hours Spent") },
                    placeholder = { Text("e.g., 2.5") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hours = hoursText.toDoubleOrNull()
                    if (selectedMember != null && description.isNotBlank() && hours != null && hours > 0) {
                        onConfirm(selectedMember!!, description, hours)
                    }
                },
                enabled = selectedMember != null && description.isNotBlank() &&
                        hoursText.toDoubleOrNull()?.let { it > 0 } == true
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}