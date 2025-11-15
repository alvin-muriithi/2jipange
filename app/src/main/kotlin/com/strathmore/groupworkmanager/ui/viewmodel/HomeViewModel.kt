package com.strathmore.groupworkmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strathmore.groupworkmanager.data.repository.GroupRepository
import com.strathmore.groupworkmanager.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * ViewModel for the home dashboard. Exposes flows for groups and summary
 * statistics such as the number of groups and tasks due or pending.
 */
class HomeViewModel(
    private val groupRepository: GroupRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {
    val groups = groupRepository.getAllGroups()

    val totalGroups: Flow<Int> = groups.map { it.size }

    // Number of tasks that are not completed across all groups
    val pendingTasks: Flow<Int> = taskRepository.getAllTasks().map { tasks ->
        tasks.count { it.status.name != "COMPLETED" }
    }

    // Number of tasks due today (deadline within the current date)
    val deadlinesToday: Flow<Int> = taskRepository.getAllTasks().map { tasks ->
        val today = LocalDate.now(ZoneId.of("Africa/Nairobi"))
        tasks.count { task ->
            task.deadline?.let { millis ->
                val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Africa/Nairobi")).toLocalDate()
                date.isEqual(today)
            } ?: false
        }
    }
}