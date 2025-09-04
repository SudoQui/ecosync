package com.example.ecosync.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecosync.viewmodel.EcoViewModel
import kotlin.math.abs

@Composable
fun EnergyScreen(vm: EcoViewModel = EcoViewModel.shared) {
    val ui by vm.ui.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Header with connect button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Energy", style = MaterialTheme.typography.headlineMedium)
            if (!ui.connected) {
                Button(onClick = { vm.connect() }) { Text("Connect") }
            } else {
                OutlinedButton(onClick = { vm.disconnect() }) { Text("Disconnect") }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Net banner
        NetBanner(
            solar = ui.solarWatts,
            house = ui.houseWatts,
            batterySoc = ui.batterySoc,
            net = vm.netWatts()
        )

        // Impact line
        Spacer(Modifier.height(8.dp))
        val co2 = vm.co2SavedKgToday()
        Text(
            "Estimated CO₂ avoided today ${"%,.2f".format(co2)} kg",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(12.dp))

        // Solar and Battery tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(title = "Solar", value = "${ui.solarWatts} W", modifier = Modifier.weight(1f))
            StatCard(title = "Battery", value = "${ui.batterySoc}%", modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        // Appliances grid
        ApplianceGrid(
            items = ui.appliances,
            onToggle = { vm.toggle(it) }
        )
    }
}

@Composable
private fun NetBanner(solar: Int, house: Int, batterySoc: Int, net: Int) {
    val exporting = net >= 0
    val title = if (exporting) "Exporting to grid" else "Importing from grid"
    val subtitle = "Solar ${solar} W   Home ${house} W   Battery ${batterySoc}%"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (exporting)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "Net ${abs(net)} W",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(subtitle)
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ApplianceGrid(
    items: List<EcoViewModel.Appliance>,
    onToggle: (id: String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { a ->
            ApplianceCard(
                name = a.name,
                watts = a.watts,
                kwh = a.kWhToday,
                isOn = a.isOn,
                onToggle = { onToggle(a.id) }
            )
        }
    }
}

@Composable
private fun ApplianceCard(
    name: String,
    watts: Int,
    kwh: Double,
    isOn: Boolean,
    onToggle: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text("${watts} W now")
            Text("${"%,.2f".format(kwh)} kWh today", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (isOn) "On" else "Off")
                Switch(checked = isOn, onCheckedChange = { onToggle() })
            }
        }
    }
}
