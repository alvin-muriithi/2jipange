package com.strathmore.groupworkmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.strathmore.groupworkmanager.R
import com.strathmore.groupworkmanager.data.repository.GroupRepository
import kotlinx.coroutines.launch

/**
 * Screen for creating a new group. Allows the user to enter group details and
 * a list of members. On save, the repository is invoked to persist the data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCreationScreen(
    onBack: () -> Unit,
    onGroupCreated: (Int) -> Unit,
    groupRepository: GroupRepository
) {
    var groupName by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }
    var lecturerName by remember { mutableStateOf("") }
    var memberNameInput by remember { mutableStateOf("") }
    var memberRoleInput by remember { mutableStateOf("") }
    val members = remember { mutableStateListOf<Pair<String, String>>() }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.create_group_button),
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors()
        )
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text(stringResource(id = R.string.group_name_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = courseName,
                onValueChange = { courseName = it },
                label = { Text(stringResource(id = R.string.course_name_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = lecturerName,
                onValueChange = { lecturerName = it },
                label = { Text(stringResource(id = R.string.lecturer_name_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Members",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = memberNameInput,
                    onValueChange = { memberNameInput = it },
                    label = { Text("Name") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = memberRoleInput,
                    onValueChange = { memberRoleInput = it },
                    label = { Text("Role") },
                    modifier = Modifier.weight(1f)
                )
            }
            TextButton(
                onClick = {
                    if (memberNameInput.isNotBlank()) {
                        members.add(memberNameInput to memberRoleInput)
                        memberNameInput = ""
                        memberRoleInput = ""
                    }
                },
                enabled = memberNameInput.isNotBlank(),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(id = R.string.add_member_button))
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                items(members) { (name, role) ->
                    MemberChip(name = name, role = role) {
                        members.remove(name to role)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        val id = groupRepository.createGroup(
                            groupName = groupName,
                            courseName = courseName,
                            lecturerName = lecturerName.takeIf { it.isNotBlank() },
                            members = members.toList()
                        ).toInt()
                        onGroupCreated(id)
                    }
                },
                enabled = groupName.isNotBlank() && courseName.isNotBlank() && members.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.save_button))
            }
        }
    }
}

@Composable
private fun MemberChip(name: String, role: String, onRemove: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                if (role.isNotBlank()) {
                    Text(text = role, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove member")
            }
        }
    }
}