package com.example.ecosync.ui.screens

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecosync.viewmodel.StepViewModel

@Composable
fun ScanScreen(vm: StepViewModel = StepViewModel.shared) {
    var hasCameraPermission by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var confirmedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showDetails by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        capturedBitmap = bmp
        showDetails = false
    }

    // Ask for camera permission on first load
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("EcoSync Scanner", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        val ui by vm.ui.collectAsState()
        Text("Points: ${ui.points}", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                if (hasCameraPermission) {
                    cameraLauncher.launch(null)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text("Open Camera")
            }

            Button(
                enabled = capturedBitmap != null,
                onClick = {
                    confirmedBitmap = capturedBitmap
                    vm.addBonusPoints(2)   // ✅ add 2 points to EcoSync
                    showDetails = true
                }
            ) {
                Text("Confirm Scan")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Preview before confirm
        if (capturedBitmap != null && !showDetails) {
            Text("Preview (not confirmed yet):")
            Spacer(Modifier.height(8.dp))
            Image(
                bitmap = capturedBitmap!!.asImageBitmap(),
                contentDescription = "Captured preview",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        // After confirm
        if (confirmedBitmap != null && showDetails) {
            Text("Scanned Image:")
            Spacer(Modifier.height(8.dp))
            Image(
                bitmap = confirmedBitmap!!.asImageBitmap(),
                contentDescription = "Confirmed image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            Spacer(Modifier.height(16.dp))
            PepsiCanDetailsCard()
        }

        if (capturedBitmap == null && confirmedBitmap == null) {
            Text("Tap Open Camera, take a photo of the Pepsi can, then press Confirm Scan.")
        }
    }
}

@Composable
private fun PepsiCanDetailsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Item: Pepsi Can", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Material: Aluminium")
            Text("Recyclable: Yes")
            Text("Bin: Yellow Lid (Recycling)")
            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))
            Text("Environmental Facts:", fontWeight = FontWeight.SemiBold)
            Text("• Recycling one aluminium can saves enough energy to run a TV for hours.")
            Text("• Aluminium is infinitely recyclable with minimal quality loss.")
            Text("• Rinse and crush the can to save space.")
            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))
            Text("EcoSync Reward:", fontWeight = FontWeight.SemiBold)
            Text("• +2 points added for a successful scan.")
        }
    }
}