package com.example.ecosync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ecosync.data.DestCategory
import com.example.ecosync.data.DestinationRepo
import com.example.ecosync.ui.widgets.ProgressBar
import com.example.ecosync.viewmodel.StepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyScreen(vm: StepViewModel = StepViewModel.shared) {
    val ui by vm.ui.collectAsState()

    var category by remember { mutableStateOf(DestCategory.Countries) }
    var expandedCat by remember { mutableStateOf(false) }
    var expandedDest by remember { mutableStateOf(false) }
    var selectedDestName by remember { mutableStateOf("France (Paris)") }

    val options = DestinationRepo.byCategory(category)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Journey", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        // Category dropdown
        ExposedDropdownMenuBox(
            expanded = expandedCat,
            onExpandedChange = { expandedCat = !expandedCat }
        ) {
            OutlinedTextField(
                value = category.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedCat,
                onDismissRequest = { expandedCat = false }
            ) {
                DestCategory.values().forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            category = cat
                            expandedCat = false
                            selectedDestName = DestinationRepo.byCategory(cat).firstOrNull()?.name
                                ?: selectedDestName
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Destination dropdown
        ExposedDropdownMenuBox(
            expanded = expandedDest,
            onExpandedChange = { expandedDest = !expandedDest }
        ) {
            OutlinedTextField(
                value = selectedDestName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Destination") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedDest,
                onDismissRequest = { expandedDest = false }
            ) {
                options.forEach { dest ->
                    DropdownMenuItem(
                        text = { Text(dest.name) },
                        onClick = {
                            selectedDestName = dest.name
                            expandedDest = false
                            vm.addOrSelectGoalByName(dest.name)
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Active goal card
        val active = ui.goals.firstOrNull { it.id == ui.selectedGoalId }
        if (active != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(active.name, style = MaterialTheme.typography.titleMedium)
                    Text("${active.progressKm.toInt()} km of ${active.targetKm} km")
                    ProgressBar("Progress", active.progressKm.toFloat(), active.targetKm.toFloat())
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // All goals
        Text("Your goals", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(ui.goals) { g ->
                Card(
                    onClick = { vm.selectGoal(g.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(g.name, style = MaterialTheme.typography.titleMedium)
                        Text("${g.progressKm.toInt()} km of ${g.targetKm} km")
                        ProgressBar("Progress", g.progressKm.toFloat(), g.targetKm.toFloat())
                    }
                }
            }
        }
    }
}