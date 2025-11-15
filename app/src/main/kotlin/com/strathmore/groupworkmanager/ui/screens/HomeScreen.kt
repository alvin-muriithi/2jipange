package com.strathmore.groupworkmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.strathmore.groupworkmanager.R
import com.strathmore.groupworkmanager.data.model.GroupEntity
import com.strathmore.groupworkmanager.ui.viewmodel.HomeViewModel

/**
 * Displays the home dashboard, showing all groups and quick statistics.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onCreateGroup: () -> Unit,
    onGroupSelected: (Int) -> Unit
) {
    val groups by viewModel.groups.collectAsState(initial = emptyList())
    val totalGroups by viewModel.totalGroups.collectAsState(initial = 0)
    val pendingTasks by viewModel.pendingTasks.collectAsState(initial = 0)
    val deadlinesToday by viewModel.deadlinesToday.collectAsState(initial = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Quick stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "Groups",
                value = totalGroups.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Pending",
                value = pendingTasks.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Due Today",
                value = deadlinesToday.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCreateGroup, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.create_group_button))
        }
        Spacer(modifier = Modifier.height(16.dp))

        // List of groups
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(groups) { group ->
                GroupItem(group = group, onClick = { onGroupSelected(group.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GroupItem(group: GroupEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = group.groupName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = group.courseName, style = MaterialTheme.typography.bodyMedium)
            group.lecturerName?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Lecturer: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}