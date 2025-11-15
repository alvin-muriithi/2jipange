package com.strathmore.groupworkmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.strathmore.groupworkmanager.R
import com.strathmore.groupworkmanager.data.model.CommentEntity
import com.strathmore.groupworkmanager.ui.viewmodel.GroupDetailViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Screen showing the comments (chat) for a group.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    viewModel: GroupDetailViewModel,
    onBack: () -> Unit
) {
    val comments by viewModel.comments.collectAsState(initial = emptyList())
    var message by remember { mutableStateOf("") }
    val formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm")

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.view_chat_button), fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            items(comments) { comment ->
                CommentItem(comment = comment, formatter = formatter)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (message.isNotBlank()) {
                    viewModel.addComment(message)
                    message = ""
                }
            }) {
                Text(text = stringResource(id = R.string.send_comment_button))
            }
        }
    }
}

@Composable
private fun CommentItem(comment: CommentEntity, formatter: DateTimeFormatter) {
    val dateTime = Instant.ofEpochMilli(comment.timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = comment.message, style = MaterialTheme.typography.bodyMedium)
            Text(text = formatter.format(dateTime), style = MaterialTheme.typography.bodySmall)
        }
    }
}