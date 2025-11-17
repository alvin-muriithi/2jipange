package com.strathmore.groupworkmanager.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.strathmore.groupworkmanager.data.repository.GroupRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Screen for scanning QR codes to join groups
 * Uses the ZXing barcode scanner library
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QRScanScreen(
    onBack: () -> Unit,
    onGroupScanned: (Int) -> Unit,
    groupRepository: GroupRepository
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdGroupName by remember { mutableStateOf("") }
    var createdGroupId by remember { mutableIntStateOf(0) }

    // Camera permission state
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // QR Scanner launcher
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents != null) {
            isProcessing = true
            errorMessage = null

            coroutineScope.launch {
                try {
                    // Parse the scanned QR code data
                    val shareData = Json.decodeFromString<GroupShareData>(result.contents)

                    // Create the group with default members (just the current user)
                    val groupId = groupRepository.createGroup(
                        groupName = shareData.groupName,
                        courseName = shareData.courseName,
                        lecturerName = shareData.lecturerName,
                        members = emptyList() // User can add members later
                    ).toInt()

                    createdGroupName = shareData.groupName
                    createdGroupId = groupId
                    showSuccessDialog = true
                    isProcessing = false
                } catch (e: Exception) {
                    errorMessage = "Failed to process QR code: ${e.message}"
                    isProcessing = false
                }
            }
        }
    }

    // Function to launch scanner
    fun launchScanner() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Scan a group QR code")
            setCameraId(0) // Use rear camera
            setBeepEnabled(true)
            setBarcodeImageEnabled(false)
            setOrientationLocked(true)
        }
        scanLauncher.launch(options)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Code", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Scan to Join Group",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Point your camera at a group QR code to join",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                when {
                    // Show loading indicator while processing
                    isProcessing -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Processing QR code...")
                    }

                    // Show error message if any
                    errorMessage != null -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                errorMessage = null
                                if (cameraPermissionState.status.isGranted) {
                                    launchScanner()
                                } else {
                                    cameraPermissionState.launchPermissionRequest()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Try Again")
                        }
                    }

                    // Show scan button based on permission state
                    else -> {
                        when {
                            cameraPermissionState.status.isGranted -> {
                                Button(
                                    onClick = { launchScanner() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Start Scanning")
                                }
                            }

                            cameraPermissionState.status.shouldShowRationale -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Camera permission is needed to scan QR codes. Please grant permission to continue.",
                                            modifier = Modifier.padding(16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { cameraPermissionState.launchPermissionRequest() },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Grant Permission")
                                    }
                                }
                            }

                            else -> {
                                Button(
                                    onClick = { cameraPermissionState.launchPermissionRequest() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Grant Camera Permission")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Success!") },
            text = { Text("You've successfully joined \"$createdGroupName\"") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onGroupScanned(createdGroupId)
                    }
                ) {
                    Text("View Group")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onBack()
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}