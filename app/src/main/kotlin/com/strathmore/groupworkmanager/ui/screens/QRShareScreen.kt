package com.strathmore.groupworkmanager.ui.screens

import androidx.compose.runtime.collectAsState
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.strathmore.groupworkmanager.data.model.GroupEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Data class for sharing group information via QR code
 */
@Serializable
data class GroupShareData(
    val groupName: String,
    val courseName: String,
    val lecturerName: String?
)

/**
 * Screen that displays a QR code for sharing group information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRShareScreen(
    group: GroupEntity?,
    onBack: () -> Unit
) {
    val qrBitmap = remember(group) {
        group?.let { generateQRCode(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Group", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            group?.let {
                Text(
                    text = "Scan to join:",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it.groupName,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = it.courseName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                qrBitmap?.let { bitmap ->
                    Card(
                        modifier = Modifier.size(300.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "QR Code",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Other members can scan this code to get group details",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } ?: run {
                Text("Group not found")
            }
        }
    }
}

/**
 * Generates a QR code bitmap from group data
 */
private fun generateQRCode(group: GroupEntity): Bitmap? {
    return try {
        val shareData = GroupShareData(
            groupName = group.groupName,
            courseName = group.courseName,
            lecturerName = group.lecturerName
        )
        val json = Json.encodeToString(shareData)

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(json, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}