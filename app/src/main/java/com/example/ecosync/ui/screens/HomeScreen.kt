package com.example.ecosync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ecosync.ui.widgets.ProgressBar
import com.example.ecosync.viewmodel.StepViewModel

private const val CALORIES_PER_STEP = 0.04  // rough avg, tweak later in Profile

@Composable
fun HomeScreen(vm: StepViewModel = StepViewModel.shared) {
    val ui by vm.ui.collectAsState()

    val goal = ui.dailyGoalSteps.coerceAtLeast(1)
    val progress = (ui.stepsToday.toFloat() / goal).coerceIn(0f, 1f)
    val km = ui.km
    val co2 = ui.co2Kg
    val calories = ui.stepsToday * CALORIES_PER_STEP

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress circle header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // ring
            CircularProgressIndicator(
                progress = { progress },
                strokeWidth = 12.dp,
                modifier = Modifier.size(220.dp)
            )
            // numbers
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = ui.stepsToday.toString(),
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "of $goal steps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Stat("Cal", "%.0f".format(calories))
            Stat("km", "%.2f".format(km))
            Stat("CO₂ kg", "%.2f".format(co2))
        }

        Spacer(Modifier.height(24.dp))
        // Keep the goal bar as a secondary visual
        ProgressBar(label = "Daily goal", value = ui.stepsToday.toFloat(), max = goal.toFloat())

        Spacer(Modifier.height(24.dp))
        // Demo controls (we’ll replace with ESP32 later)
        Button(onClick = { vm.toggleAuto() }, modifier = Modifier.fillMaxWidth()) {
            Text(if (ui.isAuto) "Pause mock stream" else "Start mock stream")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { vm.addSteps(100) }, modifier = Modifier.fillMaxWidth()) {
            Text("Add 100 steps")
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}