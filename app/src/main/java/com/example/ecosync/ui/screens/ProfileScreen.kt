package com.example.ecosync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ecosync.viewmodel.StepViewModel

@Composable
fun ProfileScreen(vm: StepViewModel = StepViewModel.shared) {
    val ui by vm.ui.collectAsState()

    var goalText by remember(ui.dailyGoalSteps) { mutableStateOf(ui.dailyGoalSteps.toString()) }
    var showLogout by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Top bar with title and a larger avatar on the right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Profile", style = MaterialTheme.typography.headlineMedium)
            Box(
                modifier = Modifier
                    .size(56.dp) // bigger icon
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("U", style = MaterialTheme.typography.titleLarge)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Daily goal editor
        OutlinedTextField(
            value = goalText,
            onValueChange = { goalText = it.filter { c -> c.isDigit() }.take(6) },
            label = { Text("Daily goal steps") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { vm.setDailyGoal(goalText.toIntOrNull() ?: ui.dailyGoalSteps) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save goal") }

        Spacer(Modifier.height(24.dp))

        // Friends section (no add inputs, just a featured card for Mustafa)
        Text("Friends", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        // Use the friend from state if present, otherwise show a default Mustafa card
        val mustafa = ui.friends.firstOrNull { it.name.equals("Mustafa", ignoreCase = true) }
            ?: StepViewModel.Friend("Mustafa", 9800)

        // Bigger card with personal icon on the left
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mustafa avatar
                Box(
                    modifier = Modifier
                        .size(64.dp) // bigger avatar here
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("M", style = MaterialTheme.typography.titleLarge)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(mustafa.name, style = MaterialTheme.typography.titleMedium)
                    Text("%,d steps".format(mustafa.steps))
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Log out button at the bottom
        Button(
            onClick = { showLogout = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text("Log out")
        }
    }

    // Simple mock dialog for logout
    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            confirmButton = {
                TextButton(onClick = { showLogout = false }) { Text("OK") }
            },
            title = { Text("Logged out") },
            text = { Text("This is a mock action for now.") }
        )
    }
}