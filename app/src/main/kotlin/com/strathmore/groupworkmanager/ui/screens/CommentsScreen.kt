package com.strathmore.groupworkmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.ArrowBack

/**
 * Screen showing the comments (chat) for a group.
 */
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
        LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
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
    val dateTime = Instant.ofEpochMilli(comment.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
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